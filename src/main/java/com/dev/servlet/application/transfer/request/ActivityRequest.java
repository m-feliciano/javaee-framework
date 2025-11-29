package com.dev.servlet.application.transfer.request;

import com.dev.servlet.domain.entity.User;

public record ActivityRequest(String id, String action, User user) {
}
