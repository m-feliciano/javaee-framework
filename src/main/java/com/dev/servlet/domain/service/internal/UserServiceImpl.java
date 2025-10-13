package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.UserMapper;
import com.dev.servlet.core.util.CryptoUtils;
import com.dev.servlet.domain.model.Credentials;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.RoleType;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.domain.service.IUserService;
import com.dev.servlet.domain.transfer.response.UserResponse;
import com.dev.servlet.domain.transfer.request.UserCreateRequest;
import com.dev.servlet.domain.transfer.request.UserRequest;
import com.dev.servlet.infrastructure.persistence.dao.UserDAO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static com.dev.servlet.core.util.CryptoUtils.getUser;
import static com.dev.servlet.core.util.ThrowableUtils.serviceError;

@Slf4j
@NoArgsConstructor
@Model
public class UserServiceImpl extends BaseServiceImpl<User, String> implements IUserService {

    @Inject
    private UserMapper userMapper;

    @Inject
    public UserServiceImpl(UserDAO userDAO) {
        super(userDAO);
    }

    @Override
    public boolean isEmailAvailable(String email, User candidate) {
        return this.find(new User(email, null))
                .map(user -> user.getId().equals(candidate.getId()))
                .orElse(true);
    }

    @Override
    public UserResponse register(UserCreateRequest user) throws ServiceException {
        log.trace("");

        boolean passwordError = user.password() == null || !user.password().equals(user.confirmPassword());
        if (passwordError) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Passwords do not match.");
        }

        User userExists = this.find(new User(user.login(), null)).orElse(null);
        if (userExists != null) {
            log.warn("User already exists: {}", userExists.getCredentials().getLogin());
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Cannot register this user.");
        }

        User newUser = User.builder()
                .credentials(Credentials.builder()
                        .login(user.login().toLowerCase())
                        .password(user.password())
                        .build())
                .status(Status.ACTIVE.getValue())
                .perfis(List.of(RoleType.DEFAULT.getCode()))
                .build();
        newUser = super.save(newUser);
        return userMapper.toResponse(newUser);
    }

    @Override
    public UserResponse update(UserRequest userRequest, String auth) throws ServiceException {
        log.trace("");

        final String email = userRequest.login().toLowerCase();

        boolean emailUnavailable = !this.isEmailAvailable(email, userMapper.toUser(userRequest));
        if (emailUnavailable) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Email already in use.");
        }

        UserResponse response = this.getById(userRequest, auth);
        User user = User.builder()
                .id(response.getId())
                .imgUrl(userRequest.imgUrl())
                .credentials(Credentials.builder()
                        .login(email)
                        .password(userRequest.password())
                        .build())
                .status(Status.ACTIVE.getValue())
                .perfis(response.getPerfis())
                .build();
        user = super.update(user);
        user.setToken(CryptoUtils.generateJwtToken(user));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getById(UserRequest request, String auth) throws ServiceException {
        log.trace("");
        User user = loadUser(request.id(), auth);
        return userMapper.toResponse(user);
    }

    @Override
    public void delete(UserRequest request, String auth) throws ServiceException {
        log.trace("");
        UserResponse response = getById(request, auth);
        User user = User.builder().id(response.getId()).build();
        super.delete(user);
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return super.find(new User(login, password));
    }

    private User loadUser(String id, String auth) throws ServiceException {
        if (!id.equals(getUser(auth).getId())) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "User not authorized.");
        }

        return findById(id).orElse(null);
    }
}
