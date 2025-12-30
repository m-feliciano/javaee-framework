package com.servletstack.application.transfer.request;

import com.servletstack.domain.entity.User;

import java.util.UUID;

public record ActivityRequest(UUID id, String action, User user) {
}
