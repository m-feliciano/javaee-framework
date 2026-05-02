package com.dev.servlet.domain.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Status {
    ACTIVE(1, "A"),
    DELETED(2, "X"),
    PENDING(3, "P");

    private final int code;
    private final String value;

    public static Status from(int cod) {
        return Arrays.stream(Status.values())
                .filter(id -> id != null && id.code == cod)
                .findFirst()
                .orElse(null);
    }

    public boolean equals(String value) {
        return this.value.equals(value);
    }
}
