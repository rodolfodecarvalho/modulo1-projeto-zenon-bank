package br.com.zenon.fraud.repositoy;

import br.com.zenon.fraud.model.Transaction;

import java.util.List;
import java.util.Optional;

public class TransactionListRepository implements TransactionRepository {

    private final List<Transaction> transactions;

    public TransactionListRepository(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public Optional<Transaction> getTransactionByOrigemName(String name) {
        return Optional.of(transactions.stream()
                                       .filter(t -> t.origin().name().equals(name))
                                       .findFirst()
                                       .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada para o cliente " + name)));
    }
}