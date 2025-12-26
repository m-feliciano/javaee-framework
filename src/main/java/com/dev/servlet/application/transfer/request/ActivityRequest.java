package com.dev.servlet.application.transfer.request;

import com.dev.servlet.domain.entity.User;

import java.util.UUID;

public record ActivityRequest(UUID id, String action, User user) {
}
