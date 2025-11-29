package com.dev.servlet.domain.valueobject;

import lombok.Getter;

public record KeyPair(@Getter String key, @Getter Object value) {
}
