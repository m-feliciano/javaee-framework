package com.dev.servlet.core.util;

public interface IRateLimiter {
    boolean tryConsume(String identifier, long tokens);

    default boolean tryConsume(String identifier) {
        return tryConsume(identifier, 1);
    }

    long getAvailableTokens(String identifier);

    void reset(String identifier);

    long getSecondsUntilRefill(String identifier);
}
