package com.zad.wallet.service;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExchangeRatesLoader {
    private final RedisTemplate<String, String> redis;

    @EventListener(ApplicationReadyEvent.class)
    public void loadExchangeRatesToRedis() {
        var usdTryKey = "exchange_rate:usd_try";
        var tryUsdKey = "exchange_rate:try_usd";

        redis.opsForValue().set(usdTryKey, "40.47");
        redis.opsForValue().set(tryUsdKey, "0.025");
    }
}
