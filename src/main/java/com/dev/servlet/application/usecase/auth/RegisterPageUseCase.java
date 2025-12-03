package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.port.in.auth.RegisterPagePort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor
public class RegisterPageUseCase implements RegisterPagePort {

    @Override
    public String registerPage() {
        return "forward:pages/user/formCreateUser.jsp";
    }
}
