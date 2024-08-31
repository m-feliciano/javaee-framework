package com.dev.servlet.pojo.enums;

import java.util.Arrays;

public enum StatusEnum {
    ACTIVE(1, "A"),
    DELETED(2, "X");

    public final int cod;
    public final String value;

    StatusEnum(int cod, String value) {
        this.cod = cod;
        this.value = value;
    }

    public static StatusEnum from(int cod) {
        return Arrays.stream(StatusEnum.values())
                .filter(id -> id != null && id.cod == cod)
                .findFirst()
                .orElse(null);
    }
}
