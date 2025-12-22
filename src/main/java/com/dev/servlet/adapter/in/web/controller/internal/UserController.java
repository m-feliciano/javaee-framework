package com.dev.servlet.adapter.in.web.controller.internal;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.annotation.Cache;
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
import com.dev.servlet.application.transfer.response.UserProfile;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.shared.vo.Query;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

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

    @Override
    protected Class<UserController> implementation() {
        return UserController.class;
    }

    @SneakyThrows
    @Override
    @Cache(invalidate = "users_cache")
    public IHttpResponse<UserResponse> update(UserRequest user, @Authorization String auth) {
        UserResponse response = updateUserUseCase.update(user, auth);
        return newHttpResponse(204, response, redirectToCtx("me"));
    }

    @SneakyThrows
    @Override
    @Cache(invalidate = "users_cache")
    public IHttpResponse<Void> delete(UserRequest user, @Authorization String auth) {
        deleteUserUseCase.delete(user.id(), auth);
        return newHttpResponse(200, REDIRECT_AUTH_FORM);
    }

    @Override
    @Cache(value = "users_cache", duration = 1, timeUnit = TimeUnit.HOURS)
    public IHttpResponse<UserResponse> find(@Authorization String auth) {
        UserResponse response = userDetailsUseCase.getDetail(auth);
        return okHttpResponse(response, forwardTo("formListUser"));
    }

    @Override
    @Cache(value = "profile_cache")
    public IHttpResponse<UserProfile> profile(@Authorization String auth) {
        UserResponse response = userDetailsUseCase.getDetail(auth);
        UserProfile profile = new UserProfile(response.getLogin(), response.getImgUrl());
        return HttpResponse.ok(profile).build();
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
    @Cache(invalidate = {"users_cache", "profile_cache"})
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
    @Cache(invalidate = {"users_cache", "profile_cache"})
    public IHttpResponse<Void> updateProfilePicture(FileUploadRequest request, @Authorization String auth) {
        updateProfilePicturePort.updatePicture(request, auth);
        return newHttpResponse(204, redirectToCtx("me"));
    }
}
