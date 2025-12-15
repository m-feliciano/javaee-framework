package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.controller.UserControllerApi;
import com.dev.servlet.adapter.in.web.controller.internal.base.BaseController;
import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.port.in.user.ChangeEmailPort;
import com.dev.servlet.application.port.in.user.ConfirmEmailPort;
import com.dev.servlet.application.port.in.user.DeleteUserPort;
import com.dev.servlet.application.port.in.user.RegisterUserPort;
import com.dev.servlet.application.port.in.user.ResendConfirmationPort;
import com.dev.servlet.application.port.in.user.UpdateProfilePicturePort;
import com.dev.servlet.application.port.in.user.UpdateUserPort;
import com.dev.servlet.application.port.in.user.UserDetailsPort;
import com.dev.servlet.application.transfer.request.ConfirmEmailRequest;
import com.dev.servlet.application.transfer.request.FileUploadRequest;
import com.dev.servlet.application.transfer.request.ResendConfirmationRequest;
import com.dev.servlet.application.transfer.request.UserCreateRequest;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.shared.vo.Query;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

@ApplicationScoped
public class UserController extends BaseController implements UserControllerApi {
    private static final String REDIRECT_AUTH_FORM = "redirect:/api/v1/auth/form";
    private static final String FORM_LOGIN_FORM = "forward:pages/formLogin.jsp";

    @Inject
    private UpdateUserPort updateUserUseCase;
    @Inject
    private DeleteUserPort deleteUserUseCase;
    @Inject
    private RegisterUserPort registerUserUseCase;
    @Inject
    private ConfirmEmailPort confirmEmailUseCase;
    @Inject
    private ChangeEmailPort changeEmailUseCase;
    @Inject
    private ResendConfirmationPort resendConfirmationUseCase;
    @Inject
    private UserDetailsPort userDetailsUseCase;
    @Inject
    private UpdateProfilePicturePort updateProfilePicturePort;


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
        UserResponse response = userDetailsUseCase.getDetail(user.id(), auth);
        return okHttpResponse(response, forwardTo("formListUser"));
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

    @SneakyThrows
    @Override
    public IHttpResponse<Void> updateProfilePicture(FileUploadRequest request, String auth) {
        updateProfilePicturePort.updatePicture(request, auth);
        return newHttpResponse(204, redirectTo(request.id()));
    }
}
