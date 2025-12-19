package com.dev.servlet.adapter.in.web.frontcontroller;

import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.Request;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;

@Slf4j
@RequestScoped
public class ResponseWriter {

    public void write(HttpServletRequest req,
                      HttpServletResponse resp,
                      Request request,
                      IHttpResponse<?> response) throws Exception {

        log.trace("write(req={}, resp={}, request={}, response={})", req, resp, request, response);

        String header = req.getHeader("Accept");
        if (header != null && header.contains("application/json")) {
            writeJson(resp, response);
        } else {
            handleNavigation(req, resp, response);
        }
    }

    private void writeJson(HttpServletResponse resp, IHttpResponse<?> response) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String json = CloneUtil.toJson(response.body());

        try (PrintWriter writer = resp.getWriter()) {
            writer.write(String.valueOf(json));
            writer.flush();
        } catch (Exception e) {
            String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            log.error("Error writing JSON response: {}", message);
        }
    }

    private void handleNavigation(HttpServletRequest req, HttpServletResponse resp, IHttpResponse<?> response) throws Exception {
        if (response.next() == null) return;

        String[] path = response.next().split(":");
        if (path.length != 2) throw new Exception("Cannot parse URL: " + response.next());

        String pathAction = path[0];
        String pathUrl = path[1];

        if ("forward".equalsIgnoreCase(pathAction)) {
            req.getRequestDispatcher("/WEB-INF/view/" + pathUrl).forward(req, resp);
        } else {
            resp.sendRedirect(pathUrl);
        }
    }
}