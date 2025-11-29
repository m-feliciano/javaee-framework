package com.dev.servlet.infrastructure.messaging;

import com.dev.servlet.domain.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record Message(String id,
                      MessageType type,
                      String toEmail,
                      String createdAt,
                      String link) {
    @JsonCreator
    public Message(@JsonProperty("id") String id,
                   @JsonProperty("type") MessageType type,
                   @JsonProperty("toEmail") String toEmail,
                   @JsonProperty("createdAt") String createdAt,
                   @JsonProperty("link") String link) {
        this.id = id;
        this.type = type;
        this.toEmail = toEmail;
        this.createdAt = createdAt;
        this.link = link;
    }

    public Message(MessageType type,
                   String email,
                   String createdAt,
                   String link) {
        this(UUID.randomUUID().toString(), type, email, createdAt, link);
    }
}
