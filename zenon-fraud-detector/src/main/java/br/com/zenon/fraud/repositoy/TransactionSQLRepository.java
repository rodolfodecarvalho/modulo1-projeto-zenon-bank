package br.com.zenon.fraud.repositoy;

import br.com.zenon.fraud.db.ConnectionFactory;
import br.com.zenon.fraud.enums.TransactionType;
import br.com.zenon.fraud.exceptions.TransactionException;
import br.com.zenon.fraud.model.Transaction;
import br.com.zenon.fraud.model.TransactionCustomer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public class TransactionSQLRepository implements TransactionRepository {

    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        String sql = """
                SELECT id, step, `type`, amount,
                  name_origin, old_balance_origin, new_balance_origin,
                  name_recipient, old_balance_recipient, new_balance_recipient,
                  is_fraud, is_flagged_fraud
                FROM transactions
                WHERE name_origin = ?
                ORDER BY step
                LIMIT 1
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, originName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    IO.println(rs.getString("name_origin"));
                    Transaction transaction = mapResultSetToTransaction(rs);
                    return Optional.of(transaction);
                } else {
                    IO.println("Transacao nao encontrada para origin: " + originName);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new TransactionException("Erro ao buscar transação da origem: " + originName, e);
        }
    }

    @Override
    public void save(Transaction transaction) {
        String sql = """
                insert into transactions
                (step, `type`, amount, name_origin , old_balance_origin , new_balance_origin, name_recipient , old_balance_recipient , new_balance_recipient , is_fraud , is_flagged_fraud  )
                values (?,?,?,?,?,?,?,?,?,?,?);
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, transaction.step());
            ps.setString(2, transaction.type().name());
            ps.setBigDecimal(3, transaction.amount());

            ps.setString(4, transaction.origin().name());
            ps.setBigDecimal(5, transaction.origin().oldBalance());
            ps.setBigDecimal(6, transaction.origin().newBalance());

            ps.setString(7, transaction.destination().name());
            ps.setBigDecimal(8, transaction.destination().oldBalance());
            ps.setBigDecimal(9, transaction.destination().newBalance());

            ps.setBoolean(10, transaction.isFraud());
            ps.setBoolean(11, transaction.isFlaggedFraud());

            ps.execute();
        } catch (SQLException e) {
            throw new TransactionException("Erro ao salvar nova transação: " + transaction, e);
        }
    }

    @Override
    public void saveAll(List<Transaction> transactions) {
        String sql = """
                insert into transactions
                (step, `type`, amount, name_origin , old_balance_origin , new_balance_origin,
                 name_recipient , old_balance_recipient , new_balance_recipient , is_fraud , is_flagged_fraud)
                values (?,?,?,?,?,?,?,?,?,?,?);
                """;

        var semaphore = new Semaphore(10);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            List<Future<?>> futures = new ArrayList<>();

            int chunkSize = 1000;

            for (int i = 0; i < transactions.size(); i += chunkSize) {

                List<Transaction> chunk = transactions.subList(i, Math.min(i + chunkSize, transactions.size()));

                futures.add(executor.submit(() -> {
                    try {
                        semaphore.acquire();

                        saveChunk(chunk, sql);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new TransactionException("Thread interrompida", e);
                    } finally {
                        semaphore.release();
                    }
                }));
            }

            for (var future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    throw new TransactionException("Erro na thread", e.getCause());
                }
            }
        } catch (Exception e) {
            throw new TransactionException("Erro ao executar virtual threads", e);
        }
    }

    private void saveChunk(List<Transaction> transactions, String sql) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            int count = 0;

            for (Transaction transaction : transactions) {

                ps.setInt(1, transaction.step());
                ps.setString(2, transaction.type().name());
                ps.setBigDecimal(3, transaction.amount());

                ps.setString(4, transaction.origin().name());
                ps.setBigDecimal(5, transaction.origin().oldBalance());
                ps.setBigDecimal(6, transaction.origin().newBalance());

                ps.setString(7, transaction.destination().name());
                ps.setBigDecimal(8, transaction.destination().oldBalance());
                ps.setBigDecimal(9, transaction.destination().newBalance());

                ps.setBoolean(10, transaction.isFraud());
                ps.setBoolean(11, transaction.isFlaggedFraud());

                ps.addBatch();
                count++;

                if (count % 10000 == 0) {
                    ps.executeBatch();
                    conn.commit();
                }
            }

            ps.executeBatch();
            conn.commit();
        } catch (Exception e) {
            throw new TransactionException("Erro ao salvar chunk", e);
        }
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) {
        try {
            int step = rs.getInt("step");
            TransactionType type = TransactionType.valueOf(rs.getString("type"));
            BigDecimal amount = rs.getBigDecimal("amount");

            String originName = rs.getString("name_origin");
            BigDecimal originOldBalance = rs.getBigDecimal("old_balance_origin");
            BigDecimal originNewBalance = rs.getBigDecimal("new_balance_origin");
            TransactionCustomer origin = new TransactionCustomer(originName, originOldBalance, originNewBalance);

            String recipientName = rs.getString("name_recipient");
            BigDecimal recipientOldBalance = rs.getBigDecimal("old_balance_recipient");
            BigDecimal recipientNewBalance = rs.getBigDecimal("new_balance_recipient");
            TransactionCustomer recipient = new TransactionCustomer(recipientName, recipientOldBalance, recipientNewBalance);

            boolean isFraud = rs.getBoolean("is_fraud");
            boolean isFlaggedFraud = rs.getBoolean("is_flagged_fraud");

            return new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud);
        } catch (SQLException e) {
            throw new TransactionException(e);
        }
    }
}