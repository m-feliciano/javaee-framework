package com.dev.servlet.domain.service;
import com.dev.servlet.domain.transfer.dto.UserDTO;
import com.dev.servlet.domain.transfer.request.Request;
import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.model.User;
import java.util.Optional;

public interface IUserService {
    boolean isEmailAvailable(String email, User candidate);
    UserDTO register(Request request) throws ServiceException;
    UserDTO update(Request request) throws ServiceException;
    UserDTO getById(Request request) throws ServiceException;
    boolean delete(Request request) throws ServiceException;
    Optional<User> findByLoginAndPassword(String login, String password);
}
