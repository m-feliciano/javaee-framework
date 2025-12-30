package com.servletstack.application.usecase.user;

import com.servletstack.adapter.out.messaging.Message;
import com.servletstack.application.exception.AppException;
import com.servletstack.application.port.in.user.GenerateConfirmationTokenUseCase;
import com.servletstack.application.port.in.user.UpdateUserUseCase;
import com.servletstack.application.port.in.user.UserDetailsUseCase;
import com.servletstack.application.port.out.AsyncMessagePort;
import com.servletstack.application.port.out.alert.AlertPort;
import com.servletstack.application.port.out.security.AuthenticationPort;
import com.servletstack.application.port.out.user.UserRepositoryPort;
import com.servletstack.application.transfer.request.UserRequest;
import com.servletstack.application.transfer.response.UserResponse;
import com.servletstack.domain.entity.Credentials;
import com.servletstack.domain.entity.User;
import com.servletstack.domain.entity.enums.Status;
import com.servletstack.domain.enums.MessageType;
import com.servletstack.infrastructure.config.Properties;
import com.servletstack.infrastructure.utils.PasswordHasher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class UpdateUserService implements UpdateUserUseCase {
    @Inject
    private AsyncMessagePort messagePort;
    @Inject
    private AuthenticationPort auth;
    @Inject
    private AlertPort alertPort;
    @Inject
    private UserRepositoryPort repository;
    @Inject
    private UserDetailsUseCase userDetailsUseCase;
    @Inject
    private GenerateConfirmationTokenUseCase generateConfirmationTokenUseCase;

    public UserResponse update(UserRequest userRequest, String auth) throws AppException {
        UUID userId = this.auth.extractUserId(auth);

        if (Properties.isDemoModeEnabled()) {
            log.warn("UpdateUserUseCase: update users is not allowed in demo mode");
            alertPort.publish(userId, "warning", "Update user is not allowed in demo mode.");
            return userDetailsUseCase.getDetail(auth);
        }

        final String newEmail = userRequest.login().toLowerCase();
        boolean emailUnavailable = !isEmailAvailable(newEmail, User.builder().id(userId).build());
        if (emailUnavailable) {
            alertPort.publish(userId, "warning", "The email address is already in use.");
            return userDetailsUseCase.getDetail(auth);
        }

        User user = repository.findById(userId).orElseThrow();
        final String oldEmail = user.getLogin();

        repository.updateCredentials(userId,
                new Credentials(null, PasswordHasher.hash(userRequest.password())));

        if (!oldEmail.equals(newEmail)) {
            String token = generateConfirmationTokenUseCase.generateFor(user, newEmail);
            String link = Properties.getAppBaseUrl() + "/api/v1/user/email-change-confirmation?token=" + token;
            messagePort.send(new Message(MessageType.CHANGE_EMAIL, newEmail, OffsetDateTime.now().toString(), link));

            alertPort.publish(user.getId(), "info", "A confirmation email has been sent to your new email address.");
        } else {
            alertPort.publish(user.getId(), "success", "Your profile has been updated successfully.");
        }

        return new UserResponse(user.getId());
    }

    private boolean isEmailAvailable(String email, User candidate) {
        User filter = new User(email, null, Status.ACTIVE);
        return repository.find(filter)
                .map(u -> u.getId().equals(candidate.getId()))
                .orElse(true);
    }
}
