package com.dev.servlet.service.internal;

import com.dev.servlet.domain.enumeration.MessageType;
import com.dev.servlet.infrastructure.messaging.Message;
import com.dev.servlet.infrastructure.messaging.interfaces.MessageService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Slf4j
@Singleton
@Named("emailSender")
public class EmailSenderImpl implements MessageService {

    private ExecutorService executor;
    private boolean smtpPrecheck = false;

    private String smtpHost;
    private String smtpPort;
    private String smtpUser;
    private String smtpPass;
    private String smtpFrom;

    @PostConstruct
    public void init() {
        smtpHost = System.getenv("SMTP_HOST");
        smtpPort = System.getenv("SMTP_PORT");
        smtpUser = System.getenv("SMTP_USER");
        smtpPass = System.getenv("SMTP_PASS");
        smtpFrom = System.getenv("SMTP_FROM");

        if (StringUtils.isBlank(smtpFrom)) {
            smtpFrom = "no-reply@localhost";
        }

        ThreadFactory factory = Thread.ofVirtual().name("email-sender-%d").factory();
        executor = Executors.newThreadPerTaskExecutor(factory);

        if (smtpHost != null && smtpPort != null) {
            executor.submit(() -> {
                if (!(smtpPrecheck = ensureConnection())) {
                    log.warn("SMTP precheck failed; email sending will be disabled until connectivity is restored.");
                } else {
                    log.info("SMTP precheck succeeded; email sending enabled.");
                }
            });
        }
    }

    @Override
    public void send(Message message) {
        MessageType messageType = MessageType.of(message.type().type);
        switch (messageType) {
            case WELCOME -> sendWelcome(message.toEmail());
            case CONFIRMATION -> sendConfirmation(message.toEmail(), message.link());
            case CHANGE_EMAIL -> sendChangeEmail(message.toEmail(), message.link());
            default -> log.warn("EmailSenderImpl: unknown message type '{}', skipping send.", message.type());
        }
    }

    private void sendChangeEmail(String email, String link) {
        String html = """
                <html>
                    <body>
                        <div>
                        <h2>Change Email Request</h2>
                        <p>Please confirm your email change request by clicking the link below:</p>
                        <p><a href="%s">Confirm Email Change</a></p>
                        <p>If you did not request this change, please ignore this email.</p>
                        </div>
                    </body>
                </html>
                """.formatted(link);
        String plain = "Please confirm your email change request.";
        String subject = "Confirm your email change";
        Properties props = defaultProperties();

        log.info("Sending change email to {}", email);
        sendEmail(email, props, subject, plain, html);
    }

    @Override
    public void sendConfirmation(String to, String link) {
        final String subject = "Sign Up Confirmation";
        if (!smtpPrecheck || smtpHost == null || smtpPort == null || smtpUser == null || smtpPass == null) {
            log.info("[EMAIL-DRYRUN] To={} Subject={} Body={}", to, subject, null);
            return;
        }

        executor.submit(() -> {
            String html = generateEmailConfirmationHtml(link);
            String plain = generateEmailConfirmationPlainText(link);
            Properties props = defaultProperties();
            sendEmail(to, props, subject, plain, html);
        });
    }

    @Override
    public void sendWelcome(String email) {
        final String subject = "Welcome!";
        if (!smtpPrecheck || smtpHost == null || smtpPort == null || smtpUser == null || smtpPass == null) {
            log.info("[EMAIL-DRYRUN] To={} Subject={}", email, subject);
            return;
        }

        executor.submit(() -> {
            String plain = generateEmailWelcomePlainText();
            String html = generateEmailWelcomeHtml();
            Properties props = defaultProperties();
            sendEmail(email, props, subject, plain, html);
        });
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

    private void sendEmail(String to, Properties props, String subject, String plain, String html) {
        try {
            log.info("Preparing SMTP session for host={} port={}", smtpHost, smtpPort);
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUser, smtpPass);
                }
            });

            log.debug("JavaMail Session created successfully");

            jakarta.mail.Message message;
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpFrom));
            message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setSentDate(new Date());

            MimeMultipart multipart = new MimeMultipart("alternative");

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(plain, "UTF-8");
            multipart.addBodyPart(textPart);

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(html, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);
            Transport.send(message);
            log.info("Confirmation email sent to {}", to);

        } catch (Exception e) {
            log.error("Failed to send confirmation email to {}: {}", to, e.getMessage(), e);
        }
    }

    private Properties defaultProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        return props;
    }

    private boolean ensureConnection() {
        try (Socket sock = new Socket()) {
            int port = Integer.parseInt(smtpPort);
            sock.connect(new InetSocketAddress(smtpHost, port), 5000);
            log.debug("SMTP {}:{} reachable", smtpHost, smtpPort);
        } catch (Exception sockEx) {
            log.error("SMTP host unreachable ({}:{}): {}", smtpHost, smtpPort, sockEx.getMessage());
            return false;
        }

        return true;
    }

    private String generateEmailConfirmationPlainText(String confirmation) {
        return """
                Hello,
                Please confirm your registration using the link below:
                %s
                The link expires in 1 hour.
                If you did not request this, please ignore this message.
                Support: no-reply@localhost
                """.formatted(confirmation);
    }

    private String generateEmailConfirmationHtml(String confirmationLink) {
        return """
                <html lang="en">
                  <head>
                    <meta charset="utf-8"/>
                    <style>
                      .btn { display:inline-block; padding:12px 22px; background:#667eea; color:#fff; text-decoration:none; border-radius:8px; }
                      .container { font-family: Arial, Helvetica, sans-serif; color:#111; line-height:1.4; }
                      .footer { color:#6b7280; font-size:13px; margin-top:18px; }
                    </style>
                  </head>
                  <body>
                    <div class="container">
                      <h2>Confirm your email</h2>
                      <p>Hello,</p>
                      <p>Click the button below to confirm your email. The link expires in 1 hour.</p>
                      <p><a class="btn" href="%s" target="_blank">Confirm email</a></p>
                      <p class="footer">
                        If the button doesn't work, copy and paste this link into your browser:<br/>
                        %s
                      </p>
                      <p class="footer">If you did not request this, simply ignore this email.</p>
                    </div>
                  </body>
                </html>
                """.formatted(confirmationLink, confirmationLink);
    }

    private String generateEmailWelcomePlainText() {
        return """
                Welcome to Our Service!
                
                We're excited to have you on board. Thank you for joining us!
                
                If you have any questions or need assistance, feel free to reach out to our support team.
                
                Best regards
                """;
    }

    private String generateEmailWelcomeHtml() {
        return """
                <html lang="en">
                  <head>
                    <meta charset="utf-8"/>
                    <style>
                      .container { font-family: Arial, Helvetica, sans-serif; color:#111; line-height:1.4; }
                    </style>
                  </head>
                  <body>
                    <div class="container">
                      <h2>Welcome to Our Service!</h2>
                      <p>We're excited to have you on board. Thank you for joining us!</p>
                      <p>If you have any questions or need assistance, feel free to reach out to our support team.</p>
                      <p>Best regards</p>
                    </div>
                  </body>
                </html>
                """;
    }
}
