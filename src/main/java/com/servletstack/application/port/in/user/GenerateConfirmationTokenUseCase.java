package com.servletstack.application.port.in.user;

import com.servletstack.domain.entity.User;

public interface GenerateConfirmationTokenUseCase {
    String generateFor(User user, Object body);
}
