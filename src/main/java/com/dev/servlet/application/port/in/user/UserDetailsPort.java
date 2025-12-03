package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.response.UserResponse;

public interface UserDetailsPort {
    UserResponse get(String userId, String auth) throws ApplicationException;
}