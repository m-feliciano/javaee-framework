package com.dev.servlet.domain.enums;

import java.util.Arrays;

public enum StatusEnum {
    ACTIVE(1, "A"),
    DELETED(2, "X");

    private final int cod;
    private final String description;

    StatusEnum(int cod, String description) {
        this.cod = cod;
        this.description = description;
    }

    public static StatusEnum getByCode(int cod) {
        return Arrays.stream(StatusEnum.values())
                .filter(id -> id != null && id.cod == cod)
                .findFirst()
                .orElse(null);
    }

    public int getCod() {
        return cod;
    }

    public String getDescription() {
        return description;
    }
}
