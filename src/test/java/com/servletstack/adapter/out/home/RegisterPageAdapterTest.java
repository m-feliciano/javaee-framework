package com.servletstack.adapter.out.home;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterPageAdapter Tests")
class RegisterPageAdapterTest {

    @InjectMocks
    private RegisterPageAdapter registerPageAdapter;

    @Test
    @DisplayName("Should return forward to registration JSP")
    void shouldReturnForwardToRegistrationJSP() {
        // Act
        String result = registerPageAdapter.registerPage();

        // Assert
        assertThat(result).isEqualTo("forward:pages/user/formCreateUser.jsp");
    }

    @Test
    @DisplayName("Should return forward path")
    void shouldReturnForwardPath() {
        // Act
        String result = registerPageAdapter.registerPage();

        // Assert
        assertThat(result).startsWith("forward:");
    }

    @Test
    @DisplayName("Should return JSP page")
    void shouldReturnJSPPage() {
        // Act
        String result = registerPageAdapter.registerPage();

        // Assert
        assertThat(result).endsWith(".jsp");
    }

    @Test
    @DisplayName("Should return user form page")
    void shouldReturnUserFormPage() {
        // Act
        String result = registerPageAdapter.registerPage();

        // Assert
        assertThat(result).contains("formCreateUser");
    }
}

