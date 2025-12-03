package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.ResendConfirmationRequest;

public interface ResendConfirmationPort {
    void resend(ResendConfirmationRequest request) throws ApplicationException;
}

