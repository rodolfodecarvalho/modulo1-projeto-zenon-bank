package br.com.zenon.fraud.service;

import br.com.zenon.fraud.enums.TransactionType;
import br.com.zenon.fraud.model.Transaction;
import br.com.zenon.fraud.model.TransactionCustomer;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class TransactionIngestor {
    private TransactionIngestor() {
    }

    public static List<Transaction> readTransactions(final String path, final int limit) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(path))) {

            return lines
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .limit(limit)
                    .map(TransactionIngestor::mapLineToTransaction)
                    .toList();
        }
    }

    private static Transaction mapLineToTransaction(final String line) {
        String[] fields = line.split(",");

        return new Transaction(
                Integer.parseInt(fields[0]),
                TransactionType.valueOf(fields[1]),
                new BigDecimal(fields[2]),
                new TransactionCustomer(
                        fields[3],
                        new BigDecimal(fields[4]),
                        new BigDecimal(fields[5])
                ),
                new TransactionCustomer(
                        fields[6],
                        new BigDecimal(fields[7]),
                        new BigDecimal(fields[8])
                ),
                fields[9].equals("1"),
                fields[10].equals("1")
        );
    }
}