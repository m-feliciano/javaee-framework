package com.dev.servlet.core.util.impl;

import com.dev.servlet.core.util.IRateLimiter;
import com.dev.servlet.core.util.PropertiesUtil;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Slf4j
@Singleton
@NoArgsConstructor
public class Bucket4jRateLimiter implements IRateLimiter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private BucketConfiguration bucketConfiguration;

    private long capacity;
    private long refillTokens;
    private long refillPeriodSeconds;

    @PostConstruct
    public void init() {
        this.capacity = PropertiesUtil.getProperty("rate.limit.capacity", 20L);
        this.refillTokens = PropertiesUtil.getProperty("rate.limit.refill.tokens", 20L);
        this.refillPeriodSeconds = PropertiesUtil.getProperty("rate.limit.refill.period.seconds", 60L);

        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(refillTokens, Duration.ofSeconds(refillPeriodSeconds))
                .build();

        this.bucketConfiguration = BucketConfiguration.builder()
                .addLimit(limit)
                .build();

        log.info("[Bucket4jRateLimiter] initialized [capacity={}, refill={} tokens/{} seconds]",
                capacity, refillTokens, refillPeriodSeconds);
    }

    @Override
    public boolean tryConsume(String identifier, long tokens) {
        Bucket bucket = resolveBucket(identifier);
        boolean consumed = bucket.tryConsume(tokens);

        if (!consumed) {
            log.warn("Rate limit exceeded [identifier={}, requestedTokens={}, available={}]",
                    identifier, tokens, bucket.getAvailableTokens());
        }

        return consumed;
    }

    @Override
    public long getAvailableTokens(String identifier) {
        Bucket bucket = buckets.get(identifier);
        if (bucket == null) {
            return capacity;
        }
        return bucket.getAvailableTokens();
    }

    @Override
    public void reset(String identifier) {
        Bucket removed = buckets.remove(identifier);
        if (removed != null) {
            log.info("Rate limit bucket reset [identifier={}]", identifier);
        }
    }

    @Override
    public long getSecondsUntilRefill(String identifier) {
        Bucket bucket = buckets.get(identifier);
        if (bucket == null || bucket.getAvailableTokens() > 0) {
            return 0;
        }

        long nanosUntilRefill = bucket.tryConsumeAndReturnRemaining(1).getNanosToWaitForRefill();
        return Duration.ofNanos(nanosUntilRefill).getSeconds();
    }

    private Bucket resolveBucket(String identifier) {
        return buckets.computeIfAbsent(identifier, k -> {
            log.debug("Creating new rate limit bucket [identifier={}]", identifier);
            return Bucket.builder()
                    .addLimit(bucketConfiguration.getBandwidths()[0])
                    .build();
        });
    }
}

