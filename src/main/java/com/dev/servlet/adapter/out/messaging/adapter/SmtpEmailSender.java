package com.dev.servlet.adapter.out.messaging.adapter;

import com.dev.servlet.adapter.out.messaging.Message;
import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.domain.enums.MessageType;
import com.dev.servlet.infrastructure.config.Properties;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

import static com.dev.servlet.adapter.out.messaging.email.EmailTemplates.changeEmailHtml;
import static com.dev.servlet.adapter.out.messaging.email.EmailTemplates.changeEmailPlain;
import static com.dev.servlet.adapter.out.messaging.email.EmailTemplates.emailConfirmationHtml;
import static com.dev.servlet.adapter.out.messaging.email.EmailTemplates.emailConfirmationPlain;
import static com.dev.servlet.adapter.out.messaging.email.EmailTemplates.subjectChangeEmail;
import static com.dev.servlet.adapter.out.messaging.email.EmailTemplates.subjectEmailConfirmation;
import static com.dev.servlet.adapter.out.messaging.email.EmailTemplates.subjectWelcome;
import static com.dev.servlet.adapter.out.messaging.email.EmailTemplates.welcomeHtml;
import static com.dev.servlet.adapter.out.messaging.email.EmailTemplates.welcomePlain;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
@Named("smtpEmailSender")
public class SmtpEmailSender implements MessagePort {
    private boolean smtpPrecheck = false;
    private java.util.Properties props;

    @PostConstruct
    public void init() {
        props = Properties.loadSmtpProperties();

        if (props.getProperty("mail.smtp.host") != null && props.getProperty("mail.smtp.port") != null) {
            if (!(smtpPrecheck = ensureConnection())) {
                log.warn("SMTP precheck failed; email sending will be disabled until connectivity is restored.");
            } else {
                log.info("SMTP precheck succeeded; email sending enabled.");
            }
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

    @Override
    public void sendConfirmation(String to, String link) {
        String subject = subjectEmailConfirmation();
        if (!isSmtpConfigSetUp()) {
            log.info("[EMAIL-DRYRUN] To={} Subject={} Body={}", to, subject, null);
            return;
        }

        sendEmail(to, subject, emailConfirmationPlain(link), emailConfirmationHtml(link));
    }

    private boolean isSmtpConfigSetUp() {
        return smtpPrecheck &&
               props.getProperty("mail.smtp.host") != null &&
               props.getProperty("mail.smtp.port") != null &&
               props.getProperty("mail.smtp.user") != null &&
               props.getProperty("mail.smtp.pass") != null;
    }

    @Override
    public void sendWelcome(String email) {
        String subject = subjectWelcome();
        if (!isSmtpConfigSetUp()) {
            log.info("[EMAIL-DRYRUN] To={} Subject={}", email, subject);
            return;
        }

        sendEmail(email, subject, welcomePlain(), welcomeHtml());
    }

    private void sendEmail(String to, String subject, String plain, String html) {
        try {
            String smtpHost = props.getProperty("mail.smtp.host");
            String smtpPort = props.getProperty("mail.smtp.port");
            String smtpUser = props.getProperty("mail.smtp.user");
            String smtpPass = props.getProperty("mail.smtp.pass");
            String smtpFrom = props.getProperty("mail.smtp.from.address");
            String smtpFromName = props.getProperty("smtp.from.name", "NoReply");

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
            message.setFrom(new InternetAddress(smtpFrom, smtpFromName));
            message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setSentDate(new Date());

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(plain, "UTF-8");

            MimeMultipart multipart = new MimeMultipart("alternative");
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

    private boolean ensureConnection() {
        String smtpHost = props.getProperty("mail.smtp.host");
        String smtpPort = props.getProperty("mail.smtp.port");

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

    private void sendChangeEmail(String email, String link) {
        sendEmail(email, subjectChangeEmail(), changeEmailPlain(link), changeEmailHtml(link));
    }
}
