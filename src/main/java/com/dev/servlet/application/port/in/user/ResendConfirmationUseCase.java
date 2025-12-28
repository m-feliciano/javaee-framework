package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.ResendConfirmationRequest;

public interface ResendConfirmationUseCase {
    void resend(ResendConfirmationRequest request) throws AppException;
}

