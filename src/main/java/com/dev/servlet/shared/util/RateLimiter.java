package com.dev.servlet.shared.util;

public interface RateLimiter {
    boolean tryConsume(String identifier, long tokens);

    long getAvailableTokens(String identifier);

    void reset(String identifier);

    long getSecondsUntilRefill(String identifier);
}
