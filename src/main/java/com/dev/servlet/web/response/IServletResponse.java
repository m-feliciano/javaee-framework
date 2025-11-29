package com.dev.servlet.web.response;

import com.dev.servlet.domain.valueobject.KeyPair;

import java.util.Optional;
import java.util.Set;

public interface IServletResponse extends IHttpResponse<Set<KeyPair>> {
    default Object getEntity(String key) {
        return Optional.of(body())
                .flatMap(response -> response.stream()
                        .filter(pair -> pair.getKey().equals(key))
                        .findFirst()
                        .map(KeyPair::getValue)
                )
                .orElse(null);
    }

    @Override
    default String error() {
        return null;
    }

    @Override
    default String reasonText() {
        return null;
    }
}
