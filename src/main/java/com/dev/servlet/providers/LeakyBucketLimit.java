package com.dev.servlet.providers;

import com.dev.servlet.interfaces.IRateLimiter;

import javax.inject.Singleton;

/**
 * This class is used to rate limit the number of requests from a client.
 *
 * @since 1.3.0
 */
@Singleton
public class LeakyBucketLimit implements IRateLimiter {

    private static final int MAX_REQUESTS = 10; // 10 requests per second
    private static final long REFILL_TIME = 1000; // 1 second
    private static final int REFILL_AMOUNT = 5; // refill 5 tokens per second

    private int availableTokens;
    private long lastRefillTime;

    public LeakyBucketLimit() {
        this.availableTokens = MAX_REQUESTS;
        this.lastRefillTime = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean acquire() {
        refill();
        if (availableTokens > 0) {
            availableTokens--;
            return true;
        }
        return false;
    }

    private void refill() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastRefillTime;
        if (elapsedTime >= REFILL_TIME) {
            int tokensToAdd = (int) (elapsedTime / REFILL_TIME) * REFILL_AMOUNT;
            availableTokens = Math.min(availableTokens + tokensToAdd, MAX_REQUESTS);
            lastRefillTime = currentTime;
        }
    }

    public static void main(String[] args) {
    }
}