package com.zad.wallet.repository;

import com.zad.wallet.dto.Balance;
import com.zad.wallet.dto.TrxResponse;
import com.zad.wallet.dto.TxOperation;
import com.zad.wallet.dto.TxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private record PendingTx(UUID id, UUID userId, String currency, String type, BigDecimal amount) {
    }

    private final JdbcTemplate jdbc;
    private final TransactionTemplate tx;

    public String createUserWithEmptyWallets(String name) {
        return tx.execute(status -> {
            var count = jdbc.queryForObject("select count(*) from users where name = ?", Integer.class, name);
            if (count != null && count > 0) {
                return jdbc.queryForObject("select id from users where name = ?", String.class, name);
            }
            var userId = UUID.randomUUID();
            jdbc.update("insert into users (id, name) values (?, ?)", userId, name);
            jdbc.update("insert into accounts (id, user_id, currency, balance) values (?, ?, ?, ?)", UUID.randomUUID(), userId, "try", BigDecimal.ZERO);
            jdbc.update("insert into accounts (id, user_id, currency, balance) values (?, ?, ?, ?)", UUID.randomUUID(), userId, "usd", BigDecimal.ZERO);
            return userId.toString();
        });
    }

    public List<Balance> getUserBalances(String userIdStr) {
        var userId = UUID.fromString(userIdStr);
        return jdbc.query(
                "SELECT currency, balance FROM accounts WHERE user_id = ?",
                new Object[]{userId},
                (rs, rowNum) -> new Balance(
                        rs.getBigDecimal("balance"),
                        rs.getString("currency")
                )
        );
    }

    public void persistTransaction(String trxIdStr, String userIdStr, String type, String currency, BigDecimal amount, String status, Instant createdAt) {
        var query = """
                insert into transactions
                (id, user_id, type, currency, amount, status, created_at)
                values (?, ?, ?, ?, ?, ?, ?)
                """;
        var userId = UUID.fromString(userIdStr);
        var trxId = UUID.fromString(trxIdStr);
        jdbc.update(query,
                trxId,
                userId,
                type.toLowerCase(),
                currency.toLowerCase(),
                amount,
                status.toLowerCase(),
                Timestamp.from(createdAt)
        );
    }

    public void processPendingTransactions() {
        tx.executeWithoutResult(status -> {
            List<PendingTx> pending = jdbc.query(
                    "select id, user_id, currency, type, amount from transactions where status = 'pending' for update skip locked",
                    (rs, rn) -> new PendingTx(
                            rs.getObject("id", java.util.UUID.class),
                            rs.getObject("user_id", java.util.UUID.class),
                            rs.getString("currency"),
                            rs.getString("type"),
                            rs.getBigDecimal("amount")
                    )
            );

            for (var txn : pending) {
                var amount = txn.amount;
                var sign = "withdraw".equalsIgnoreCase(txn.type) ? "-" : "+";
                var updated = jdbc.update("update accounts set balance = balance " + sign + " ? " + "where user_id = ? and currency = ?", amount, txn.userId, txn.currency);

                if (updated == 0) {
                    var initial = "+".equals(sign) ? amount : BigDecimal.ZERO.subtract(amount);
                    jdbc.update("insert into accounts (id, user_id, currency, balance) values (?, ?, ?, ?)", UUID.randomUUID(), txn.userId, txn.currency, initial);
                }
                jdbc.update("update transactions set status = 'success' where id = ?", txn.id);
            }
        });
    }

    public TrxResponse getLastTransaction(String userIdStr) {
        var query = """
                    SELECT id, type, amount, status, currency, created_at
                    FROM transactions
                    WHERE user_id = ?
                    ORDER BY created_at DESC LIMIT 1
                """;
        try {
            var userId = UUID.fromString(userIdStr);
            return jdbc.queryForObject(
                    query,
                    (rs, rn) -> new TrxResponse(
                            rs.getObject("id", UUID.class).toString(),
                            TxOperation.valueOf(rs.getString("type").toUpperCase()),
                            rs.getBigDecimal("amount"),
                            TxStatus.valueOf(rs.getString("status").toUpperCase()),
                            rs.getString("currency").toUpperCase(),
                            rs.getTimestamp("created_at").toInstant()
                    ),
                    userId
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
}
