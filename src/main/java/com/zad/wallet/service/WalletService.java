package com.zad.wallet.service;

import com.zad.wallet.exception.TxInProgressException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final StringRedisTemplate redis;
    private final KafkaTemplate<String, TxMessage> kafka;
    private final RateLimiterService rateLimiter;

    private static final Duration IDEMPOTENCY_TTL = Duration.ofMinutes(10);

    @Value("${kafka.topic.transactions}")
    private int trxTopic;


    public String Deposit(String trxKey) {
        var redisKey = "idempotency:" + trxKey;
        if (redis.hasKey(redisKey)) {
            var trxId = redis.opsForValue().get(redisKey);
            throw new TxInProgressException(trxId);
        }
        redis.opsForValue().set(redisKey, trxKey, IDEMPOTENCY_TTL.getSeconds());
        return "";
    }
}
