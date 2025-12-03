package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.in.user.GetUserPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class GetUserUseCase implements GetUserPort {
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    private AuditPort auditPort;

    public Optional<User> get(UserRequest request) throws ApplicationException {
        log.debug("GetUserUseCase: getting user details with id {}", request.login());

        var maybe = repositoryPort.find(new User(request.login(), request.password()));
        if (maybe.isEmpty()) {
            auditPort.failure("user:find_by_login", null, null);
            return Optional.empty();
        }

        auditPort.success("user:find_by_login", null, null);
        return maybe;
    }
}
