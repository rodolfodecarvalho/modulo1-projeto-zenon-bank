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
}