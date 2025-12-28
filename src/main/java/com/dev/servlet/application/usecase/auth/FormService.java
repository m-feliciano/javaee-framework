package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.port.in.auth.FormUseCase;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ApplicationScoped
public class FormService implements FormUseCase {
    @Inject
    private AuthenticationPort auth;

    @Override
    public String form(String auth, String onSuccess) {
        return this.auth.validateToken(auth) ? "redirect:/" + onSuccess : "forward:pages/formLogin.jsp";
    }
}
