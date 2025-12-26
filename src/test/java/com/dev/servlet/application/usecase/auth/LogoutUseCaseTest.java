package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.refreshtoken.RefreshTokenRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LogoutUseCase Tests")
class LogoutUseCaseTest {

    private static final String AUTH_TOKEN = "Bearer valid.jwt.token";
    private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    @Mock
    private AuthenticationPort authenticationPort;
    @Mock
    private RefreshTokenRepositoryPort repositoryPort;
    @Mock
    private CachePort cachePort;
    @InjectMocks
    private LogoutUseCase logoutUseCase;

    @Nested
    @DisplayName("Successful Logout Tests")
    class SuccessfulLogoutTests {

        @Test
        @DisplayName("Should logout user successfully")
        void shouldLogoutSuccessfully() {
            // Arrange
            when(authenticationPort.extractUserId(AUTH_TOKEN)).thenReturn(USER_ID);
            doNothing().when(repositoryPort).revokeAll(USER_ID);
            doNothing().when(cachePort).clearAll(USER_ID);

            // Act
            logoutUseCase.logout(AUTH_TOKEN);

            // Assert
            verify(authenticationPort).extractUserId(AUTH_TOKEN);
            verify(repositoryPort).revokeAll(USER_ID);
            verify(cachePort).clearAll(USER_ID);
        }

        @Test
        @DisplayName("Should revoke all refresh tokens on logout")
        void shouldRevokeAllRefreshTokens() {
            // Arrange
            when(authenticationPort.extractUserId(AUTH_TOKEN)).thenReturn(USER_ID);

            // Act
            logoutUseCase.logout(AUTH_TOKEN);

            // Assert
            verify(repositoryPort).revokeAll(eq(USER_ID));
        }

        @Test
        @DisplayName("Should clear all cache on logout")
        void shouldClearAllCache() {
            // Arrange
            when(authenticationPort.extractUserId(AUTH_TOKEN)).thenReturn(USER_ID);

            // Act
            logoutUseCase.logout(AUTH_TOKEN);

            // Assert
            verify(cachePort).clearAll(eq(USER_ID));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle exception when extracting user ID gracefully")
        void shouldHandleExtractionException() {
            // Arrange
            when(authenticationPort.extractUserId(AUTH_TOKEN))
                    .thenThrow(new RuntimeException("Invalid token"));

            // Act - Should not throw exception
            logoutUseCase.logout(AUTH_TOKEN);

            // Assert
            verify(authenticationPort).extractUserId(AUTH_TOKEN);
            verify(repositoryPort, never()).revokeAll(org.mockito.ArgumentMatchers.any(UUID.class));
            verify(cachePort, never()).clearAll(org.mockito.ArgumentMatchers.any(UUID.class));
        }

        @Test
        @DisplayName("Should handle exception when revoking tokens gracefully")
        void shouldHandleRevocationException() {
            // Arrange
            when(authenticationPort.extractUserId(AUTH_TOKEN)).thenReturn(USER_ID);
            doThrow(new RuntimeException("Database error"))
                    .when(repositoryPort).revokeAll(USER_ID);

            // Act - Should not throw exception
            logoutUseCase.logout(AUTH_TOKEN);

            // Assert
            verify(repositoryPort).revokeAll(USER_ID);
        }

        @Test
        @DisplayName("Should handle exception when clearing cache gracefully")
        void shouldHandleCacheClearException() {
            // Arrange
            when(authenticationPort.extractUserId(AUTH_TOKEN)).thenReturn(USER_ID);
            doThrow(new RuntimeException("Cache error"))
                    .when(cachePort).clearAll(USER_ID);

            // Act - Should not throw exception
            logoutUseCase.logout(AUTH_TOKEN);

            // Assert
            verify(cachePort).clearAll(USER_ID);
        }

        @Test
        @DisplayName("Should handle null auth token gracefully")
        void shouldHandleNullAuthToken() {
            // Arrange
            when(authenticationPort.extractUserId(null))
                    .thenThrow(new NullPointerException("Null token"));

            // Act - Should not throw exception
            logoutUseCase.logout(null);

            // Assert
            verify(authenticationPort).extractUserId(null);
        }
    }

    @Nested
    @DisplayName("Execution Order Tests")
    class ExecutionOrderTests {

        @Test
        @DisplayName("Should extract user ID before revoking tokens")
        void shouldExtractUserIdFirst() {
            // Arrange
            when(authenticationPort.extractUserId(AUTH_TOKEN)).thenReturn(USER_ID);

            // Act
            logoutUseCase.logout(AUTH_TOKEN);

            // Assert - Verify order
            var inOrder = inOrder(authenticationPort, repositoryPort, cachePort);
            inOrder.verify(authenticationPort).extractUserId(AUTH_TOKEN);
            inOrder.verify(repositoryPort).revokeAll(USER_ID);
            inOrder.verify(cachePort).clearAll(USER_ID);
        }
    }
}
