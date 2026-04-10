package br.com.zenon.fraud.service;

import br.com.zenon.fraud.enums.TransactionType;
import br.com.zenon.fraud.model.Transaction;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FraudAnalyzer {
    private FraudAnalyzer() {

    }

    public static long isFraudTrue(List<Transaction> transactions) {
        return transactions.stream()
                           .filter(Transaction::isFraud)
                           .count();
    }

    public static Set<Transaction> topNByTransaction(final List<Transaction> transactions, final int limit) {
        return transactions.stream()
                           .sorted(Comparator.comparing(Transaction::amount).reversed())
                           .limit(limit)
                           .collect(Collectors.toSet());
    }

    public static Set<String> topNSuspectByNameOrig(List<Transaction> topNByTransaction, final int limit) {
        return topNByTransaction.stream()
                                .filter(Transaction::isFraud)
                                .sorted(Comparator.comparing(Transaction::amount).reversed())
                                .map(t -> t.origin().name())
                                .distinct()
                                .limit(limit)
                                .collect(Collectors.toSet());
    }

    public static List<BigDecimal> getAmounts(Set<Transaction> topNByTransaction) {
        return topNByTransaction.stream()
                                .map(Transaction::amount)
                                .toList();
    }

    public static BigDecimal totalAmount(final List<Transaction> transactions) {
        return transactions.stream()
                           .filter(Transaction::isFraud)
                           .map(Transaction::amount)
                           .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Map<TransactionType, Long> totalByTransactionType(final List<Transaction> transactions) {
        return transactions.stream()
                           .filter(Transaction::isFraud)
                           .collect(Collectors.groupingBy(Transaction::type, Collectors.counting()));
    }
}