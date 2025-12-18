package com.dev.servlet.application.mapper;

import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.FileImage;
import com.dev.servlet.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface UserMapper {
    @Mapping(target = "login", source = "credentials.login")
    @Mapping(target = "password", source = "credentials.password")
    @Mapping(target = "roles", source = "perfis")
    @Mapping(target = "imgUrl", source = "images", qualifiedByName = "firstElement")
    UserResponse toResponse(User user);

    User toUser(UserRequest userRequest);

    @Named("firstElement")
    default String firstElement(List<FileImage> list) {
        if (list != null && !list.isEmpty()) {
            return list.getFirst().getUri();
        }
        return null;
    }
}
