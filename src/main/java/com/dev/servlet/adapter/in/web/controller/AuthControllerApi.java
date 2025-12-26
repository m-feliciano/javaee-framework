package com.dev.servlet.adapter.in.web.controller;

import com.dev.servlet.adapter.in.web.annotation.Controller;
import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.request.RefreshTokenRequest;
import com.dev.servlet.application.transfer.response.RefreshTokenResponse;
import com.dev.servlet.application.transfer.response.UserResponse;

import static com.dev.servlet.domain.entity.enums.RequestMethod.POST;

@Controller("auth")
public interface AuthControllerApi {
    @RequestMapping(
            value = "/registerPage",
            requestAuth = false,
            description = "Forward to the registration page."
    )
    IHttpResponse<String> forwardRegister();

    @RequestMapping(
            value = "/form",
            requestAuth = false,
            description = "Retrieve the registration form."
    )
    IHttpResponse<String> form(String auth, String homepage);

    @RequestMapping(
            value = "/login",
            method = POST,
            requestAuth = false,
            jsonType = LoginRequest.class,
            description = "Authenticate user and return user details."
    )
    IHttpResponse<UserResponse> login(LoginRequest request, String homepage);

    @RequestMapping(
            value = "/logout",
            method = POST,
            description = "Logout the authorized user."
    )
    IHttpResponse<String> logout(String auth);

    @RequestMapping(
            value = "/refresh-token",
            method = POST,
            apiVersion = "v2",
            requestAuth = false,
            description = "Refresh the authentication token."
    )
    IHttpResponse<RefreshTokenResponse> refreshToken(RefreshTokenRequest refreshToken);
}
