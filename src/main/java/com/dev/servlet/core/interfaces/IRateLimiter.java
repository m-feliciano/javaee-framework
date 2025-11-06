package com.dev.servlet.core.interfaces;

public interface IRateLimiter {
    boolean acquire();
    boolean acquireOrWait(int milliseconds);
}
