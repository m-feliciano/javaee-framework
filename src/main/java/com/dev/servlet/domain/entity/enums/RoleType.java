package com.dev.servlet.domain.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {
    ADMIN(1, "ADMIN"),
    DEFAULT(2, "USER"),
    MODERATOR(3, "MODERATOR"),
    VISITOR(4, "GUEST");
    private final Integer code;
    private final String description;

    public static RoleType toEnum(Integer code) {
        if (code == null) return null;
        for (RoleType p : RoleType.values()) {
            if (code.equals(p.code))
                return p;
        }
        throw new IllegalArgumentException("Invalid Id: " + code);
    }
}
