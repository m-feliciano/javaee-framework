package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.controller.internal.UserController;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.port.in.user.ChangeEmailPort;
import com.dev.servlet.application.port.in.user.ConfirmEmailPort;
import com.dev.servlet.application.port.in.user.DeleteUserPort;
import com.dev.servlet.application.port.in.user.RegisterUserPort;
import com.dev.servlet.application.port.in.user.ResendConfirmationPort;
import com.dev.servlet.application.port.in.user.UpdateProfilePicturePort;
import com.dev.servlet.application.port.in.user.UpdateUserPort;
import com.dev.servlet.application.port.in.user.UserDetailsPort;
import com.dev.servlet.application.transfer.request.FileUploadRequest;
import com.dev.servlet.application.transfer.request.UserCreateRequest;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.vo.BinaryPayload;
import com.dev.servlet.shared.vo.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("UserController Tests")
class UserControllerTest extends BaseControllerTest {

    @Mock
    private UpdateUserPort updateUserPort;
    @Mock
    private DeleteUserPort deleteUserPort;
    @Mock
    private RegisterUserPort registerUserPort;
    @Mock
    private ConfirmEmailPort confirmEmailPort;
    @Mock
    private ChangeEmailPort changeEmailPort;
    @Mock
    private ResendConfirmationPort resendConfirmationPort;
    @Mock
    private UserDetailsPort userDetailsPort;
    @Mock
    private UpdateProfilePicturePort updateProfilePicturePort;

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
                    .id("user-123")
                    .login("newuser@example.com")
                    .build();

            when(registerUserPort.register(any(UserCreateRequest.class))).thenReturn(expectedResponse);

            // Act
            IHttpResponse<UserResponse> response = userController.register(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(expectedResponse);

            verify(registerUserPort).register(request);
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

            when(updateUserPort.update(any(UserRequest.class), eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedResponse);

            // Act
            IHttpResponse<UserResponse> response = userController.update(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(expectedResponse);

            verify(updateUserPort).update(request, VALID_AUTH_TOKEN);
        }
    }

    @Nested
    @DisplayName("User Deletion Tests")
    class DeletionTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUser() {
            // Arrange
            UserRequest request = UserRequest.builder().id("user-to-delete").build();

            doNothing().when(deleteUserPort).delete(eq("user-to-delete"), eq(VALID_AUTH_TOKEN));

            // Act
            IHttpResponse<Void> response = userController.delete(request, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            verify(deleteUserPort).delete("user-to-delete", VALID_AUTH_TOKEN);
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

            when(userDetailsPort.getDetail(eq(VALID_AUTH_TOKEN)))
                    .thenReturn(expectedUser);

            // Act
            IHttpResponse<UserResponse> response = userController.find(VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.body()).isEqualTo(expectedUser);

            verify(userDetailsPort).getDetail(VALID_AUTH_TOKEN);
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

            doNothing().when(confirmEmailPort).confirm(any());

            // Act
            IHttpResponse<Void> response = userController.confirm(query);

            // Assert
            assertThat(response).isNotNull();
            verify(confirmEmailPort).confirm(any());
        }

        @Test
        @DisplayName("Should resend confirmation email")
        void shouldResendConfirmation() {
            // Arrange
            User user = User.builder().id(USER_ID).build();
            doNothing().when(resendConfirmationPort).resend(any());

            // Act
            IHttpResponse<Void> response = userController.resendConfirmation(user);

            // Assert
            assertThat(response).isNotNull();
            verify(resendConfirmationPort).resend(any());
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

            doNothing().when(changeEmailPort).change(eq("email-change-token"));

            // Act
            IHttpResponse<Void> response = userController.changeEmail(query);

            // Assert
            assertThat(response).isNotNull();
            verify(changeEmailPort).change("email-change-token");
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

            doNothing().when(updateProfilePicturePort).updatePicture(any(FileUploadRequest.class), eq(VALID_AUTH_TOKEN));

            // Act
            IHttpResponse<Void> response = userController.updateProfilePicture(uploadRequest, VALID_AUTH_TOKEN);

            // Assert
            assertThat(response).isNotNull();
            verify(updateProfilePicturePort).updatePicture(uploadRequest, VALID_AUTH_TOKEN);
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

