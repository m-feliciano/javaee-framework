package com.dev.servlet.application.port.out.user;

import com.dev.servlet.domain.entity.Credentials;
import com.dev.servlet.domain.entity.User;

import java.util.Optional;

public interface UserRepositoryPort {

    Optional<User> findById(String userId);

    User update(User user);

    Optional<User> find(User user);

    User save(User newUser);

    void delete(User user);

    void updateCredentials(String userId, Credentials credentials);
}

