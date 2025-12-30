package com.servletstack.adapter.out.home;

import com.servletstack.application.port.in.auth.RegisterPageUseCase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RegisterPageAdapter implements RegisterPageUseCase {

    @Override
    public String registerPage() {
        // TODO Use property file to manage page paths
        return "forward:pages/user/formCreateUser.jsp";
    }
}
