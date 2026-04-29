package br.com.zenon.fraud.repositoy;

import br.com.zenon.fraud.model.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransactionMapRepository implements TransactionRepository {

    private final Map<String, Transaction> transactionByOrigiName;

    public TransactionMapRepository(List<Transaction> transactions) {
        this.transactionByOrigiName = transactions.stream()
                                                  .collect(Collectors.toMap(
                                                          transaction -> transaction.origin().name(),
                                                          Function.identity()
                                                  ));
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        return Optional.ofNullable(transactionByOrigiName.get(originName));
    }

    @Override
    public void save(Transaction transaction) {
        this.transactionByOrigiName.putIfAbsent(transaction.origin().name(), transaction);
    }

    @Override
    public void saveAll(List<Transaction> transactions) {
        transactions.forEach(transaction ->
                this.transactionByOrigiName.put(
                        transaction.origin().name(),
                        transaction
                )
        );
    }
}