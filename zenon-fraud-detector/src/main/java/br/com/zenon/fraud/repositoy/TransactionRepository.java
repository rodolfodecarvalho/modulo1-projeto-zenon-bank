package br.com.zenon.fraud.repositoy;

import br.com.zenon.fraud.model.Transaction;

import java.util.Optional;

public interface TransactionRepository {
    Optional<Transaction> getTransactionByOrigemName(String name);
}