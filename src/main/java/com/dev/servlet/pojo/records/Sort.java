package com.dev.servlet.pojo.records;

import lombok.Getter;

import java.io.Serializable;

public record Sort(String field, Direction direction) implements Serializable {

    public static Sort of(String sortField, Direction direction) {
        return new Sort(sortField, direction);
    }

    public static Sort of(String sortField) {
        return new Sort(sortField, Direction.UNSET);
    }

    @Getter
    public enum Direction {
        ASC("asc"),
        DESC("desc"),
        UNSET(null);

        private final String value;

        Direction(String value) {
            this.value = value;
        }

        public static Direction from(String value) {
            for (Direction direction : Direction.values()) {
                if (direction.value.equalsIgnoreCase(value)) {
                    return direction;
                }
            }
            return null;
        }
    }
}
