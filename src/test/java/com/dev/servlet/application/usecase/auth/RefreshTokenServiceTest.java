package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.refreshtoken.RefreshTokenRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.response.RefreshTokenResponse;
import com.dev.servlet.domain.entity.Credentials;
import com.dev.servlet.domain.entity.RefreshToken;
import com.dev.servlet.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RefreshTokenUseCase Tests")
class RefreshTokenServiceTest {

    private static final String OLD_TOKEN_RAW = "old.refresh.token";
    private static final String NEW_TOKEN_RAW = "new.refresh.token";
    private static final String OLD_REFRESH_TOKEN = "Bearer old.refresh.token";
    private static final String NEW_REFRESH_TOKEN = "Bearer new.refresh.token";
    private static final String NEW_ACCESS_TOKEN = "Bearer new.access.token";

    private static final UUID OLD_TOKEN_ID = UUID.fromString("111e4567-e89b-12d3-a456-426614174000");
    private static final UUID NEW_TOKEN_ID = UUID.fromString("222e4567-e89b-12d3-a456-426614174000");
    private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @Mock
    private AuthenticationPort authenticationPort;
    @Mock
    private RefreshTokenRepositoryPort repositoryPort;
    @Mock
    private CachePort cachePort;
    @InjectMocks
    private RefreshTokenService refreshTokenService;
    private User testUser;
    private RefreshToken oldRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(USER_ID)
                .credentials(Credentials.builder()
                        .login("test@example.com")
                        .build())
                .build();

        oldRefreshToken = RefreshToken.builder()
                .id(OLD_TOKEN_ID)
                .token(OLD_TOKEN_RAW)
                .user(testUser)
                .revoked(false)
                .issuedAt(Instant.now().minusSeconds(3600))
                .expiresAt(Instant.now().plusSeconds(86400))
                .build();

        lenient().when(authenticationPort.stripBearerPrefix(OLD_REFRESH_TOKEN)).thenReturn(OLD_TOKEN_RAW);
        lenient().when(authenticationPort.stripBearerPrefix(NEW_REFRESH_TOKEN)).thenReturn(NEW_TOKEN_RAW);
        lenient().when(authenticationPort.generateAccessToken(testUser)).thenReturn(NEW_ACCESS_TOKEN);
        lenient().when(authenticationPort.generateRefreshToken(testUser)).thenReturn(NEW_REFRESH_TOKEN);
        lenient().when(authenticationPort.extractUser(OLD_REFRESH_TOKEN)).thenReturn(testUser);
    }

    @Nested
    @DisplayName("Successful Token Refresh Tests")
    class SuccessfulRefreshTests {

        @Test
        @DisplayName("Should refresh token successfully")
        void shouldRefreshTokenSuccessfully() {
            // Arrange
            when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(true);
            when(repositoryPort.findByToken(OLD_TOKEN_RAW)).thenReturn(Optional.of(oldRefreshToken));

            RefreshToken newToken = RefreshToken.builder()
                    .id(NEW_TOKEN_ID)
                    .token(NEW_TOKEN_RAW)
                    .build();
            when(repositoryPort.save(any(RefreshToken.class))).thenReturn(newToken);

            // Act
            RefreshTokenResponse response = refreshTokenService.refreshToken(OLD_REFRESH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.token()).isEqualTo(NEW_ACCESS_TOKEN);
            assertThat(response.refreshToken()).isEqualTo(NEW_REFRESH_TOKEN);

            verify(authenticationPort).validateToken(OLD_REFRESH_TOKEN);
            verify(repositoryPort).findByToken(OLD_TOKEN_RAW);
            verify(authenticationPort).generateAccessToken(testUser);
            verify(authenticationPort).generateRefreshToken(testUser);
        }

        @Test
        @DisplayName("Should revoke old token after refresh")
        void shouldRevokeOldToken() {
            // Arrange
            lenient().when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(true);
            lenient().when(repositoryPort.findByToken(OLD_TOKEN_RAW)).thenReturn(Optional.of(oldRefreshToken));

            RefreshToken newToken = RefreshToken.builder()
                    .id(NEW_TOKEN_ID)
                    .token(NEW_TOKEN_RAW)
                    .build();

            lenient().when(repositoryPort.save(any())).thenReturn(newToken);

            // Act
            refreshTokenService.refreshToken(OLD_REFRESH_TOKEN);

            // Assert
            verify(repositoryPort).update(argThat(RefreshToken::isRevoked));
        }

        @Test
        @DisplayName("Should create new refresh token")
        void shouldCreateNewRefreshToken() {
            // Arrange
            when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(true);
            when(repositoryPort.findByToken(OLD_TOKEN_RAW)).thenReturn(Optional.of(oldRefreshToken));
            when(repositoryPort.save(any(RefreshToken.class))).thenReturn(RefreshToken.builder().build());

            // Act
            refreshTokenService.refreshToken(OLD_REFRESH_TOKEN);

            // Assert
            verify(repositoryPort).save(argThat(rt ->
                    rt.getToken().equals(NEW_TOKEN_RAW) &&
                    rt.getUser().equals(testUser) &&
                    !rt.isRevoked() &&
                    rt.getReplacedBy() == null
            ));
        }

        @Test
        @DisplayName("Should clear user cache after refresh")
        void shouldClearUserCache() {
            // Arrange
            when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(true);
            when(repositoryPort.findByToken(OLD_TOKEN_RAW)).thenReturn(Optional.of(oldRefreshToken));
            when(repositoryPort.save(any(RefreshToken.class))).thenReturn(RefreshToken.builder().build());

            // Act
            refreshTokenService.refreshToken(OLD_REFRESH_TOKEN);

            // Assert
            verify(cachePort).clearAll(eq(USER_ID));
        }

        @Test
        @DisplayName("Should set expiration to 30 days for new token")
        void shouldSetExpiration() {
            // Arrange
            when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(true);
            when(repositoryPort.findByToken(OLD_TOKEN_RAW)).thenReturn(Optional.of(oldRefreshToken));
            when(repositoryPort.save(any(RefreshToken.class))).thenReturn(RefreshToken.builder().build());

            Instant before = Instant.now().plusSeconds(2592000 - 10); // 30 days - 10s
            Instant after = Instant.now().plusSeconds(2592000 + 10); // 30 days + 10s

            // Act
            refreshTokenService.refreshToken(OLD_REFRESH_TOKEN);

            // Assert
            verify(repositoryPort).save(argThat(rt ->
                    rt.getExpiresAt().isAfter(before) &&
                    rt.getExpiresAt().isBefore(after)
            ));
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should reject invalid token signature")
        void shouldRejectInvalidSignature() {
            // Arrange
            when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> refreshTokenService.refreshToken(OLD_REFRESH_TOKEN))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("Invalid refresh token");

            verify(repositoryPort, never()).findByToken(any());
        }

        @Test
        @DisplayName("Should reject token not found in database")
        void shouldRejectTokenNotFound() {
            // Arrange
            when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(true);
            when(repositoryPort.findByToken(OLD_TOKEN_RAW)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> refreshTokenService.refreshToken(OLD_REFRESH_TOKEN))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("Refresh token is invalid or revoked");
        }

        @Test
        @DisplayName("Should reject expired token")
        void shouldRejectExpiredToken() {
            // Arrange
            RefreshToken expiredToken = RefreshToken.builder()
                    .id(OLD_TOKEN_ID)
                    .token(OLD_TOKEN_RAW)
                    .user(testUser)
                    .revoked(false)
                    .issuedAt(Instant.now().minusSeconds(7200))
                    .expiresAt(Instant.now().minusSeconds(3600))
                    .build();

            when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(true);
            when(repositoryPort.findByToken(OLD_TOKEN_RAW)).thenReturn(Optional.of(expiredToken));

            // Act & Assert
            assertThatThrownBy(() -> refreshTokenService.refreshToken(OLD_REFRESH_TOKEN))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("Refresh token is invalid or revoked");
        }

        @Test
        @DisplayName("Should reject token with null expiration")
        void shouldRejectTokenWithNullExpiration() {
            // Arrange
            RefreshToken tokenWithNullExpiry = RefreshToken.builder()
                    .id(OLD_TOKEN_ID)
                    .token(OLD_TOKEN_RAW)
                    .user(testUser)
                    .revoked(false)
                    .expiresAt(null)
                    .build();

            when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(true);
            when(repositoryPort.findByToken(OLD_TOKEN_RAW)).thenReturn(Optional.of(tokenWithNullExpiry));

            // Act & Assert
            assertThatThrownBy(() -> refreshTokenService.refreshToken(OLD_REFRESH_TOKEN))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("Refresh token is invalid or revoked");
        }
    }

    @Nested
    @DisplayName("Token Rotation Tests")
    class TokenRotationTests {

        @Test
        @DisplayName("Should link old token to new token")
        void shouldLinkOldToNewToken() {
            // Arrange
            when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(true);
            when(repositoryPort.findByToken(OLD_TOKEN_RAW)).thenReturn(Optional.of(oldRefreshToken));

            when(repositoryPort.save(any(RefreshToken.class))).thenAnswer(i -> {
                RefreshToken rt = i.getArgument(0);
                rt.setId(NEW_TOKEN_ID);
                return rt;
            });

            // Act
            refreshTokenService.refreshToken(OLD_REFRESH_TOKEN);

            // Assert
            verify(repositoryPort).update(argThat(rt ->
                    OLD_TOKEN_ID.equals(rt.getId()) &&
                    NEW_TOKEN_ID.equals(rt.getReplacedBy())
            ));
        }

        @Test
        @DisplayName("Should execute operations in correct order")
        void shouldExecuteInCorrectOrder() {
            // Arrange
            when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(true);
            when(repositoryPort.findByToken(OLD_TOKEN_RAW)).thenReturn(Optional.of(oldRefreshToken));
            when(repositoryPort.save(any(RefreshToken.class))).thenReturn(RefreshToken.builder().id(NEW_TOKEN_ID).build());

            // Act
            refreshTokenService.refreshToken(OLD_REFRESH_TOKEN);

            // Assert - Verify order
            var inOrder = inOrder(authenticationPort, repositoryPort, cachePort);
            inOrder.verify(authenticationPort).validateToken(OLD_REFRESH_TOKEN);
            inOrder.verify(repositoryPort).findByToken(OLD_TOKEN_RAW);
            inOrder.verify(authenticationPort).extractUser(OLD_REFRESH_TOKEN);
            inOrder.verify(authenticationPort).generateAccessToken(testUser);
            inOrder.verify(authenticationPort).generateRefreshToken(testUser);
            inOrder.verify(repositoryPort).save(any(RefreshToken.class));
            inOrder.verify(repositoryPort).update(any(RefreshToken.class));
            inOrder.verify(cachePort).clearAll(USER_ID);
        }
    }

    @Nested
    @DisplayName("Bearer Prefix Handling Tests")
    class BearerPrefixTests {

        @Test
        @DisplayName("Should strip Bearer prefix before database lookup")
        void shouldStripBearerForLookup() {
            // Arrange
            when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(true);
            when(repositoryPort.findByToken(OLD_TOKEN_RAW)).thenReturn(Optional.of(oldRefreshToken));
            when(repositoryPort.save(any(RefreshToken.class))).thenReturn(RefreshToken.builder().build());

            // Act
            refreshTokenService.refreshToken(OLD_REFRESH_TOKEN);

            // Assert
            verify(authenticationPort).stripBearerPrefix(OLD_REFRESH_TOKEN);
            verify(repositoryPort).findByToken(eq(OLD_TOKEN_RAW));
        }

        @Test
        @DisplayName("Should strip Bearer prefix before saving new token")
        void shouldStripBearerForSave() {
            // Arrange
            when(authenticationPort.validateToken(OLD_REFRESH_TOKEN)).thenReturn(true);
            when(repositoryPort.findByToken(OLD_TOKEN_RAW)).thenReturn(Optional.of(oldRefreshToken));
            when(repositoryPort.save(any(RefreshToken.class))).thenReturn(RefreshToken.builder().build());

            // Act
            refreshTokenService.refreshToken(OLD_REFRESH_TOKEN);

            // Assert
            verify(authenticationPort).stripBearerPrefix(NEW_REFRESH_TOKEN);
            verify(repositoryPort).save(argThat(rt ->
                    !rt.getToken().startsWith("Bearer ")
            ));
        }
    }
}

