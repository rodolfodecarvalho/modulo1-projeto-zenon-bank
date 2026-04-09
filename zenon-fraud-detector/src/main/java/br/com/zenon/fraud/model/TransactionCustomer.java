package br.com.zenon.fraud.model;

import java.math.BigDecimal;

public record TransactionCustomer(
        String name,
        BigDecimal oldBalance,
        BigDecimal newBalance
) {

    public TransactionCustomer {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome inválido");
        }

        validatePositive(oldBalance, "oldBalance");
        validatePositive(newBalance, "newBalance");
    }

    private static void validatePositive(BigDecimal value, String field) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(field + " inválido: " + value);
        }
    }
}