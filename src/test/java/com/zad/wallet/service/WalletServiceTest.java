package com.zad.wallet.service;

import com.zad.wallet.constants.Constants;
import com.zad.wallet.dto.Balance;
import com.zad.wallet.dto.TxOperation;
import com.zad.wallet.exception.InsufficientFundsException;
import com.zad.wallet.exception.InvalidCurrencyException;
import com.zad.wallet.exception.TxInProgressException;
import com.zad.wallet.model.KafkaTxMessage;
import com.zad.wallet.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private KafkaTemplate<String, KafkaTxMessage> kafka;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(walletService, "trxTopic", "test-topic");
    }

    @Test
    void shouldLogUserSuccessfully() {
        var username = "user";
        var userId = "denis";
        var token = "jwt.token";

        when(walletRepository.createUserWithEmptyWallets(username)).thenReturn(userId);
        when(jwtService.generateToken(userId)).thenReturn(token);

        var result = walletService.logUser(username);

        assertEquals(userId, result.userId());
        assertEquals(username, result.username());
        assertEquals(token, result.accessToken());
    }

    @Test
    void shouldThrowEntityNotFoundWhenExchangeRateMissing() {
        var valueOps = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("exchange_rate:usd_eur")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () ->
                walletService.getExchangeRates("usd", "eur")
        );
    }

    @Test
    void shouldReturnExchangeRateIfPresentInRedis() {
        var from = "usd";
        var to = "try";
        var rate = "34.56";

        var valueOps = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("exchange_rate:usd_try")).thenReturn(rate);

        var result = walletService.getExchangeRates(from, to);
        assertEquals(new BigDecimal(rate), result);
    }

    @Test
    void shouldThrowTxInProgressWhenKeyExists() {
        var trxKey = "key123";
        var userId = "user1";
        var amount = BigDecimal.TEN;
        var currency = "usd";
        var now = Instant.now();

        when(redis.hasKey("idempotency:" + trxKey)).thenReturn(true);

        assertThrows(TxInProgressException.class, () ->
                walletService.makeTransaction(trxKey, userId, amount, currency, TxOperation.DEPOSIT, now)
        );
    }

    @Test
    void shouldThrowInvalidCurrencyExceptionIfCurrencyMissing() {
        var trxKey = "";
        var userId = "user1";
        var amount = BigDecimal.TEN;
        var currency = "invalid";
        var now = Instant.now();

        var setOps = mock(SetOperations.class);
        when(redis.opsForSet()).thenReturn(setOps);
        when(setOps.members(Constants.CURRENCIES_REDIS_KEY)).thenReturn(Set.of("usd", "try"));

        assertThrows(InvalidCurrencyException.class, () ->
                walletService.makeTransaction(trxKey, userId, amount, currency, TxOperation.DEPOSIT, now)
        );
    }

    @Test
    void shouldThrowInsufficientFundsExceptionIfBalanceTooLow() {
        var trxKey = "";
        var userId = "user1";
        var amount = new BigDecimal("100");
        var currency = "usd";
        var now = Instant.now();

        var setOps = mock(SetOperations.class);
        when(redis.opsForSet()).thenReturn(setOps);
        when(setOps.members(Constants.CURRENCIES_REDIS_KEY)).thenReturn(Set.of(currency));

        var balance = new Balance(new BigDecimal("50"),currency);
        when(walletRepository.getUserBalances(userId)).thenReturn(List.of(balance));

        assertThrows(InsufficientFundsException.class, () ->
                walletService.makeTransaction(trxKey, userId, amount, currency, TxOperation.WITHDRAW, now)
        );
    }
}
