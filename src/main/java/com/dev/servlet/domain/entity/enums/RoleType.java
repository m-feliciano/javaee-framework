package com.dev.servlet.domain.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

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

    public static EnumSet<RoleType> getRoles(RoleType role) {
        return switch (role) {
            case ADMIN -> EnumSet.allOf(RoleType.class);
            case MODERATOR -> EnumSet.of(RoleType.MODERATOR, RoleType.DEFAULT, RoleType.VISITOR);
            case DEFAULT -> EnumSet.of(RoleType.DEFAULT, RoleType.VISITOR);
            case VISITOR -> EnumSet.of(RoleType.VISITOR);
        };
    }
}
