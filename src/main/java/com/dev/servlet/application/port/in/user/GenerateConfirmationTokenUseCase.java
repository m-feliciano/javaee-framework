package com.dev.servlet.application.port.in.user;

import com.dev.servlet.domain.entity.User;

public interface GenerateConfirmationTokenUseCase {
    String generateFor(User user, Object body);
}
