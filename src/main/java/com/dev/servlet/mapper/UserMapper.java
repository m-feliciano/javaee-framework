package com.dev.servlet.mapper;

import com.dev.servlet.domain.User;
import com.dev.servlet.dto.UserDto;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserDto from(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto(user.getId());
        dto.setLogin(user.getLogin());
        dto.setToken(user.getToken());
        dto.setImgUrl(user.getImgUrl());
        dto.setStatus(user.getStatus());
        dto.setPerfis(user.getPerfis());
        return dto;
    }

    public static UserDto onlyId(User user) {
        if (user == null) return null;
        UserDto userDto = new UserDto(user.getId());
        userDto.setLogin(user.getLogin());
        return userDto;
    }

    public static User from(UserDto dto) {
        if (dto == null) return null;
        User user = new User(dto.getId());
        user.setLogin(dto.getLogin());
        user.setStatus(dto.getStatus());
        user.setToken(dto.getToken());
        user.setImgUrl(dto.getImgUrl());
        user.setPassword(dto.getPassword());
        user.setPerfis(dto.getPerfis());
        return user;
    }
}
