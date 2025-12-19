package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.response.UserResponse;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @Mock
    private UserRepositoryPort repositoryPort;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationPort authenticationPort;
    @InjectMocks
    private UserDetailsUseCase userDetailsUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(USER_ID)
                .build();

        UserResponse userResponse = new UserResponse(USER_ID);
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
    }

    @Nested
    @DisplayName("Successful Retrieval Tests")
    class SuccessfulRetrievalTests {

        @Test
        @DisplayName("Should get user details successfully")
        void shouldGetUserDetailsSuccessfully() {
            // Act
            UserResponse response = userDetailsUseCase.getDetail(AUTH_TOKEN);

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
            userDetailsUseCase.getDetail(AUTH_TOKEN);

            // Assert
            verify(authenticationPort).extractUserId(AUTH_TOKEN);
        }

        @Test
        @DisplayName("Should map user entity to response")
        void shouldMapUserToResponse() {
            // Act
            userDetailsUseCase.getDetail(AUTH_TOKEN);

            // Assert
            verify(userMapper).toResponse(user);
        }
    }

    @Nested
    @DisplayName("Authorization Tests")
    class AuthorizationTests {

        @Test
        @DisplayName("Should allow user to access own details")
        void shouldAllowUserToAccessOwnDetails() {
            // Act
            UserResponse response = userDetailsUseCase.getDetail(AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            verify(repositoryPort).findById(USER_ID);
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
            assertThatThrownBy(() -> userDetailsUseCase.getDetail(AUTH_TOKEN))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("User not found");

            verify(userMapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("Execution Order Tests")
    class ExecutionOrderTests {

        @Test
        @DisplayName("Should execute operations in correct order")
        void shouldExecuteInCorrectOrder() {
            // Act
            userDetailsUseCase.getDetail(AUTH_TOKEN);

            // Assert - Verify order
            var inOrder = inOrder(authenticationPort, repositoryPort, userMapper);
            inOrder.verify(authenticationPort).extractUserId(AUTH_TOKEN);
            inOrder.verify(repositoryPort).findById(USER_ID);
            inOrder.verify(userMapper).toResponse(user);
        }
    }
}

