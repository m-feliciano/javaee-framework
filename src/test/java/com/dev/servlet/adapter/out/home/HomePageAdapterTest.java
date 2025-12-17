package com.dev.servlet.adapter.out.home;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("HomePageAdapter Tests")
class HomePageAdapterTest {

    @InjectMocks
    private HomePageAdapter homePageAdapter;

    @Test
    @DisplayName("Should return redirect to login form")
    void shouldReturnRedirectToLoginForm() {
        // Act
        String result = homePageAdapter.homePage();

        // Assert
        assertThat(result).isEqualTo("redirect:/api/v1/auth/form");
    }

    @Test
    @DisplayName("Should return redirect path")
    void shouldReturnRedirectPath() {
        // Act
        String result = homePageAdapter.homePage();

        // Assert
        assertThat(result).startsWith("redirect:");
    }

    @Test
    @DisplayName("Should return auth endpoint")
    void shouldReturnAuthEndpoint() {
        // Act
        String result = homePageAdapter.homePage();

        // Assert
        assertThat(result).contains("/api/v1/auth/");
    }
}

