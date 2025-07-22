package com.zad.wallet.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Service
public class RateLimiterService {

    @Value("${limiter.period.duration.minute}")
    private int period;

    @Value("${limiter.period.number}")
    private int amount;

    private final LoadingCache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(period))
            .build(key -> Bucket.builder()
                    .addLimit(Bandwidth.simple(amount, Duration.ofMinutes(period)))
                    .build());

    public void check(String ipAddr) {
        var bucket = cache.get(ipAddr);
        if (!bucket.tryConsume(1)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
    }
}
