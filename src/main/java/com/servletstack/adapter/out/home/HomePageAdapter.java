package com.servletstack.adapter.out.home;

import com.servletstack.application.port.in.auth.HomePageUseCase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HomePageAdapter implements HomePageUseCase {
    @Override
    public String homePage() {
        // TODO Use property file to manage page paths
        return "redirect:/api/v1/auth/form";
    }
}
