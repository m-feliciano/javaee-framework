package com.servletstack.application.port.in.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.ResendConfirmationRequest;

public interface ResendConfirmationUseCase {
    void resend(ResendConfirmationRequest request) throws AppException;
}

