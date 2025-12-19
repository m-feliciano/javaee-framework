package com.dev.servlet.adapter.in.web.frontcontroller;

import com.dev.servlet.adapter.in.web.builder.HtmlTemplate;
import com.dev.servlet.infrastructure.utils.URIUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.text.MessageFormat;

@Slf4j
@RequestScoped
public class ErrorResponseWriter {

    public void write(HttpServletRequest req, HttpServletResponse resp, int status, String message) {
        resp.setStatus(status);
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");


        String statusMessage = URIUtils.getErrorMessage(status);
        String accept = req.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            resp.setContentType("application/json");

            String json = "{" +
                          "\"status\":" + status + "," +
                          "\"error\":\"" + statusMessage + "\"," +
                          "\"message\":\"" + message + "\"" +
                          "}";
            try (PrintWriter writer = resp.getWriter()) {
                writer.write(json);
                writer.flush();
            } catch (Exception e) {
                String cause = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                log.error("Error writing JSON response: {}", cause);
            }
            return;
        }

        String funnyGif = "cat_error404.gif";
        String image = MessageFormat.format("{0}/resources/assets/images/{1}", req.getContextPath(), funnyGif);

        String html = HtmlTemplate.newBuilder()
                .error(status)
                .subTitle(statusMessage)
                .message(message)
                .image(image)
                .build();

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(html);
            writer.flush();
        } catch (Exception e) {
            String cause = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            log.error("Error writing response: {}", cause);
        }
    }
}
