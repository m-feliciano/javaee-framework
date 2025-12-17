package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.application.port.out.security.AuthenticationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.lenient;

/**
 * Base class for controller tests providing common mock setup and utilities.
 * Extends this class to inherit authentication mock setup and test utilities.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class BaseControllerTest {

    protected static final String VALID_AUTH_TOKEN = "Bearer valid.jwt.token";
    protected static final String INVALID_AUTH_TOKEN = "Bearer invalid.token";
    protected static final String USER_ID = "test-user-123";
    protected static final String ADMIN_USER_ID = "admin-user-456";
    @Mock
    protected AuthenticationPort authenticationPort;

    @BeforeEach
    void setupBaseController() {
        // Setup default authentication behavior using lenient() to avoid UnnecessaryStubbingException
        lenient().when(authenticationPort.extractUserId(VALID_AUTH_TOKEN)).thenReturn(USER_ID);
        lenient().when(authenticationPort.extractUserId(INVALID_AUTH_TOKEN)).thenReturn(null);
        setupAdditionalMocks();
    }

    /**
     * Override this method to setup additional mocks specific to each controller test.
     */
    protected void setupAdditionalMocks() {
        // Default implementation does nothing
    }

    /**
     * Verify that a redirect response follows the expected pattern.
     */
    protected void assertRedirectResponse(String actual, String expectedContext) {
        assert actual != null : "Redirect response should not be null";
        assert actual.startsWith("redirect:") : "Response should start with 'redirect:'";
        assert actual.contains(expectedContext) : "Response should contain context: " + expectedContext;
    }

    /**
     * Verify that a forward response follows the expected pattern.
     */
    protected void assertForwardResponse(String actual, String expectedPage) {
        assert actual != null : "Forward response should not be null";
        assert actual.startsWith("forward:") : "Response should start with 'forward:'";
        assert actual.contains(expectedPage) : "Response should contain page: " + expectedPage;
    }
}

