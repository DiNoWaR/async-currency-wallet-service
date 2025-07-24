package com.zad.wallet.service;

import com.zad.wallet.constants.Constants;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class CurrencyCacheLoader {
    private final RedisTemplate<String, String> redis;
    private final Set<String> currencies = Set.of("usd", "try");

    @EventListener(ApplicationReadyEvent.class)
    public void loadCurrenciesToRedis() {
        redis.opsForSet().add(Constants.CURRENCIES_REDIS_KEY, currencies.toArray(String[]::new));
    }
}
