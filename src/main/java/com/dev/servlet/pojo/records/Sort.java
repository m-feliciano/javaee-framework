package com.dev.servlet.pojo.records;

public enum Sort {
    ID("id"),
    NAME("name"),
    DESCRIPTION("description"),
    STATUS("status"),
    ;

    private final String value;

    Sort(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Sort from(String value) {
        for (Sort sort : Sort.values()) {
            if (sort.value.equalsIgnoreCase(value)) {
                return sort;
            }
        }
        return null;
    }
}
