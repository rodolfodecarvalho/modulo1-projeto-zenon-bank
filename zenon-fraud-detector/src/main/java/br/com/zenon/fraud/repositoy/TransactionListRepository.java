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
    public Optional<Transaction> findByOriginName(String originName) {
        return Optional.of(transactions.stream()
                                       .filter(t -> t.origin().name().equals(originName))
                                       .findFirst()
                                       .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada para o cliente " + originName)));
    }

    @Override
    public void save(Transaction transaction) {
        this.transactions.add(transaction);
    }

    @Override
    public void saveAll(List<Transaction> transactions) {
        this.transactions.addAll(transactions);
    }
}