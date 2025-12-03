package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.transfer.request.UserCreateRequest;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.RoleType;
import com.dev.servlet.shared.vo.Query;

import static com.dev.servlet.domain.entity.enums.RequestMethod.POST;

@Controller("user")
public interface UserControllerApi {
    @RequestMapping(value = "/update/{id}", method = POST, jsonType = UserRequest.class)
    IHttpResponse<UserResponse> update(UserRequest user, @Authorization String auth);

    @RequestMapping(value = "/delete/{id}", roles = RoleType.ADMIN, method = POST, jsonType = UserRequest.class)
    IHttpResponse<Void> delete(UserRequest user, @Authorization String auth);

    @RequestMapping(value = "/list/{id}", jsonType = UserRequest.class)
    IHttpResponse<UserResponse> findById(UserRequest user, @Authorization String auth);

    @RequestMapping(requestAuth = false, value = "/registerUser", method = POST, jsonType = UserCreateRequest.class)
    IHttpResponse<UserResponse> register(UserCreateRequest user);

    @RequestMapping(requestAuth = false, value = "/confirm")
    IHttpResponse<Void> confirm(Query query);

    @RequestMapping(requestAuth = false, value = "/email-change-confirmation")
    IHttpResponse<Void> changeEmail(Query query);

    @RequestMapping(requestAuth = false, value = "/resend-confirmation", method = POST)
    IHttpResponse<Void> resendConfirmation(User user);
}
