package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.annotation.Authorization;
import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.annotation.Property;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.response.UserResponse;

import static com.dev.servlet.domain.entity.enums.RequestMethod.POST;

@Controller("auth")
public interface AuthControllerApi {
    @RequestMapping(value = "/registerPage", requestAuth = false)
    IHttpResponse<String> forwardRegister();

    @RequestMapping(value = "/form", requestAuth = false)
    IHttpResponse<String> form(@Authorization String auth, @Property("homepage") String homepage);

    @RequestMapping(value = "/login", method = POST, requestAuth = false, jsonType = LoginRequest.class)
    IHttpResponse<UserResponse> login(LoginRequest request, @Property("homepage") String homepage);

    @RequestMapping(value = "/logout", method = POST)
    IHttpResponse<String> logout(@Authorization String auth);
}
