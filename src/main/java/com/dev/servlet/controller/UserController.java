package com.dev.servlet.controller;

import com.dev.servlet.controller.base.BaseController;
import com.dev.servlet.core.annotation.Authentication;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.model.enums.RoleType;
import com.dev.servlet.domain.service.IUserService;
import com.dev.servlet.domain.transfer.request.UserCreateRequest;
import com.dev.servlet.domain.transfer.request.UserRequest;
import com.dev.servlet.domain.transfer.response.UserResponse;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.dev.servlet.domain.model.enums.RequestMethod.POST;

@NoArgsConstructor
@Singleton
@Controller("user")
public class UserController extends BaseController {

    @Inject
    private IUserService userService;

    @RequestMapping(value = "/update/{id}", method = POST, jsonType = UserRequest.class)
    @SneakyThrows
    public IHttpResponse<UserResponse> update(UserRequest user, @Authentication String auth) {
        UserResponse response = userService.update(user, auth);
        return newHttpResponse(204, response, redirectTo(response.getId()));
    }

    @RequestMapping(value = "/delete/{id}", roles = RoleType.ADMIN, method = POST, jsonType = UserRequest.class)
    @SneakyThrows
    public IHttpResponse<Void> delete(UserRequest user, @Authentication String auth) {
        userService.delete(user, auth);
        return HttpResponse.<Void>next(forwardTo("formLogin")).build();
    }

    @RequestMapping(requestAuth = false, value = "/registerUser", method = POST, jsonType = UserCreateRequest.class)
    @SneakyThrows
    public IHttpResponse<Void> register(UserCreateRequest user) {
        UserResponse response = userService.register(user);
        return newHttpResponse(201, "redirect:/api/v1/login/form");
    }

    @RequestMapping(value = "/list/{id}", jsonType = UserRequest.class)
    @SneakyThrows
    public IHttpResponse<UserResponse> getUserDetail(UserRequest user, @Authentication String auth) {
        UserResponse response = userService.getUserDetail(user, auth);
        return okHttpResponse(response, forwardTo("formListUser"));
    }
}
