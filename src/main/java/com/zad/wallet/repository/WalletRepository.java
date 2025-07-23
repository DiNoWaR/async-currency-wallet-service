package com.zad.wallet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletRepository {

    private JdbcTemplate jdbc;
    private final TransactionTemplate tx;

    public BigDecimal getUserBalance(String userId, String currency) {
        List<BigDecimal> list = jdbc.query(
                "SELECT balance FROM wallets WHERE user_id = ? AND currency = ?",
                (rs, rowNum) -> rs.getBigDecimal("balance"), userId, currency
        );
        return list.isEmpty() ? BigDecimal.ZERO : list.get(0);
    }

    public UUID createUserWithEmptyWallets(String name, String hashedPassword) {
        return tx.execute(status -> {
            var userId = UUID.randomUUID();
            jdbc.update("INSERT INTO users (id, name, hashed_password) VALUES (?, ?, ?)", userId, name, hashedPassword);

            jdbc.update("INSERT INTO accounts (id, user_id, currency, balance) VALUES (?, ?, ?, ?)", UUID.randomUUID(), userId, "try", BigDecimal.ZERO);

            jdbc.update("INSERT INTO accounts (id, user_id, currency, balance) VALUES (?, ?, ?, ?)", UUID.randomUUID(), userId, "usd", BigDecimal.ZERO);

            return userId;
        });
    }

    public void persistTransaction(UUID trxId,
                                   UUID userId,
                                   String type,
                                   String currency,
                                   BigDecimal amount,
                                   String status,
                                   Instant createdAt) {

        String sql = """
                INSERT INTO transactions
                (id, user_id, type, currency, amount, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbc.update(sql,
                trxId,
                userId,
                type,
                currency,
                amount,
                status,
                Timestamp.from(createdAt)
        );
    }


}
