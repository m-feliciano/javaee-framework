package com.dev.servlet.adapter.in.ws;

import com.dev.servlet.application.port.out.security.AuthCookiePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint(value = "/ws/alerts", configurator = WebSocketConfigurator.class)
public class AlertWebSocket {

    private static final Map<UUID, Session> sessions = new ConcurrentHashMap<>();
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuthCookiePort authCookiePort;

    private final ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        Map<String, Object> props = session.getUserProperties();
        HandshakeRequest req = (HandshakeRequest) props.get("jakarta.websocket.server.HandshakeRequest");

        UUID userId = authenticateUserFromHandshake(req);
        if (userId == null) {
            log.warn("AlertWebSocket: unauthorized connection attempt, closing session {}", session.getId());
            closeQuiet(session);
            return;
        }

        sessions.put(userId, session);

        log.debug("AlertWebSocket: connection opened for user {}", userId);
    }

    private UUID authenticateUserFromHandshake(HandshakeRequest req) {
        try {
            if (req == null) throw new Exception("No handshake request in WS session");

            List<String> cookie = req.getHeaders().get("cookie");
            if (cookie == null) throw new Exception("No cookies in WS request");

            String jwt = authCookiePort.getCookieFromList(cookie, authCookiePort.getAccessTokenCookieName());
            if (jwt == null) throw new Exception("Missing JWT token in WS cookies");

            UUID userId = validateJwtAndGetUser(jwt);
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

    public void push(UUID userId, Object payload) {
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

    private UUID validateJwtAndGetUser(String jwt) {
        try {
            return authenticationPort.extractUserId(jwt);
        } catch (Exception e) {
            log.error("AlertWebSocket: JWT validation error", e);
            return null;
        }
    }
}
