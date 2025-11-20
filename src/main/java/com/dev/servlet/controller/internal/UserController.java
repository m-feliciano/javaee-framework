package com.dev.servlet.controller.internal;

import com.dev.servlet.controller.UserControllerApi;
import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.request.UserCreateRequest;
import com.dev.servlet.domain.request.UserRequest;
import com.dev.servlet.domain.response.UserResponse;
import com.dev.servlet.service.IUserService;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.inject.Inject;
import javax.inject.Singleton;

@NoArgsConstructor
@Singleton
public class UserController extends BaseController implements UserControllerApi {

    @Inject
    private IUserService userService;

    @SneakyThrows
    public IHttpResponse<UserResponse> update(UserRequest user, String auth) {
        UserResponse response = userService.update(user, auth);
        return newHttpResponse(204, response, redirectTo(response.getId()));
    }

    @SneakyThrows
    public IHttpResponse<Void> delete(UserRequest user, String auth) {
        userService.delete(user, auth);
        return HttpResponse.<Void>next(forwardTo("formLogin")).build();
    }

    @SneakyThrows
    public IHttpResponse<Void> register(UserCreateRequest user) {
        UserResponse response = userService.register(user);
        return newHttpResponse(201, "redirect:/api/v1/login/form");
    }

    @SneakyThrows
    public IHttpResponse<UserResponse> getUserDetail(UserRequest user, String auth) {
        UserResponse response = userService.getUserDetail(user, auth);
        return okHttpResponse(response, forwardTo("formListUser"));
    }
}
