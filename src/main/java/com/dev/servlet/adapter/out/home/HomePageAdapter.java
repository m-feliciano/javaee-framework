package com.dev.servlet.adapter.out.home;

import com.dev.servlet.application.port.in.auth.HomePagePort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HomePageAdapter implements HomePagePort {
    @Override
    public String homePage() {
        // TODO Use property file to manage page paths
        return "redirect:/api/v1/auth/form";
    }
}
