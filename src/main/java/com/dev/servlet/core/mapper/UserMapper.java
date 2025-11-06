package com.dev.servlet.core.mapper;

import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.transfer.request.UserRequest;
import com.dev.servlet.domain.transfer.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface UserMapper {
    @Mapping(target = "login", source = "credentials.login")
    @Mapping(target = "password", source = "credentials.password")
    UserResponse toResponse(User user);

    User toUser(UserRequest userRequest);
}
