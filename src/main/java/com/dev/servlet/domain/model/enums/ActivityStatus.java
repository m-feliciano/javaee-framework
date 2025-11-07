package com.dev.servlet.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityStatus {
    SUCCESS(1, "Success"),
    FAILED(2, "Failed"),
    PENDING(3, "Pending");

    private final Integer code;
    private final String description;
}

