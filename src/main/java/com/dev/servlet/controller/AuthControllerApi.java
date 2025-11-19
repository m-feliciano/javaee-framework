package com.dev.servlet.controller;

import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.Property;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.request.LoginRequest;
import com.dev.servlet.domain.response.UserResponse;

import static com.dev.servlet.domain.model.enums.RequestMethod.POST;

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

