package br.com.zenon.fraud.service;

import br.com.zenon.fraud.enums.TransactionType;
import br.com.zenon.fraud.exceptions.TransactionException;
import br.com.zenon.fraud.model.Transaction;
import br.com.zenon.fraud.model.TransactionCustomer;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class TransactionIngestor {

    private static final Logger LOGGER = Logger.getLogger(TransactionIngestor.class.getName());

    private TransactionIngestor() {
    }

    public static List<Transaction> readTransactions(final String path, final int limit) {
        try (Stream<String> lines = Files.lines(Paths.get(path))) {

            return lines
                    .skip(1)
                    .limit(limit)
                    .filter(line -> !line.isBlank())
                    .map(TransactionIngestor::mapLineToTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (Exception ex) {
            throw new TransactionException(String.format("Erro ao ler o arquivo: %s", path), ex);
        }
    }

    private static Optional<Transaction> mapLineToTransaction(final String line) {
        String[] fields = line.split(",");

        try {
            return Optional.of(
                    new Transaction(
                            Integer.parseInt(fields[0]),
                            TransactionType.valueOf(fields[1].toUpperCase()),
                            parseBigDecimal(fields[2], "amount", line),
                            new TransactionCustomer(
                                    fields[3],
                                    parseBigDecimal(fields[4], "oldbalanceOrg", line),
                                    parseBigDecimal(fields[5], "newbalanceOrig", line)
                            ),
                            new TransactionCustomer(
                                    fields[6],
                                    parseBigDecimal(fields[7], "oldbalanceDest", line),
                                    parseBigDecimal(fields[8], "newbalanceDest", line)
                            ),
                            fields[9].equals("1"),
                            fields[10].equals("1")
                    )
            );

        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.WARNING, String.format("Erro de validacao ao processar linha: %s %s", line, ex));
            return Optional.empty();

        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, String.format("Erro inesperado ao processar linha: %s %s", line, ex));
            return Optional.empty();
        }
    }

    private static BigDecimal parseBigDecimal(String value, String fieldName, String line) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Campo numérico vazio: " + fieldName + " | linha: " + line);
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor inválido para " + fieldName + ": " + value + " | linha: " + line, ex);
        }
    }
}