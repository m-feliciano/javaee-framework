package com.dev.servlet.pojo.records;

public enum Order {
    ASC("asc"),
    DESC("desc");

    private final String value;

    Order(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Order from(String value) {
        for (Order order : Order.values()) {
            if (order.value.equalsIgnoreCase(value)) {
                return order;
            }
        }
        return null;
    }
}
