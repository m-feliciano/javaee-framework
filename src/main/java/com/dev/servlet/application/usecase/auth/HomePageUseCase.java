package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.port.in.auth.HomePagePort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor
public class HomePageUseCase implements HomePagePort {
    @Override
    public String homePage() {
        return "redirect:/api/v1/auth/form";
    }
}
