package com.dev.servlet.adapter.out.messaging.email;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmailTemplates {

    private static final String APP_NAME = "ServletStack";
    private static final String SUPPORT_EMAIL = "support@servletstack.io";
    private static final int LINK_EXPIRATION_MINUTES = 60;

    public static String subjectEmailConfirmation() {
        return "Confirm your email address";
    }

    public static String subjectChangeEmail() {
        return "Confirm your email change request";
    }

    public static String subjectWelcome() {
        return "Welcome to " + APP_NAME;
    }

    public static String emailConfirmationPlain(String link) {
        return """
            Thank you for creating an account with %s.

            To complete your registration, confirm your email address using the link below:
            %s

            This link expires in %d minutes.

            If you did not create this account, you can safely ignore this email.

            — %s Team
            %s
            """.formatted(APP_NAME, link, LINK_EXPIRATION_MINUTES, APP_NAME, SUPPORT_EMAIL);
    }

    public static String emailConfirmationHtml(String link) {
        return baseHtml("""
            <h2>Confirm your email address</h2>

            <p>
              Thank you for creating an account with <strong>%s</strong>.
            </p>

            <p>
              To complete your registration, confirm your email address:
            </p>

            <p>
              <a href="%s" class="btn">Confirm email</a>
            </p>

            <p class="hint">
              This link expires in %d minutes.
            </p>

            <p class="hint">
              If you did not create this account, no action is required.
            </p>
            """.formatted(APP_NAME, link, LINK_EXPIRATION_MINUTES));
    }

    public static String changeEmailPlain(String link) {
        return """
            We received a request to change the email address associated with your %s account.

            To confirm this change, open the link below:
            %s

            This link expires in %d minutes.

            If you did not request this change, no action is required and your email will remain unchanged.

            — %s Team
            %s
            """.formatted(APP_NAME, link, LINK_EXPIRATION_MINUTES, APP_NAME, SUPPORT_EMAIL);
    }

    public static String changeEmailHtml(String link) {
        return baseHtml("""
            <h2>Email change confirmation</h2>

            <p>
              We received a request to change the email address associated with your %s account.
            </p>

            <p>
              To confirm this change, click the link below:
            </p>

            <p>
              <a href="%s" class="btn">Confirm email change</a>
            </p>

            <p class="hint">
              This link expires in %d minutes.
            </p>

            <p class="hint">
              If you did not request this change, you can safely ignore this email.
            </p>
            """.formatted(APP_NAME, link, LINK_EXPIRATION_MINUTES));
    }

    /* ============================================================
       WELCOME EMAIL
       ============================================================ */

    public static String welcomePlain() {
        return """
            Welcome to %s.

            Your account has been successfully created and is ready to use.

            If you need help or have questions, contact us at:
            %s

            — %s Team
            """.formatted(APP_NAME, SUPPORT_EMAIL, APP_NAME);
    }

    public static String welcomeHtml() {
        return baseHtml("""
            <h2>Welcome to %s</h2>

            <p>
              Your account has been successfully created and is ready to use.
            </p>

            <p>
              If you need help or have questions, contact us at
              <a href="mailto:%s">%s</a>.
            </p>
            """.formatted(APP_NAME, SUPPORT_EMAIL, SUPPORT_EMAIL));
    }

    private static String baseHtml(String content) {
        return """
            <html lang="en">
              <head>
                <meta charset="utf-8"/>
                <style>
                  body {
                    font-family: Arial, Helvetica, sans-serif;
                    color: #111;
                    line-height: 1.5;
                  }
                  .btn {
                    display: inline-block;
                    padding: 12px 22px;
                    background: #2563eb;
                    color: #ffffff !important;
                    text-decoration: none;
                    border-radius: 6px;
                  }
                  .hint {
                    color: #555;
                    font-size: 14px;
                  }
                  .footer {
                    margin-top: 24px;
                    color: #777;
                    font-size: 12px;
                  }
                </style>
              </head>
              <body>
                %s
                <hr/>
                <div class="footer">
                  %s • <a href="mailto:%s">%s</a>
                </div>
              </body>
            </html>
            """.formatted(content, APP_NAME, SUPPORT_EMAIL, SUPPORT_EMAIL);
    }
}
