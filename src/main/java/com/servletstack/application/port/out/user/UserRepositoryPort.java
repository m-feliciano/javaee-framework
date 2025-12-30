package com.servletstack.application.port.out.user;

import com.servletstack.domain.entity.Credentials;
import com.servletstack.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    Optional<User> findById(UUID userId);

    User update(User user);

    Optional<User> find(User user);

    User save(User newUser);

    void delete(User user);

    void updateCredentials(UUID userId, Credentials credentials);
}

