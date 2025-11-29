package com.dev.servlet.domain.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityStatus {
    SUCCESS(1, "Success"),
    FAILED(2, "Failed"),
    PENDING(3, "Pending"),
    WARNING(4, "Warning"),
    INFO(5, "Info");

    private final Integer code;
    private final String description;
}
