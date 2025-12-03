package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.ConfirmEmailRequest;

public interface ConfirmEmailPort {
    void confirm(ConfirmEmailRequest request) throws ApplicationException;
}

