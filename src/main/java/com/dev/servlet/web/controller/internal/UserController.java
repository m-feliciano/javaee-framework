package com.dev.servlet.web.controller.internal;

import com.dev.servlet.application.port.in.user.ChangeEmailUseCasePort;
import com.dev.servlet.application.port.in.user.ConfirmEmailUseCasePort;
import com.dev.servlet.application.port.in.user.DeleteUserUseCasePort;
import com.dev.servlet.application.port.in.user.RegisterUserUseCasePort;
import com.dev.servlet.application.port.in.user.ResendConfirmationUseCasePort;
import com.dev.servlet.application.port.in.user.UpdateUserUseCasePort;
import com.dev.servlet.application.port.in.user.UserDetailsUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.transfer.request.ConfirmEmailRequest;
import com.dev.servlet.application.transfer.request.ResendConfirmationRequest;
import com.dev.servlet.application.transfer.request.UserCreateRequest;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.valueobject.Query;
import com.dev.servlet.web.controller.UserControllerApi;
import com.dev.servlet.web.controller.internal.base.BaseController;
import com.dev.servlet.web.response.HttpResponse;
import com.dev.servlet.web.response.IHttpResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@ApplicationScoped
@NoArgsConstructor
public class UserController extends BaseController implements UserControllerApi {
    private static final String REDIRECT_AUTH_FORM = "redirect:/api/v1/auth/form";
    private static final String FORM_LOGIN_FORM = "forward:pages/formLogin.jsp";

    @Inject
    private UpdateUserUseCasePort updateUserUseCase;
    @Inject
    private DeleteUserUseCasePort deleteUserUseCase;
    @Inject
    private RegisterUserUseCasePort registerUserUseCase;
    @Inject
    private ConfirmEmailUseCasePort confirmEmailUseCase;
    @Inject
    private ChangeEmailUseCasePort changeEmailUseCase;
    @Inject
    private ResendConfirmationUseCasePort resendConfirmationUseCase;
    @Inject
    private UserDetailsUseCasePort userDetailsUseCase;
    @Inject
    private AuditPort auditPort;

    @SneakyThrows
    @Override
    public IHttpResponse<UserResponse> update(UserRequest user, String auth) {
        UserResponse response = updateUserUseCase.update(user, auth);
        return newHttpResponse(204, response, redirectTo(response.getId()));
    }

    @SneakyThrows
    @Override
    public IHttpResponse<Void> delete(UserRequest user, String auth) {
        deleteUserUseCase.delete(user.id(), auth);
        return HttpResponse.<Void>next(FORM_LOGIN_FORM).build();
    }

    @SneakyThrows
    @Override
    public IHttpResponse<UserResponse> findById(UserRequest user, String auth) {
        try {
            UserResponse response = userDetailsUseCase.get(user.id(), auth);
            auditPort.success(response.getId(), auth, "User details accessed.");
            return okHttpResponse(response, forwardTo("formListUser"));
        } catch (Exception e) {
            auditPort.failure(user.id(), auth, "Failed to access user details.");
            throw e;
        }
    }

    @SneakyThrows
    @Override
    public IHttpResponse<UserResponse> register(UserCreateRequest user) {
        UserResponse response = registerUserUseCase.register(user);
        return newHttpResponse(201, response, FORM_LOGIN_FORM);
    }

    @SneakyThrows
    @Override
    public IHttpResponse<Void> confirm(Query query) {
        String token = query.get("token");
        confirmEmailUseCase.confirm(new ConfirmEmailRequest(token));
        return newHttpResponse(200, REDIRECT_AUTH_FORM);
    }

    @SneakyThrows
    @Override
    public IHttpResponse<Void> changeEmail(Query query) {
        changeEmailUseCase.change(query.get("token"));
        return newHttpResponse(200, REDIRECT_AUTH_FORM);
    }

    @SneakyThrows
    @Override
    public IHttpResponse<Void> resendConfirmation(User user) {
        ResendConfirmationRequest req = new ResendConfirmationRequest(user.getId());
        resendConfirmationUseCase.resend(req);
        return HttpResponse.<Void>next(FORM_LOGIN_FORM).build();
    }
}
