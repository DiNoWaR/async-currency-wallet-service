package com.zad.wallet.repository;

import com.zad.wallet.dto.Balance;
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
    private record PendingTx(String id, String userId, String currency, String type, BigDecimal amount) {
    }

    private final JdbcTemplate jdbc;
    private final TransactionTemplate tx;

    public String createUserWithEmptyWallets(String name, String hashedPassword) {
        return tx.execute(status -> {
            var userId = UUID.randomUUID();
            jdbc.update("insert into users (id, name, hashed_password) values (?, ?, ?)", userId, name, hashedPassword);

            jdbc.update("insert into accounts (id, user_id, currency, balance) values (?, ?, ?, ?)", UUID.randomUUID(), userId, "try", BigDecimal.ZERO);

            jdbc.update("insert into accounts (id, user_id, currency, balance) values (?, ?, ?, ?)", UUID.randomUUID(), userId, "usd", BigDecimal.ZERO);

            return userId.toString();
        });
    }

    public List<Balance> getUserBalances(String userId) {
        return jdbc.query("select currency, balance from accounts where user_id = ?", (rs, rowNum) -> new Balance(
                        rs.getBigDecimal("balance"),
                        rs.getString("currency")),
                userId
        );
    }

    public void persistTransaction(String trxId, String userId, String type, String currency, BigDecimal amount, String status, Instant createdAt) {
        var query = """
                insert into transactions
                (id, user_id, type, currency, amount, status, created_at)
                values (?, ?, ?, ?, ?, ?, ?)
                """;

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
                            rs.getString("id"),
                            rs.getString("user_id"),
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
}
