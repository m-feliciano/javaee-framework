package com.dev.servlet.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.UserMapper;
import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.core.util.CloneUtil;
import com.dev.servlet.core.util.Properties;
import com.dev.servlet.domain.enumeration.MessageType;
import com.dev.servlet.domain.model.ConfirmationToken;
import com.dev.servlet.domain.model.Credentials;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.RoleType;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.domain.request.UserCreateRequest;
import com.dev.servlet.domain.request.UserRequest;
import com.dev.servlet.domain.response.UserResponse;
import com.dev.servlet.infrastructure.messaging.Message;
import com.dev.servlet.infrastructure.messaging.MessageProducer;
import com.dev.servlet.infrastructure.messaging.interfaces.MessageService;
import com.dev.servlet.infrastructure.persistence.dao.ConfirmationTokenDAO;
import com.dev.servlet.infrastructure.persistence.dao.UserDAO;
import com.dev.servlet.service.AuditService;
import com.dev.servlet.service.IUserService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Model;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dev.servlet.core.util.ThrowableUtils.serviceError;

@Slf4j
@NoArgsConstructor
@Model
public class UserServiceImpl extends BaseServiceImpl<User, String> implements IUserService {

    private static final String CACHE_KEY = "userCacheKey";
    @Inject
    private UserMapper userMapper;
    @Inject
    private AuditService auditService;
    @Inject
    private AlertService alertService;
    @Inject
    private ConfirmationTokenDAO confirmationTokenDAO;
    @Inject
    private Instance<MessageService> messageSenderInstance;
    @Inject
    private Instance<MessageProducer> emailProducerInstance;

    private String baseUrl;

    @PostConstruct
    public void init() {
        baseUrl = Properties.getEnvOrDefault("APP_BASE_URL", "http://localhost:8080");
    }

    @Inject
    public UserServiceImpl(UserDAO userDAO) {
        super(userDAO);
    }

    @Override
    public boolean isEmailAvailable(String email, User candidate) {
        User filter = new User(email, null, Status.ACTIVE);
        return this.find(filter)
                .map(user -> user.getId().equals(candidate.getId()))
                .orElse(true);
    }

    @Override
    public UserResponse register(UserCreateRequest user) throws ServiceException {
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
                .status(Status.PENDING.getValue())
                .perfis(List.of(RoleType.DEFAULT.getCode()))
                .build();
        newUser = super.save(newUser);
        log.info("User registered: {}", newUser.getCredentials().getLogin());

        String url = this.baseUrl + "/api/v1/user/confirm?token=" + generateConfirmationToken(newUser, null);
        MessageService sender = messageSender();
        sender.sendConfirmation(newUser.getCredentials().getLogin(), url);

        UserResponse response = userMapper.toResponse(newUser);
        CacheUtils.setObject(newUser.getId(), CACHE_KEY, response);

        auditService.auditSuccess("user:register", null, new AuditPayload<>(user, response));
        return response;
    }

    @Override
    public UserResponse confirmEmail(String token) throws ServiceException {
        ConfirmationToken ct = confirmationTokenDAO.findByToken(token)
                .orElseThrow(() -> serviceError(HttpServletResponse.SC_NOT_FOUND, "Invalid token."));

        if (ct.isUsed()) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Token already used.");
        }

        if (ct.getExpiresAt() != null && ct.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Token expired.");
        }

        User user = findById(ct.getUserId())
                .orElseThrow(() -> serviceError(HttpServletResponse.SC_NOT_FOUND, "User not found."));

        user.setStatus(Status.ACTIVE.getValue());
        try {
            user = super.update(user);
            ct.setUsed(true);
            confirmationTokenDAO.update(ct);
            CacheUtils.clear(user.getId(), CACHE_KEY);

        } catch (Exception e) {
            auditService.auditFailure("user:confirm", null, new AuditPayload<>(token, null));
            throw e;
        }

        String url = this.baseUrl + "/api/v1/user/confirm?token=" + generateConfirmationToken(user, null);
        MessageService sender = messageSender();
        sender.sendWelcome(user.getCredentials().getLogin(), url);

        UserResponse response = userMapper.toResponse(user);
        auditService.auditSuccess("user:confirm", null, new AuditPayload<>(token, response));
        return response;
    }

    @Override
    public UserResponse update(UserRequest userRequest, String auth) throws ServiceException {
        final String email = userRequest.login().toLowerCase();

        boolean emailUnavailable = !this.isEmailAvailable(email, userMapper.toUser(userRequest));
        if (emailUnavailable) {
            auditService.auditWarning("user:update", auth,
                    new AuditPayload<>(userRequest.forAudit(), null));
            alertService.publish(jwts.getUserId(auth), "warning", "The email address is already in use.");
            return this.getById(userRequest, auth);
        }

        UserResponse entity = this.getById(userRequest, auth);
        String oldEmail = entity.getLogin();

        User user = User.builder()
                .id(entity.getId())
                .imgUrl(userRequest.imgUrl())
                .credentials(Credentials.builder()
                        .login(entity.getLogin())
                        .password(userRequest.password())
                        .build())
                .status(Status.ACTIVE.getValue())
                .perfis(entity.getPerfis())
                .build();

        try {
            user = super.update(user);
            CacheUtils.clear(entity.getId(), CACHE_KEY);
        } catch (Exception e) {
            auditService.auditFailure("user:update", auth,
                    new AuditPayload<>(userRequest.forAudit(), null));
            throw e;
        }

        if (!oldEmail.equals(email)) {
            String link = this.baseUrl + "/api/v1/user/email-change-confirmation?token=" + generateConfirmationToken(user, email);
            String createdAt = OffsetDateTime.now().toString();
            MessageService sender = messageSender();
            sender.send(new Message(MessageType.CHANGE_EMAIL, email, createdAt, link));

            String info = "Email change requested for userId: " + user.getId();
            auditService.auditInfo("user:email-change-confirmation", auth,
                    new AuditPayload<>(userRequest.forAudit(), info));

            alertService.publish(user.getId(), "info", "A confirmation email has been sent to your new email address.");
        } else {
            alertService.publish(user.getId(), "success", "Your profile has been updated successfully.");
        }

        UserResponse response = userMapper.toResponse(user);
        auditService.auditSuccess("user:update", auth,
                new AuditPayload<>(userRequest.forAudit(), response));
        return response;
    }

    @Override
    public UserResponse getById(UserRequest request, String auth) throws ServiceException {
        log.info("User requested: {}", request.id());
        return getUserResponse(request.id(), auth);
    }

    @Override
    public UserResponse getUserDetail(UserRequest user, String auth) throws ServiceException {
        try {
            UserResponse response = getUserResponse(user.id(), auth);
            auditService.auditSuccess("user:find_by_id", auth, new AuditPayload<>(user, response));
            return response;
        } catch (Exception e) {
            auditService.auditFailure("user:find_by_id", auth, new AuditPayload<>(user, null));
            throw e;
        }
    }

    @Override
    public void delete(UserRequest request, String auth) throws ServiceException {
        try {
            UserResponse response = getById(request, auth);
            User user = User.builder().id(response.getId()).build();
            super.delete(user);
            CacheUtils.clearAll(response.getId());

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

    @Override
    public void resendConfirmation(String userId) throws ServiceException {
        if (StringUtils.isBlank(userId)) {
            throw serviceError(HttpServletResponse.SC_BAD_REQUEST, "Login is required");
        }

        Optional<User> maybe = this.findById(userId);
        if (maybe.isEmpty()) {
            log.warn("Resend confirmation requested for unknown userId: {}", userId);
            return;
        }

        User user = maybe.get();
        if (!Status.PENDING.equals(user.getStatus())) {
            log.info("User {} is not pending confirmation (status={}), skip resend", userId, user.getStatus());
            return;
        }

        String link = this.baseUrl + "/api/v1/user/confirm?token=" + generateConfirmationToken(user, null);
        String email = user.getCredentials().getLogin();
        String createdAt = OffsetDateTime.now().toString();
        Message confirmation = new Message(userId, MessageType.CONFIRMATION, email, createdAt, link);

        MessageService sender = messageSender();
        sender.send(confirmation);

        UserResponse response = userMapper.toResponse(user);
        CacheUtils.setObject(user.getId(), CACHE_KEY, response);

        auditService.auditSuccess("user:resend_confirmation", null, new AuditPayload<>(userId, response));
        alertService.publish(userId, "info", "A new confirmation email has been sent to your email address.");
    }

    @Override
    public void changeEmail(String token) throws ServiceException {
        if (StringUtils.isBlank(token)) {
            throw serviceError(HttpServletResponse.SC_BAD_REQUEST, "Token is required");
        }

        Optional<ConfirmationToken> oct = this.confirmationTokenDAO.findByToken(token);
        if (oct.isEmpty()) {
            log.warn("Resend confirmation requested for unknown token: {}", oct);
            return;
        }

        ConfirmationToken ct = oct.get();
        String userId = ct.getUserId();

        this.findById(userId).ifPresent(user -> {
            String email = CloneUtil.fromJson(ct.getBody(), String.class);
            user.setLogin(email);
            user = super.update(user);

            ct.setUsed(true);
            confirmationTokenDAO.update(ct);

            UserResponse response = userMapper.toResponse(user);
            CacheUtils.setObject(user.getId(), CACHE_KEY, response);

            auditService.auditSuccess("user:change_email", null, new AuditPayload<>(token, response));
            alertService.publish(userId, "success", "Your email has been changed successfully.");
        });
    }

    private String generateConfirmationToken(User user, Object body) {
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .userId(user.getId())
                .createdAt(OffsetDateTime.now())
                .expiresAt(OffsetDateTime.now().plusHours(1))
                .body(CloneUtil.toJson(body))
                .used(false)
                .build();

        confirmationTokenDAO.save(confirmationToken);
        return token;
    }

    private UserResponse getUserResponse(String id, String auth) throws ServiceException {
        String userId = jwts.getUserId(auth);
        if (!id.trim().equals(userId.trim())) {
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "User not authorized.");
        }

        UserResponse response = CacheUtils.getObject(id, CACHE_KEY);
        if (response != null) return response;

        findById(id)
                .ifPresent(u ->
                        CacheUtils.setObject(id, CACHE_KEY, userMapper.toResponse(u)));

        return CacheUtils.getObject(id, CACHE_KEY);
    }

    private MessageService messageSender() {
        if (!emailProducerInstance.isUnsatisfied()) {
            return emailProducerInstance.get();

        } else if (!messageSenderInstance.isUnsatisfied()) {
            return messageSenderInstance.get();
        }

        throw new IllegalStateException("No EmailService or JMS producer available; cannot send emails.");
    }
}
