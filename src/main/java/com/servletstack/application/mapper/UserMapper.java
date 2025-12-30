package com.servletstack.application.mapper;

import com.servletstack.application.transfer.response.UserResponse;
import com.servletstack.domain.entity.FileImage;
import com.servletstack.domain.entity.User;
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

    @Named("firstElement")
    default String firstElement(List<FileImage> list) {
        if (list != null && !list.isEmpty()) {
            return list.getFirst().getUri();
        }
        return null;
    }
}
