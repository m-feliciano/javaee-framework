package com.servletstack.application.port.in.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.ConfirmEmailRequest;

public interface ConfirmEmailUseCase {
    void confirm(ConfirmEmailRequest request) throws AppException;
}

