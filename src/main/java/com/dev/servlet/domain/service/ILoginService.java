package com.dev.servlet.domain.service;
import com.dev.servlet.domain.transfer.dto.UserDTO;
import com.dev.servlet.domain.transfer.request.Request;
import com.dev.servlet.core.exception.ServiceException;

public interface ILoginService {
    UserDTO login(Request request, IUserService userService) throws ServiceException;
    void logout(Request request);
}
