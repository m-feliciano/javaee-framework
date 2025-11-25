package com.dev.servlet.core.util;

public interface RateLimiter {
    boolean tryConsume(String identifier, long tokens);

    long getAvailableTokens(String identifier);

    void reset(String identifier);

    long getSecondsUntilRefill(String identifier);
}
