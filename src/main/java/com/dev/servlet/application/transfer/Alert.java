package com.dev.servlet.application.transfer;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Alert(String id, String status, String message, String createdAt) {
    public Alert(String status, String message) {
        this(UUID.randomUUID().toString(), status, message, OffsetDateTime.now().toString());
    }
}