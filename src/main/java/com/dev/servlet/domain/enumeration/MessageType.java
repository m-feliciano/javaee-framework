package com.dev.servlet.domain.enumeration;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageType {
    CONFIRMATION(1, "email_confirmation"),
    WELCOME(2, "email_welcome"),
    CHANGE_EMAIL(3, "email_change");

    public final int code;
    public final String type;

    public static MessageType of(String type) {
        for (MessageType mt : values()) {
            if (mt.type.equalsIgnoreCase(type)) {
                return mt;
            }
        }
        throw new IllegalArgumentException("Unknown MessageType: " + type);
    }

    @Override
    public String toString() {
        return this.type;
    }
}
