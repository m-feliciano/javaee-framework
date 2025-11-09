package com.dev.servlet.domain.request;

import com.dev.servlet.domain.model.User;

public record ActivityRequest(String id, String action, User user) {
}
