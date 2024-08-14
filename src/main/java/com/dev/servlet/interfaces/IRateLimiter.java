package com.dev.servlet.interfaces;

@FunctionalInterface
public interface IRateLimiter {

    boolean tryAcquire(String token);
}
