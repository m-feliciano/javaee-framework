package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.port.in.auth.FormPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ApplicationScoped
public class FormUseCase implements FormPort {
    @Inject
    private AuthenticationPort authenticationPort;

    @Override
    public String form(String auth, String onSuccess) {
        return authenticationPort.validateToken(auth) ? "redirect:/" + onSuccess : "forward:pages/formLogin.jsp";
    }
}
