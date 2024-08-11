package com.dev.servlet.domain.enums;

import java.util.Arrays;

public enum StatusEnum {
    ACTIVE(1, "A"),
    DELETED(2, "X");

    private final int cod;
    private final String name;

    StatusEnum(int cod, String name) {
        this.cod = cod;
        this.name = name;
    }

    public static StatusEnum getByName(int cod) {
        return Arrays.stream(StatusEnum.values())
                .filter(id -> id != null && id.cod == cod)
                .findFirst()
                .orElse(null);
    }

    public int getCod() {
        return cod;
    }

    public String getName() {
        return name;
    }
}
