package com.servletstack.adapter.in.web.controller;

import com.servletstack.adapter.in.web.annotation.Controller;
import com.servletstack.adapter.in.web.annotation.RequestMapping;
import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.application.transfer.request.FileUploadRequest;
import com.servletstack.application.transfer.request.UserCreateRequest;
import com.servletstack.application.transfer.request.UserRequest;
import com.servletstack.application.transfer.response.UserProfile;
import com.servletstack.application.transfer.response.UserResponse;
import com.servletstack.domain.entity.User;
import com.servletstack.domain.entity.enums.RoleType;
import com.servletstack.shared.vo.Query;

import static com.servletstack.domain.entity.enums.RequestMethod.POST;

@Controller("user")
public interface UserControllerApi {
    @RequestMapping(
            value = "/update",
            method = POST,
            jsonType = UserRequest.class,
            description = "Update user information."
    )
    IHttpResponse<UserResponse> update(UserRequest user, String auth);

    @RequestMapping(
            value = "/delete",
            roles = RoleType.ADMIN,
            method = POST,
            jsonType = UserRequest.class,
            description = "Delete a user by ID. Requires ADMIN role."
    )
    IHttpResponse<Void> delete(UserRequest user, String auth);

    @RequestMapping(
            value = "/me",
            jsonType = UserRequest.class,
            description = "Retrieve user information by ID."
    )
    IHttpResponse<UserResponse> find(String auth);

    @RequestMapping(value = "/profile", description = "Retrieve user profile information.")
    IHttpResponse<UserProfile> profile(String auth);

    @RequestMapping(
            requestAuth = false,
            value = "/registerUser",
            method = POST,
            jsonType = UserCreateRequest.class,
            description = "Register a new user."
    )
    IHttpResponse<UserResponse> register(UserCreateRequest user);

    @RequestMapping(
            requestAuth = false,
            value = "/confirm",
            description = "Confirm user email registration."
    )
    IHttpResponse<Void> confirm(Query query);

    @RequestMapping(
            requestAuth = false,
            value = "/email-change-confirmation",
            description = "Confirm user email change."
    )
    IHttpResponse<Void> changeEmail(Query query);

    @RequestMapping(
            requestAuth = false,
            value = "/resend-confirmation",
            method = POST,
            description = "Resend confirmation email to user. The message will be sent to the email associated with the provided user information. V2 API."
    )
    IHttpResponse<Void> resendConfirmation(User user);

    @RequestMapping(
            value = "/upload-photo",
            apiVersion = "v2",
            method = POST,
            jsonType = FileUploadRequest.class,
            description = "Update user profile picture. Accepts file upload. V2 API."
    )
    IHttpResponse<Void> updateProfilePicture(FileUploadRequest request, String auth);
}
