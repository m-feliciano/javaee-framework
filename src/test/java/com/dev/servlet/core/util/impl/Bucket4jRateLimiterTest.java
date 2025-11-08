package com.dev.servlet.core.util.impl;

import com.dev.servlet.core.util.IRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Bucket4jRateLimiterTest {

    private IRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new Bucket4jRateLimiter();
        ((Bucket4jRateLimiter) rateLimiter).init();
    }

    @Test
    void shouldAllowRequestsUnderCapacity() {
        String identifier = "user@test.com";

        for (int i = 0; i < 10; i++) {
            assertTrue(rateLimiter.tryConsume(identifier),
                "Request " + (i + 1) + " should be allowed");
        }
    }

    @Test
    void shouldBlockRequestsOverCapacity() {
        String identifier = "user@test.com";

        for (int i = 0; i < 10; i++) {
            rateLimiter.tryConsume(identifier);
        }

        assertFalse(rateLimiter.tryConsume(identifier),
            "Request should be blocked after consuming all tokens");
    }

    @Test
    void shouldTrackAvailableTokens() {
        String identifier = "user@test.com";

        assertEquals(10, rateLimiter.getAvailableTokens(identifier));

        rateLimiter.tryConsume(identifier);
        assertEquals(9, rateLimiter.getAvailableTokens(identifier));

        rateLimiter.tryConsume(identifier);
        assertEquals(8, rateLimiter.getAvailableTokens(identifier));
    }

    @Test
    void shouldResetBucket() {
        String identifier = "user@test.com";

        rateLimiter.tryConsume(identifier);
        rateLimiter.tryConsume(identifier);
        assertEquals(8, rateLimiter.getAvailableTokens(identifier));

        rateLimiter.reset(identifier);
        assertEquals(10, rateLimiter.getAvailableTokens(identifier));
    }

    @Test
    void shouldIsolateDifferentIdentifiers() {
        String user1 = "user1@test.com";
        String user2 = "user2@test.com";

        rateLimiter.tryConsume(user1);
        rateLimiter.tryConsume(user1);

        assertEquals(8, rateLimiter.getAvailableTokens(user1));
        assertEquals(10, rateLimiter.getAvailableTokens(user2));
    }

    @Test
    void shouldConsumeMultipleTokens() {
        String identifier = "user@test.com";

        assertEquals(10, rateLimiter.getAvailableTokens(identifier));

        assertTrue(rateLimiter.tryConsume(identifier, 3));
        assertEquals(7, rateLimiter.getAvailableTokens(identifier));
    }

    @Test
    void shouldNotConsumeMoreTokensThanAvailable() {
        String identifier = "user@test.com";

        rateLimiter.tryConsume(identifier, 8);
        assertEquals(2, rateLimiter.getAvailableTokens(identifier));

        assertFalse(rateLimiter.tryConsume(identifier, 5));
        assertEquals(2, rateLimiter.getAvailableTokens(identifier),
            "Tokens should not be consumed if request exceeds available");
    }

    @Test
    void shouldReturnZeroSecondsUntilRefillWhenTokensAvailable() {
        String identifier = "user@test.com";

        assertEquals(0, rateLimiter.getSecondsUntilRefill(identifier));

        rateLimiter.tryConsume(identifier, 5);
        assertEquals(0, rateLimiter.getSecondsUntilRefill(identifier),
            "Should return 0 when tokens are still available");
    }

    @Test
    void shouldReturnPositiveSecondsUntilRefillWhenNoTokens() {
        String identifier = "user@test.com";

        rateLimiter.tryConsume(identifier, 10);
        rateLimiter.tryConsume(identifier);

        long secondsUntilRefill = rateLimiter.getSecondsUntilRefill(identifier);
        assertTrue(secondsUntilRefill > 0,
            "Should return positive seconds when no tokens available");
    }

    @Test
    void shouldAllowNewIdentifierWithFullCapacity() {
        String newIdentifier = "new-user@test.com";

        assertEquals(10, rateLimiter.getAvailableTokens(newIdentifier));
        assertTrue(rateLimiter.tryConsume(newIdentifier));
    }
}

