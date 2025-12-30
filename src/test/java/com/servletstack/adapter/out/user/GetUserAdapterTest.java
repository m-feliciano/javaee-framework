package com.servletstack.adapter.out.user;

import com.servletstack.application.port.out.user.UserRepositoryPort;
import com.servletstack.application.transfer.request.UserRequest;
import com.servletstack.domain.entity.Credentials;
import com.servletstack.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserAdapter Tests")
class GetUserAdapterTest {

    private static final String TEST_LOGIN = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    @Mock
    private UserRepositoryPort repositoryPort;
    @InjectMocks
    private GetUserAdapter getUserAdapter;
    private UserRequest userRequest;
    private User user;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest(TEST_LOGIN, TEST_PASSWORD);

        user = User.builder()
                .id(UUID.randomUUID())
                .credentials(Credentials.builder()
                        .login(TEST_LOGIN)
                        .password(TEST_PASSWORD)
                        .build())
                .build();

        lenient().when(repositoryPort.find(any(User.class))).thenReturn(Optional.of(user));
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should get user when exists")
        void shouldGetUserWhenExists() {
            // Act
            Optional<User> result = getUserAdapter.get(userRequest);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(user);
            verify(repositoryPort).find(any(User.class));
        }

        @Test
        @DisplayName("Should return empty when user not found")
        void shouldReturnEmptyWhenUserNotFound() {
            // Arrange
            when(repositoryPort.find(any(User.class))).thenReturn(Optional.empty());

            // Act
            Optional<User> result = getUserAdapter.get(userRequest);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should delegate to repository")
        void shouldDelegateToRepository() {
            // Act
            getUserAdapter.get(userRequest);

            // Assert
            verify(repositoryPort).find(any(User.class));
        }
    }

    @Nested
    @DisplayName("User Construction Tests")
    class UserConstructionTests {

        @Test
        @DisplayName("Should create user with login and password from request")
        void shouldCreateUserWithLoginAndPassword() {
            // Act
            getUserAdapter.get(userRequest);

            // Assert
            verify(repositoryPort).find(argThat(u ->
                    u.getLogin() != null && u.getLogin().equals(TEST_LOGIN)
            ));
        }

        @Test
        @DisplayName("Should search user by credentials")
        void shouldSearchUserByCredentials() {
            // Arrange
            UserRequest customRequest = new UserRequest("custom@example.com", "customPass");

            // Act
            getUserAdapter.get(customRequest);

            // Assert
            verify(repositoryPort).find(argThat(u ->
                    u.getLogin() != null && u.getLogin().equals("custom@example.com")
            ));
        }
    }

    @Nested
    @DisplayName("Different Request Scenarios Tests")
    class DifferentRequestScenariosTests {

        @Test
        @DisplayName("Should handle request with login only")
        void shouldHandleRequestWithLoginOnly() {
            // Arrange
            UserRequest loginOnlyRequest = new UserRequest(TEST_LOGIN, null);

            // Act
            getUserAdapter.get(loginOnlyRequest);

            // Assert
            verify(repositoryPort).find(any(User.class));
        }

        @Test
        @DisplayName("Should handle request with password only")
        void shouldHandleRequestWithPasswordOnly() {
            // Arrange
            UserRequest passwordOnlyRequest = new UserRequest(null, TEST_PASSWORD);

            // Act
            getUserAdapter.get(passwordOnlyRequest);

            // Assert
            verify(repositoryPort).find(any(User.class));
        }

        @Test
        @DisplayName("Should handle request with null values")
        void shouldHandleRequestWithNullValues() {
            // Arrange
            UserRequest nullRequest = new UserRequest(null, null);

            // Act
            getUserAdapter.get(nullRequest);

            // Assert
            verify(repositoryPort).find(any(User.class));
        }
    }

    @Nested
    @DisplayName("Return Value Tests")
    class ReturnValueTests {

        @Test
        @DisplayName("Should return Optional with user")
        void shouldReturnOptionalWithUser() {
            // Act
            Optional<User> result = getUserAdapter.get(userRequest);

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result.get().getId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("Should preserve user data")
        void shouldPreserveUserData() {
            // Act
            Optional<User> result = getUserAdapter.get(userRequest);

            // Assert
            assertThat(result).isPresent();
            User foundUser = result.get();
            assertThat(foundUser.getLogin()).isEqualTo(TEST_LOGIN);
        }
    }
}

