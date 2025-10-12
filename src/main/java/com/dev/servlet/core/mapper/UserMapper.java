package com.dev.servlet.core.mapper;

import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.transfer.response.UserResponse;
import com.dev.servlet.domain.transfer.request.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface UserMapper {
    UserResponse toResponse(User user);

    User toUser(UserRequest userRequest);
}
