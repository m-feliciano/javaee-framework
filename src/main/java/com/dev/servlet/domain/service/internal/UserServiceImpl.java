package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.UserMapper;
import com.dev.servlet.domain.model.Credentials;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.RoleType;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.domain.service.AuditService;
import com.dev.servlet.domain.service.IUserService;
import com.dev.servlet.domain.transfer.request.UserCreateRequest;
import com.dev.servlet.domain.transfer.request.UserRequest;
import com.dev.servlet.domain.transfer.response.UserResponse;
import com.dev.servlet.infrastructure.persistence.dao.UserDAO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static com.dev.servlet.core.util.ThrowableUtils.serviceError;

@Slf4j
@NoArgsConstructor
@Model
public class UserServiceImpl extends BaseServiceImpl<User, String> implements IUserService {

    @Inject
    private UserMapper userMapper;

    @Inject
    private AuditService auditService;

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
            auditService.auditFailure("user:register", null, new AuditPayload<>(user, null));
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Passwords do not match.");
        }

        User userExists = this.find(new User(user.login(), null)).orElse(null);
        if (userExists != null) {
            log.warn("User already exists: {}", userExists.getCredentials().getLogin());
            auditService.auditFailure("user:register", null, new AuditPayload<>(user, null));
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
        UserResponse response = userMapper.toResponse(newUser);
        auditService.auditSuccess("user:register", null, new AuditPayload<>(user, response));
        return response;
    }

    @Override
    public UserResponse update(UserRequest userRequest, String auth) throws ServiceException {
        log.trace("");

        final String email = userRequest.login().toLowerCase();

        boolean emailUnavailable = !this.isEmailAvailable(email, userMapper.toUser(userRequest));
        if (emailUnavailable) {
            auditService.auditWarning("user:update", auth, new AuditPayload<>(userRequest, null));
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Email already in use.");
        }

        UserResponse entity = this.getById(userRequest, auth);
        User user = User.builder()
                .id(entity.getId())
                .imgUrl(userRequest.imgUrl())
                .credentials(Credentials.builder()
                        .login(email)
                        .password(userRequest.password())
                        .build())
                .status(Status.ACTIVE.getValue())
                .perfis(entity.getPerfis())
                .build();

        try {
            user = super.update(user);
        } catch (Exception e) {
            auditService.auditFailure("user:update", auth, new AuditPayload<>(userRequest, null));
            throw e;
        }

        UserResponse response = userMapper.toResponse(user);
        auditService.auditSuccess("user:update", auth, new AuditPayload<>(userRequest, response));
        return response;
    }

    @Override
    public UserResponse getById(UserRequest request, String auth) throws ServiceException {
        log.trace("");

        try {
            User user = loadUser(request.id(), auth);
            UserResponse response = userMapper.toResponse(user);
            auditService.auditSuccess("user:get_by_id", auth, new AuditPayload<>(request, response));
            return response;
        } catch (Exception e) {
            auditService.auditFailure("user:get_by_id", auth, new AuditPayload<>(request, null));
            throw e;
        }
    }

    @Override
    public void delete(UserRequest request, String auth) throws ServiceException {
        log.trace("");

        try {
            UserResponse response = getById(request, auth);
            User user = User.builder().id(response.getId()).build();
            super.delete(user);
            auditService.auditSuccess("user:delete", auth, new AuditPayload<>(request, null));
        } catch (Exception e) {
            auditService.auditFailure("user:delete", auth, new AuditPayload<>(request, e.getMessage()));
            throw e;
        }
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return super.find(new User(login, password));
    }

    private User loadUser(String id, String auth) throws ServiceException {
        String userId = jwts.getUserId(auth);
        if (!id.equals(userId)) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "User not authorized.");
        }

        return findById(id).orElse(null);
    }
}
