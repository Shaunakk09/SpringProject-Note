package com.example.LogGuard.RateLimiting;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestControllerAdvice
public class RateLimitingControllerAdvice {
    private final Map<String, TokenBucket> tokenBuckets = new ConcurrentHashMap<>();

    private static final int DEFAULT_CAPACITY = 10;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<String> handleRateLimitExceeded() {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded. Please try again later.");
    }

    public void checkRateLimit(String key) {
        tokenBuckets.computeIfAbsent(key, k -> new TokenBucket(DEFAULT_CAPACITY, REFILL_PERIOD));

        TokenBucket tokenBucket = tokenBuckets.get(key);
        if (!tokenBucket.tryConsume()) {
            throw new RateLimitExceededException();
        }
    }

    static class RateLimitExceededException extends RuntimeException {
    }

    static class TokenBucket {
        private final int capacity;
        private final Duration refillPeriod;
        private int tokens;
        private long lastRefillTimestamp;

        TokenBucket(int capacity, Duration refillPeriod) {
            this.capacity = capacity;
            this.refillPeriod = refillPeriod;
            this.tokens = capacity;
            this.lastRefillTimestamp = System.currentTimeMillis();
        }

        synchronized boolean tryConsume() {
            refill();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long elapsedTime = now - lastRefillTimestamp;
            int newTokens = (int) (elapsedTime / refillPeriod.toMillis());
            tokens = Math.min(capacity, tokens + newTokens);
            lastRefillTimestamp = now;
        }
    }
}

