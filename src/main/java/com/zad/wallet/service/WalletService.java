package com.zad.wallet.service;

import com.zad.wallet.dto.TxOperation;
import com.zad.wallet.dto.TxStatus;
import com.zad.wallet.exception.InsufficientFundsException;
import com.zad.wallet.exception.InvalidCurrencyException;
import com.zad.wallet.exception.TxInProgressException;
import com.zad.wallet.model.KafkaTxMessage;
import com.zad.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final StringRedisTemplate redis;
    private final KafkaTemplate<String, KafkaTxMessage> kafka;
    private final WalletRepository walletRepository;

    private static final Duration IDEMPOTENCY_TTL = Duration.ofMinutes(1);

    @Value("${kafka.topic.transactions}")
    private String trxTopic;


    public String makeTransaction(String trxKey, String userId, BigDecimal amount, String currency, TxOperation operation, Instant ts) {
        var redisKey = "idempotency:" + trxKey;
        if (redis.hasKey(redisKey)) {
            var trxId = redis.opsForValue().get(redisKey);
            throw new TxInProgressException(trxId, operation, amount, TxStatus.PENDING, ts);
        }
        redis.opsForValue().set(redisKey, trxKey, IDEMPOTENCY_TTL.getSeconds());
        if (operation.equals(TxOperation.WITHDRAW)) {
            var currentBalance = getBalance(userId, currency);
            if (currentBalance.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientFundsException(trxKey, userId);
            }
        }

        var kafkaMessage = KafkaTxMessage.builder()
                .userId(userId)
                .amount(amount)
                .ts(ts)
                .currency(currency)
                .status(TxStatus.PENDING)
                .operation(operation)
                .build();

        kafka.send(trxTopic, userId, kafkaMessage).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send kafka message trxId={}, userId={}, amount={}, operation={}",
                        kafkaMessage.getTrxId(),
                        kafkaMessage.getUserId(),
                        kafkaMessage.getOperation(),
                        ex
                );
                throw new RuntimeException("Internal Error");
            }
        });
        return kafkaMessage.getTrxId();
    }

    public BigDecimal getBalance(String userId, String currency) {
        var curr = currency.toLowerCase();
        if (!curr.equals("usd") && !curr.equals("try")) {
            throw new InvalidCurrencyException(curr);
        }
        return walletRepository.getUserBalance(userId, curr);
    }
}
