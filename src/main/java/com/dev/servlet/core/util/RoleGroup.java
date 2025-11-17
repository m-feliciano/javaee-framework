package com.dev.servlet.core.util;

import com.dev.servlet.domain.model.enums.RoleType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoleGroup {

    public static EnumSet<RoleType> get(RoleType role) {
        return switch (role) {
            case ADMIN -> EnumSet.allOf(RoleType.class);
            case MODERATOR -> EnumSet.of(RoleType.MODERATOR, RoleType.DEFAULT, RoleType.VISITOR);
            case DEFAULT -> EnumSet.of(RoleType.DEFAULT, RoleType.VISITOR);
            case VISITOR -> EnumSet.of(RoleType.VISITOR);
        };
    }
}
