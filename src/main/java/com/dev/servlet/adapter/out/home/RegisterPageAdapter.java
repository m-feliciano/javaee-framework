package com.dev.servlet.adapter.out.home;

import com.dev.servlet.application.port.in.auth.RegisterPagePort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RegisterPageAdapter implements RegisterPagePort {

    @Override
    public String registerPage() {
        // TODO Use property file to manage page paths
        return "forward:pages/user/formCreateUser.jsp";
    }
}
