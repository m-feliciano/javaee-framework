package com.dev.servlet.application.port.in.user;

import com.dev.servlet.domain.entity.User;

public interface GenerateConfirmationTokenUseCasePort {
    String createTokenForUser(User user, Object body);
}
