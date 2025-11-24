package com.dev.servlet.domain.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageType {
    CONFIRMATION(1, "email_confirmation"),
    WELCOME(2, "email_welcome"),
    CHANGE_EMAIL(3, "email_change");

    public final int code;
    public final String type;

    // Creates a MessageType from a string, case-insensitive
    @JsonCreator
    public static MessageType of(String type) {
        if (type == null) throw new IllegalArgumentException("MessageType cannot be null");

        for (MessageType mt : values()) {
            if (mt.type.equalsIgnoreCase(type)) return mt;
        }

        throw new IllegalArgumentException("Unknown MessageType: " + type);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.type;
    }
}
