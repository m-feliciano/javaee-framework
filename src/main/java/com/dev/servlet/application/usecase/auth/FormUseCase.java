package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.port.in.auth.FormUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ApplicationScoped
public class FormUseCase implements FormUseCasePort {
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private AuditPort auditPort;

    @Override
    public String form(String auth, String onSuccess) {
        if (authenticationPort.validateToken(auth)) {
            auditPort.success("auth:form", auth, null);
            return "redirect:/" + onSuccess;
        }

        auditPort.warning("auth:form", auth, null);
        return "forward:pages/formLogin.jsp";
    }
}
