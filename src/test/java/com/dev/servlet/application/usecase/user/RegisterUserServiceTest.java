package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.user.GenerateConfirmationTokenUseCase;
import com.dev.servlet.application.port.out.AsyncMessagePort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.request.UserCreateRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.Credentials;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RegisterUserUseCase Tests")
class RegisterUserServiceTest {

    private static final String TEST_LOGIN = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final String CONFIRMATION_TOKEN = "token-abc-123";
    @Mock
    private UserRepositoryPort repositoryPort;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AsyncMessagePort messagePort;
    @Mock
    private GenerateConfirmationTokenUseCase generateConfirmationTokenUseCase;
    @InjectMocks
    private RegisterUserService registerUserService;
    private UserCreateRequest userCreateRequest;

    @BeforeEach
    void setUp() {
        userCreateRequest = new UserCreateRequest(TEST_LOGIN, TEST_PASSWORD, TEST_PASSWORD);

        User mockUser = User.builder()
                .id(USER_ID)
                .credentials(Credentials.builder()
                        .login(TEST_LOGIN.toLowerCase())
                        .password("$2a$10$hashedPassword")
                        .build())
                .status(Status.PENDING.getValue())
                .build();

        lenient()
                .when(repositoryPort.find(any(User.class)))
                .thenReturn(Optional.empty());
        lenient()
                .when(repositoryPort.save(any(User.class)))
                .thenReturn(mockUser);
        lenient()
                .when(generateConfirmationTokenUseCase.generateFor(any(User.class), isNull()))
                .thenReturn(CONFIRMATION_TOKEN);
        lenient()
                .doNothing()
                .when(messagePort).sendConfirmation(anyString(), anyString());
    }

    @Nested
    @DisplayName("Successful Registration Tests")
    class SuccessfulRegistrationTests {

        @Test
        @DisplayName("Should register new user successfully")
        void shouldRegisterNewUserSuccessfully() {
            // Act
            UserResponse response = registerUserService.register(userCreateRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(USER_ID);
            assertThat(response.getCreated()).isTrue();

            verify(repositoryPort).find(any(User.class));
            verify(repositoryPort).save(any(User.class));
            verify(generateConfirmationTokenUseCase).generateFor(any(User.class), isNull());
            verify(messagePort).sendConfirmation(anyString(), anyString());
        }

        @Test
        @DisplayName("Should check if user already exists")
        void shouldCheckIfUserExists() {
            // Act
            registerUserService.register(userCreateRequest);

            // Assert
            verify(repositoryPort).find(argThat(u ->
                    u.getCredentials() != null &&
                    TEST_LOGIN.toLowerCase().equals(u.getCredentials().getLogin())
            ));
        }

        @Test
        @DisplayName("Should save user with PENDING status")
        void shouldSaveUserWithPendingStatus() {
            // Act
            registerUserService.register(userCreateRequest);

            // Assert
            verify(repositoryPort).save(argThat(u ->
                    Status.PENDING.getValue().equals(u.getStatus())
            ));
        }

        @Test
        @DisplayName("Should convert login to lowercase")
        void shouldConvertLoginToLowercase() {
            // Arrange
            UserCreateRequest upperCaseRequest = new UserCreateRequest("TEST@EXAMPLE.COM", TEST_PASSWORD, TEST_PASSWORD);

            // Act
            registerUserService.register(upperCaseRequest);

            // Assert
            verify(repositoryPort).save(argThat(u ->
                    u.getCredentials() != null &&
                    "test@example.com".equals(u.getCredentials().getLogin())
            ));
        }

        @Test
        @DisplayName("Should generate confirmation token")
        void shouldGenerateConfirmationToken() {
            // Act
            registerUserService.register(userCreateRequest);

            // Assert
            verify(generateConfirmationTokenUseCase).generateFor(any(User.class), isNull());
        }

        @Test
        @DisplayName("Should send confirmation email")
        void shouldSendConfirmationEmail() {
            // Act
            registerUserService.register(userCreateRequest);

            // Assert
            verify(messagePort).sendConfirmation(eq(TEST_LOGIN.toLowerCase()), contains("token=" + CONFIRMATION_TOKEN));
        }

        @Test
        @DisplayName("Should return response with created flag true")
        void shouldReturnResponseWithCreatedFlag() {
            // Act
            UserResponse response = registerUserService.register(userCreateRequest);

            // Assert
            assertThat(response.getCreated()).isTrue();
        }
    }

    @Nested
    @DisplayName("Password Validation Tests")
    class PasswordValidationTests {

        @Test
        @DisplayName("Should throw exception when passwords do not match")
        void shouldThrowExceptionWhenPasswordsDoNotMatch() {
            // Arrange
            UserCreateRequest mismatchRequest = new UserCreateRequest(TEST_LOGIN, TEST_PASSWORD, "differentPassword");

            // Act & Assert
            assertThatThrownBy(() -> registerUserService.register(mismatchRequest))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("Passwords do not match");

            verify(repositoryPort, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when password is null")
        void shouldThrowExceptionWhenPasswordIsNull() {
            // Arrange
            UserCreateRequest nullPasswordRequest = new UserCreateRequest(TEST_LOGIN, null, null);

            // Act & Assert
            assertThatThrownBy(() -> registerUserService.register(nullPasswordRequest))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("Passwords do not match");

            verify(repositoryPort, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when confirm password is null")
        void shouldThrowExceptionWhenConfirmPasswordIsNull() {
            // Arrange
            UserCreateRequest nullConfirmRequest = new UserCreateRequest(TEST_LOGIN, TEST_PASSWORD, null);

            // Act & Assert
            assertThatThrownBy(() -> registerUserService.register(nullConfirmRequest))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("Passwords do not match");

            verify(repositoryPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("User Already Exists Tests")
    class UserAlreadyExistsTests {

        @Test
        @DisplayName("Should throw exception when user already exists")
        void shouldThrowExceptionWhenUserExists() {
            // Arrange
            User existingUser = User.builder()
                    .id(UUID.randomUUID())
                    .credentials(Credentials.builder()
                            .login(TEST_LOGIN.toLowerCase())
                            .build())
                    .build();

            when(repositoryPort.find(any(User.class))).thenReturn(Optional.of(existingUser));

            // Act & Assert
            assertThatThrownBy(() -> registerUserService.register(userCreateRequest))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("Cannot register this user");

            verify(repositoryPort, never()).save(any());
        }

        @Test
        @DisplayName("Should not send email when user already exists")
        void shouldNotSendEmailWhenUserExists() {
            // Arrange
            User existingUser = User.builder()
                    .credentials(Credentials.builder()
                            .login(TEST_LOGIN.toLowerCase())
                            .build())
                    .build();

            when(repositoryPort.find(any(User.class))).thenReturn(Optional.of(existingUser));

            // Act & Assert
            assertThatThrownBy(() -> registerUserService.register(userCreateRequest))
                    .isInstanceOf(AppException.class);

            verify(messagePort, never()).sendConfirmation(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Execution Order Tests")
    class ExecutionOrderTests {

        @Test
        @DisplayName("Should execute operations in correct order")
        void shouldExecuteInCorrectOrder() {
            // Act
            registerUserService.register(userCreateRequest);

            // Assert - Verify order
            var inOrder = inOrder(repositoryPort, generateConfirmationTokenUseCase, messagePort);
            inOrder.verify(repositoryPort).find(any(User.class));
            inOrder.verify(repositoryPort).save(any(User.class));
            inOrder.verify(generateConfirmationTokenUseCase).generateFor(any(User.class), isNull());
            inOrder.verify(messagePort).sendConfirmation(anyString(), anyString());
        }

        @Test
        @DisplayName("Should check existence before saving")
        void shouldCheckExistenceBeforeSaving() {
            // Act
            registerUserService.register(userCreateRequest);

            // Assert
            var inOrder = inOrder(repositoryPort);
            inOrder.verify(repositoryPort).find(any(User.class));
            inOrder.verify(repositoryPort).save(any(User.class));
        }

        @Test
        @DisplayName("Should generate token before sending email")
        void shouldGenerateTokenBeforeSendingEmail() {
            // Act
            registerUserService.register(userCreateRequest);

            // Assert
            var inOrder = inOrder(generateConfirmationTokenUseCase, messagePort);
            inOrder.verify(generateConfirmationTokenUseCase).generateFor(any(User.class), isNull());
            inOrder.verify(messagePort).sendConfirmation(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Email Format Tests")
    class EmailFormatTests {

        @Test
        @DisplayName("Should send email with confirmation URL")
        void shouldSendEmailWithConfirmationUrl() {
            // Act
            registerUserService.register(userCreateRequest);

            // Assert
            verify(messagePort).sendConfirmation(
                    eq(TEST_LOGIN.toLowerCase()),
                    argThat(url -> url.contains("/api/v1/user/confirm?token="))
            );
        }

        @Test
        @DisplayName("Should include token in confirmation URL")
        void shouldIncludeTokenInUrl() {
            // Act
            registerUserService.register(userCreateRequest);

            // Assert
            verify(messagePort).sendConfirmation(
                    anyString(),
                    contains(CONFIRMATION_TOKEN)
            );
        }
    }
}

