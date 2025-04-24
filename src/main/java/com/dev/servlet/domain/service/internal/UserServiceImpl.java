package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.UserMapper;
import com.dev.servlet.core.util.CryptoUtils;
import com.dev.servlet.domain.model.Credentials;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.RoleType;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.domain.service.IUserService;
import com.dev.servlet.domain.transfer.dto.UserDTO;
import com.dev.servlet.domain.transfer.request.Request;
import com.dev.servlet.infrastructure.persistence.dao.UserDAO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static com.dev.servlet.core.util.CryptoUtils.getUser;
import static com.dev.servlet.core.util.ThrowableUtils.notFound;
import static com.dev.servlet.core.util.ThrowableUtils.serviceError;

@Slf4j
@NoArgsConstructor
@Model
public class UserServiceImpl extends BaseServiceImpl<User, String> implements IUserService {

    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String CONFIRM_PASSWORD = "confirmPassword";

    @Inject
    public UserServiceImpl(UserDAO userDAO) {
        super(userDAO);
    }

    @Override
    public Class<UserDTO> getDataMapper() {
        return UserDTO.class;
    }

    @Override
    public User toEntity(Object object) {
        return UserMapper.full((UserDTO) object);
    }

    @Override
    public User getBody(Request request) {
        return getUser(request.getToken());
    }

    @Override
    public boolean isEmailAvailable(String email, User candidate) {
        return this.find(new User(email))
                .map(user -> !user.getId().equals(candidate.getId()))
                .orElse(true);
    }

    @Override
    public UserDTO register(Request request) throws ServiceException {
        log.trace("");
        String login = request.getParameter(LOGIN).toLowerCase();
        String password = request.getParameter(PASSWORD);
        String confirmPassword = request.getParameter(CONFIRM_PASSWORD);
        boolean passwordError = password == null || !password.equals(confirmPassword);
        if (passwordError) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Passwords do not match.");
        }

        User userExists = this.find(new User(login)).orElse(null);
        if (userExists != null) {
            log.warn("User already exists: {}", userExists.getCredentials().getLogin());
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "User already exists.");
        }

        User newUser = User.builder()
                .credentials(Credentials.builder()
                        .login(login)
                        .password(password)
                        .build())
                .imgUrl(request.getParameter("imgUrl"))
                .status(Status.ACTIVE.getValue())
                .perfis(List.of(RoleType.DEFAULT.getCode()))
                .build();
        newUser = super.save(newUser);
        return UserMapper.full(newUser);
    }

    @Override
    public UserDTO update(Request request) throws ServiceException {
        log.trace("");
        final User entity = getBody(request);
        String email = request.getParameter(LOGIN).toLowerCase();

        boolean emailUnavailable = !this.isEmailAvailable(email, entity);
        if (emailUnavailable) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Email already in use.");
        }

        User user = User.builder()
                .id(entity.getId())
                .imgUrl(request.getParameter("imgUrl"))
                .credentials(Credentials.builder()
                        .login(email)
                        .password(request.getParameter(PASSWORD))
                        .build())
                .status(Status.ACTIVE.getValue())
                .perfis(entity.getPerfis())
                .build();
        user = super.update(user);
        UserDTO dto = UserMapper.full(user);
        dto.setToken(CryptoUtils.generateJwtToken(dto));
        return dto;
    }

    @Override
    public UserDTO getById(Request request) throws ServiceException {
        log.trace("");
        User user = require(request.id());
        return UserMapper.full(user);
    }

    @Override
    public boolean delete(Request request) throws ServiceException {
        log.trace("");
        final User entity = getBody(request);
        super.delete(entity);
        return true;
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return super.find(new User(login, password));
    }

    private User require(String id) throws ServiceException {
        return findById(id).orElseThrow(() -> notFound("User not found"));
    }
}
