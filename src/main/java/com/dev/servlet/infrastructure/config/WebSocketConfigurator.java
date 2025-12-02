package com.dev.servlet.infrastructure.config;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

public class WebSocketConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec,
                                HandshakeRequest request,
                                HandshakeResponse response) {
        sec.getUserProperties().put("jakarta.websocket.server.HandshakeRequest", request);
    }

    @Override
    public <T> T getEndpointInstance(Class<T> clazz) {
        return CDI.current().select(clazz).get();
    }
}

