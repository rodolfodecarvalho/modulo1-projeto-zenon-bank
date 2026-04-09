package br.com.zenon.fraud.model;

import br.com.zenon.fraud.enums.TransactionType;

import java.math.BigDecimal;

public record Transaction(
        Integer step,
        TransactionType type,
        BigDecimal amount,
        TransactionCustomer origin,
        TransactionCustomer destination,
        Boolean isFraud,
        Boolean isFlaggedFraud
) {

    public Transaction {
        validateStep(step);
        validateNotNull(type, "type");
        validatePositive(amount);
        validateNotNull(origin, "origin");
        validateNotNull(destination, "destination");
        validateNotNull(isFraud, "isFraud");
        validateNotNull(isFlaggedFraud, "isFlaggedFraud");
    }

    private static void validateStep(Integer step) {
        if (step == null || step < 1) {
            throw new IllegalArgumentException("Step inválido: " + step);
        }
    }

    private static void validatePositive(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount inválido: " + value);
        }
    }

    private static void validateNotNull(Object obj, String field) {
        if (obj == null) {
            throw new IllegalArgumentException(field + " não pode ser nulo");
        }
    }
}