package com.dev.servlet.providers;

import com.dev.servlet.interfaces.IRateLimiter;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TokenRateLimiter implements IRateLimiter {

    private final long window; // in milliseconds
    private final long threshold; // max requests
    private final Map<String, List<Long>> requestMap;

    public TokenRateLimiter(long threshold, long window) {
        this.window = window;
        this.threshold = threshold;
        this.requestMap = new java.util.concurrent.ConcurrentHashMap<>();
    }

    public TokenRateLimiter() {
        this(5, 1000);
    }

    @Override
    public boolean tryAcquire(String token) {
        long currentTime = System.currentTimeMillis();
        requestMap.putIfAbsent(token, new ArrayList<>());

        List<Long> requestTimes = requestMap.get(token);
        if (requestTimes.size() < threshold) {
            requestTimes.add(currentTime);
            return true;
        }

        long oldestRequestTime = requestTimes.get(0);
        if (currentTime - oldestRequestTime > window) {
            requestTimes.remove(0);
            requestTimes.add(currentTime);
            return true;
        }

        return false;
    }


    public static void main(String[] args) {
    }
}
