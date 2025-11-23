package com.dev.servlet.infrastructure.messaging;

public record Message(String userId,
                      String type,
                      String email,
                      String createdAt,
                      String link) {
}