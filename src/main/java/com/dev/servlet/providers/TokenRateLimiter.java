package com.dev.servlet.providers;

import com.dev.servlet.interfaces.IRateLimiter;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is used to rate limit the number of requests from a client.
 *
 * @since 1.3.0
 */
@ApplicationScoped
public class TokenRateLimiter implements IRateLimiter {

    private final long window; // in milliseconds
    private final long threshold; // max requests
    private final Map<String, Queue<Long>> requestMap;

    public TokenRateLimiter(long threshold, long window) {
        this.window = window;
        this.threshold = threshold;
        this.requestMap = new ConcurrentHashMap<>();
    }

    public TokenRateLimiter() {
        this(5, 1000);
    }

    @Override
    public boolean tryAcquire(String token) {
        long currentTime = System.currentTimeMillis();
        requestMap.putIfAbsent(token, new ConcurrentLinkedQueue<>());

        Queue<Long> requestTimes = requestMap.get(token);
        if (requestTimes.isEmpty() || requestTimes.size() < threshold) {
            requestTimes.add(currentTime);
            return true;
        }

        long oldestRequestTime = requestTimes.peek();
        if (currentTime - oldestRequestTime > window) {
            while (!requestTimes.isEmpty() && currentTime - requestTimes.peek() > window) {
                requestTimes.poll();
            }
            requestTimes.add(currentTime);
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
    }
}