package com.example.LogGuard.RateLimiting;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RateLimitingControllerAdviceTest {

    @InjectMocks
    private RateLimitingControllerAdvice rateLimitingControllerAdvice;

    @Mock
    private Map<String, RateLimitingControllerAdvice.TokenBucket> tokenBuckets;

    private static final int DEFAULT_CAPACITY = 10;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);

    @Before
    public void setUp() {
        tokenBuckets = new ConcurrentHashMap<>();
        rateLimitingControllerAdvice = new RateLimitingControllerAdvice();
    }

    @Test
    public void testHandleRateLimitExceeded() {
        ResponseEntity<String> responseEntity = rateLimitingControllerAdvice.handleRateLimitExceeded();
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responseEntity.getStatusCode());
        assertEquals("Rate limit exceeded. Please try again later.", responseEntity.getBody());
    }

    @Test
    public void testCheckRateLimitTokenBucketAbsent() {
        String testKey = "testKey";
        rateLimitingControllerAdvice.checkRateLimit(testKey);
    }
}

