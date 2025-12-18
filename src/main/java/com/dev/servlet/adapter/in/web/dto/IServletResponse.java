package com.dev.servlet.adapter.in.web.dto;

import com.dev.servlet.shared.vo.KeyPair;

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
    default int statusCode() {
        return 200;
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
