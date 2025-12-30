package com.servletstack.adapter.in.web.controller;

import com.servletstack.adapter.in.web.controller.internal.UserController;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.application.port.in.user.ChangeEmailUseCase;
import com.servletstack.application.port.in.user.ConfirmEmailUseCase;
import com.servletstack.application.port.in.user.DeleteUserUseCase;
import com.servletstack.application.port.in.user.RegisterUserUseCase;
import com.servletstack.application.port.in.user.ResendConfirmationUseCase;
import com.servletstack.application.port.in.user.UpdateProfilePictureUseCase;
import com.servletstack.application.port.in.user.UpdateUserUseCase;
import com.servletstack.application.port.in.user.UserDetailsUseCase;
import com.servletstack.application.transfer.request.FileUploadRequest;
import com.servletstack.application.transfer.request.UserCreateRequest;
import com.servletstack.application.transfer.request.UserRequest;
import com.servletstack.application.transfer.response.UserResponse;
import com.servletstack.domain.entity.User;
import com.servletstack.domain.vo.BinaryPayload;
import com.servletstack.shared.vo.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("UserController Tests")
class UserControllerTest extends BaseControllerTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private UpdateUserUseCase updateUserUseCase;
    @Mock
    private DeleteUserUseCase deleteUserUseCase;
    @Mock
    private RegisterUserUseCase registerUserUseCase;
    @Mock
    private ConfirmEmailUseCase confirmEmailUseCase;
    @Mock
    private ChangeEmailUseCase changeEmailUseCase;
    @Mock
    private ResendConfirmationUseCase resendConfirmationUseCase;
    @Mock
    private UserDetailsUseCase userDetailsUseCase;
    @Mock
    private UpdateProfilePictureUseCase updateProfilePictureUseCase;

    @InjectMocks
    private UserController userController;

    @Override
    protected void setupAdditionalMocks() {
        userController.setJwtUtils(authenticationPort);
    }

    @Nested
    @DisplayName("User Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register new user successfully")
        void shouldRegisterNewUser() {
            // Arrange
            UserCreateRequest request = new UserCreateRequest(
                    "newuser@example.com",
                    "SecurePass123!",
                    "SecurePass123!"
            );

            UserResponse expectedResponse = UserResponse.builder()
                    .id(USER_ID)
                    .login("newuser@example.com")
                    .build();

            when(registerUserUseCase.register(any(UserCreateRequest.class))).thenReturn(expectedResponse);

            // Act
            IHttpResponse<UserResponse> response = userController.register(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(expectedResponse);

            verify(registerUserUseCase).register(request);
        }
    }

    @Nested
    @DisplayName("User Update Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUser() {
            // Arrange
            UserRequest request = UserRequest.builder()
                    .id(USER_ID)
                    .login("updateduser@example.com")
                    .build();

            UserResponse expectedResponse = UserResponse.builder()
                    .id(USER_ID)
                    .login("updateduser@example.com")
                    .build();

            when(updateUserUseCase.update(any(UserRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedResponse);

            // Act
            IHttpResponse<UserResponse> response = userController.update(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(expectedResponse);

            verify(updateUserUseCase).update(request, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("User Deletion Tests")
    class DeletionTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUser() {
            // Arrange
            UserRequest request = UserRequest.builder().id(USER_ID).build();

            doNothing().when(deleteUserUseCase).delete(eq(USER_ID), eq(VALID_AUTH_TOKEN));

            // Act
            IHttpResponse<Void> response = userController.delete(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            verify(deleteUserUseCase).delete(USER_ID, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("User Retrieval Tests")
    class RetrievalTests {

        @Test
        @DisplayName("Should find user by ID")
        void shouldFindUserById() {
            // Arrange
            UserResponse expectedUser = UserResponse.builder()
                    .id(USER_ID)
                    .login("test@example.com")
                    .build();

            when(userDetailsUseCase.getDetail(eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedUser);

            // Act
            IHttpResponse<UserResponse> response = userController.find(VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(expectedUser);

            verify(userDetailsUseCase).getDetail(VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Email Confirmation Tests")
    class EmailConfirmationTests {

        @Test
        @DisplayName("Should confirm email with valid token")
        void shouldConfirmEmail() {
            // Arrange
            Map<String, String> params = new HashMap<>();
            params.put("token", "valid-confirmation-token");
            Query query = Query.builder().parameters(params).build();

            doNothing().when(confirmEmailUseCase).confirm(any());

            // Act
            IHttpResponse<Void> response = userController.confirm(query);

            // Assert
            assertThat(response).isNotNull();
            verify(confirmEmailUseCase).confirm(any());
        }

        @Test
        @DisplayName("Should resend confirmation email")
        void shouldResendConfirmation() {
            // Arrange
            User user = User.builder().id(USER_ID).build();
            doNothing().when(resendConfirmationUseCase).resend(any());

            // Act
            IHttpResponse<Void> response = userController.resendConfirmation(user);

            // Assert
            assertThat(response).isNotNull();
            verify(resendConfirmationUseCase).resend(any());
        }
    }

    @Nested
    @DisplayName("Email Change Tests")
    class EmailChangeTests {

        @Test
        @DisplayName("Should change email with valid token")
        void shouldChangeEmail() {
            // Arrange
            Map<String, String> params = new HashMap<>();
            params.put("token", "email-change-token");
            Query query = Query.builder().parameters(params).build();

            doNothing().when(changeEmailUseCase).change(eq("email-change-token"));

            // Act
            IHttpResponse<Void> response = userController.changeEmail(query);

            // Assert
            assertThat(response).isNotNull();
            verify(changeEmailUseCase).change("email-change-token");
        }
    }

    @Nested
    @DisplayName("Profile Picture Tests")
    class ProfilePictureTests {

        @Test
        @DisplayName("Should update profile picture")
        void shouldUpdateProfilePicture() {
            // Arrange
            FileUploadRequest uploadRequest = new FileUploadRequest(
                    new BinaryPayload("/temp/path/avatar.jpg", 2048L, "image/jpeg"),
                    USER_ID);

            doNothing().when(updateProfilePictureUseCase).updatePicture(any(FileUploadRequest.class), eq(VALID_AUTH_TOKEN));

            // Act
            IHttpResponse<Void> response = userController.updateProfilePicture(uploadRequest, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            verify(updateProfilePictureUseCase).updatePicture(uploadRequest, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Controller Implementation Tests")
    class ImplementationTests {

        @Test
        @DisplayName("Should implement UserControllerApi interface")
        void shouldImplementInterface() {
            assertThat(userController).isInstanceOf(UserControllerApi.class);
        }
    }
}

