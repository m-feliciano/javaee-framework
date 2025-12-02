package com.dev.servlet.infrastructure.alert;

import com.dev.servlet.application.port.out.AuthCookiePort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.infrastructure.config.WebSocketConfigurator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint(value = "/ws/alerts", configurator = WebSocketConfigurator.class)
public class AlertWebSocket {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuthCookiePort authCookiePort;

    private final ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        Map<String, Object> props = session.getUserProperties();
        HandshakeRequest req = (HandshakeRequest) props.get("jakarta.websocket.server.HandshakeRequest");

        String userId = authenticateUserFromHandshake(req);
        if (userId == null) {
            log.warn("AlertWebSocket: unauthorized connection attempt, closing session {}", session.getId());
            closeQuiet(session);
            return;
        }

        sessions.put(userId, session);

        log.info("AlertWebSocket: connection opened for user {}", userId);
    }

    private String authenticateUserFromHandshake(HandshakeRequest req) {
        try {
            if (req == null) throw new Exception("No handshake request in WS session");

            List<String> cookieHeaders = req.getHeaders().get("cookie");
            if (cookieHeaders == null) throw new Exception("No cookies in WS request");

            String jwt = extractJwtFromCookies(cookieHeaders);
            if (jwt == null) throw new Exception("Missing JWT token in WS cookies");

            String userId = validateJwtAndGetUser(jwt);
            if (userId == null) throw new Exception("Invalid JWT token");

            return userId;
        } catch (Exception e) {
            log.error("AlertWebSocket: error during validation", e);
            return null;
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.values().removeIf(s -> s.equals(session));
    }

    @OnError
    public void onError(Session session, Throwable ex) {
        log.error("AlertWebSocket: error in session {}", session.getId(), ex);
    }

    public void push(String userId, Object payload) {
        log.debug("AlertWebSocket: pushing to user {} payload {}", userId, payload);

        Session ses = sessions.get(userId);
        if (ses != null && ses.isOpen()) {
            try {
                ses.getBasicRemote().sendText(mapper.writeValueAsString(payload));
            } catch (Exception e) {
                log.error("AlertWebSocket: error pushing to user {}", userId, e);
            }
        }
    }

    private void closeQuiet(Session session) {
        try {
            session.close();
        } catch (Exception ignored) {
        }
    }

    private String extractJwtFromCookies(List<String> cookies) {
        return authCookiePort.getTokenFromCookieList(cookies, authCookiePort.getAccessTokenCookieName());
    }

    private String validateJwtAndGetUser(String jwt) {
        try {
            return authenticationPort.extractUserId(jwt);
        } catch (Exception e) {
            log.error("AlertWebSocket: JWT validation error", e);
            return null;
        }
    }
}
