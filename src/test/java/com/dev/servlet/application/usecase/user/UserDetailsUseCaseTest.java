package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.User;
import org.assertj.core.api.Assertions;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserDetailsUseCase Tests")
class UserDetailsUseCaseTest {

    private static final String USER_ID = "user-123";
    private static final String AUTH_TOKEN = "Bearer valid.token";
    private static final String CACHE_NAMESPACE = "userCacheKey";
    @Mock
    private UserRepositoryPort repositoryPort;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationPort authenticationPort;
    @Mock
    private CachePort cachePort;
    @InjectMocks
    private UserDetailsUseCase userDetailsUseCase;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(USER_ID)
                .build();

        userResponse = new UserResponse(USER_ID);
        userResponse.setLogin("test@example.com");

        lenient()
                .when(authenticationPort.extractUserId(AUTH_TOKEN))
                .thenReturn(USER_ID);
        lenient()
                .when(repositoryPort.findById(USER_ID))
                .thenReturn(Optional.of(user));
        lenient()
                .when(userMapper.toResponse(user))
                .thenReturn(userResponse);
        lenient()
                .when(cachePort.get(CACHE_NAMESPACE, USER_ID))
                .thenReturn(null);
        lenient()
                .doNothing()
                .when(cachePort).set(any(), any(), any());
    }

    @Nested
    @DisplayName("Successful Retrieval Tests")
    class SuccessfulRetrievalTests {

        @Test
        @DisplayName("Should get user details successfully")
        void shouldGetUserDetailsSuccessfully() {
            // Act
            UserResponse response = userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(USER_ID);

            verify(authenticationPort).extractUserId(AUTH_TOKEN);
            verify(repositoryPort).findById(USER_ID);
            verify(userMapper).toResponse(user);
        }

        @Test
        @DisplayName("Should validate user authorization")
        void shouldValidateUserAuthorization() {
            // Act
            userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN);

            // Assert
            verify(authenticationPort).extractUserId(AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should check cache before database")
        void shouldCheckCacheBeforeDatabase() {
            // Act
            userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN);

            // Assert - Verify order
            var inOrder = inOrder(cachePort, repositoryPort);
            inOrder.verify(cachePort).get(CACHE_NAMESPACE, USER_ID);
            inOrder.verify(repositoryPort).findById(USER_ID);
        }

        @Test
        @DisplayName("Should store result in cache")
        void shouldStoreResultInCache() {
            // Act
            userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN);

            // Assert
            verify(cachePort).set(CACHE_NAMESPACE, USER_ID, userResponse);
        }

        @Test
        @DisplayName("Should map user entity to response")
        void shouldMapUserToResponse() {
            // Act
            userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN);

            // Assert
            verify(userMapper).toResponse(user);
        }
    }

    @Nested
    @DisplayName("Cache Tests")
    class CacheTests {

        @Test
        @DisplayName("Should return cached response when available")
        void shouldReturnCachedResponse() {
            // Arrange
            UserResponse cachedResponse = new UserResponse(USER_ID);
            cachedResponse.setLogin("cached@example.com");
            when(cachePort.get(CACHE_NAMESPACE, USER_ID)).thenReturn(cachedResponse);

            // Act
            UserResponse response = userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN);

            // Assert
            assertThat(response).isEqualTo(cachedResponse);
            verify(repositoryPort, never()).findById(anyString());
            verify(userMapper, never()).toResponse(any());
        }

        @Test
        @DisplayName("Should not query database when cache hit")
        void shouldNotQueryDatabaseOnCacheHit() {
            // Arrange
            when(cachePort.get(CACHE_NAMESPACE, USER_ID)).thenReturn(userResponse);

            // Act
            userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN);

            // Assert
            verify(repositoryPort, never()).findById(anyString());
        }

        @Test
        @DisplayName("Should query database when cache miss")
        void shouldQueryDatabaseOnCacheMiss() {
            // Arrange
            when(cachePort.get(CACHE_NAMESPACE, USER_ID)).thenReturn(null);

            // Act
            userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN);

            // Assert
            verify(repositoryPort).findById(USER_ID);
        }

        @Test
        @DisplayName("Should use correct cache namespace")
        void shouldUseCorrectCacheNamespace() {
            // Act
            userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN);

            // Assert
            verify(cachePort).get(CACHE_NAMESPACE, USER_ID);
            verify(cachePort).set(CACHE_NAMESPACE, USER_ID, userResponse);
        }
    }

    @Nested
    @DisplayName("Authorization Tests")
    class AuthorizationTests {

        @Test
        @DisplayName("Should throw exception when user IDs do not match")
        void shouldThrowExceptionWhenUserIdsDontMatch() {
            // Arrange
            String differentUserId = "different-user-id";

            // Act & Assert
            assertThatThrownBy(() -> userDetailsUseCase.getDetail(differentUserId, AUTH_TOKEN))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("User not authorized");

            verify(repositoryPort, never()).findById(anyString());
        }

        @Test
        @DisplayName("Should allow user to access own details")
        void shouldAllowUserToAccessOwnDetails() {
            // Act
            UserResponse response = userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            verify(repositoryPort).findById(USER_ID);
        }

        @Test
        @DisplayName("Should trim user ID before validation")
        void shouldTrimUserIdBeforeValidation() {
            // Arrange
            String userIdWithSpaces = "  " + USER_ID + "  ";

            Assertions.assertThatThrownBy(() -> userDetailsUseCase.getDetail(userIdWithSpaces, AUTH_TOKEN))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("User not found");
        }
    }

    @Nested
    @DisplayName("User Not Found Tests")
    class UserNotFoundTests {

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            when(repositoryPort.findById(USER_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("User not found");

            verify(userMapper, never()).toResponse(any());
            verify(cachePort, never()).set(anyString(), anyString(), any());
        }

        @Test
        @DisplayName("Should not cache when user not found")
        void shouldNotCacheWhenUserNotFound() {
            // Arrange
            when(repositoryPort.findById(USER_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN))
                    .isInstanceOf(AppException.class);

            verify(cachePort, never()).set(anyString(), anyString(), any());
        }
    }

    @Nested
    @DisplayName("Execution Order Tests")
    class ExecutionOrderTests {

        @Test
        @DisplayName("Should execute operations in correct order")
        void shouldExecuteInCorrectOrder() {
            // Act
            userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN);

            // Assert - Verify order
            var inOrder = inOrder(authenticationPort, cachePort, repositoryPort, userMapper);
            inOrder.verify(authenticationPort).extractUserId(AUTH_TOKEN);
            inOrder.verify(cachePort).get(CACHE_NAMESPACE, USER_ID);
            inOrder.verify(repositoryPort).findById(USER_ID);
            inOrder.verify(userMapper).toResponse(user);
            inOrder.verify(cachePort).set(CACHE_NAMESPACE, USER_ID, userResponse);
        }

        @Test
        @DisplayName("Should validate authorization before cache check")
        void shouldValidateBeforeCacheCheck() {
            // Act
            userDetailsUseCase.getDetail(USER_ID, AUTH_TOKEN);

            // Assert
            var inOrder = inOrder(authenticationPort, cachePort);
            inOrder.verify(authenticationPort).extractUserId(AUTH_TOKEN);
            inOrder.verify(cachePort).get(anyString(), anyString());
        }
    }
}

