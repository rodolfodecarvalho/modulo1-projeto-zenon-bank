package br.com.zenon.fraud.service;

import br.com.zenon.fraud.exceptions.TransactionException;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.nio.file.Files.lines;

public class TransactionReport {

    private static final Logger LOGGER = Logger.getLogger(TransactionReport.class.getName());

    private record ReportTransaction(BigDecimal amount, Boolean isFraud) {
    }

   public record Statistics(Long totalRegister, Long totalIsFraud, BigDecimal totalAmount) {
        private static final Statistics STATISTICS_ZERO = new Statistics(0L, 0L, BigDecimal.ZERO);

        public static Statistics readTransactions(final String path) {
            try (Stream<String> lines = lines(Paths.get(path))) {

                return lines
                        .skip(1)
                        .filter(line -> !line.isBlank())
                        .map(TransactionReport::mapLineToReportTransaction)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .reduce(
                                STATISTICS_ZERO,
                                Statistics::getStatistics,
                                Statistics::add
                        );
            } catch (Exception ex) {
                throw new TransactionException(String.format("Erro ao ler o arquivo: %s", path), ex);
            }
        }

       private static Statistics getStatistics(Statistics acc, ReportTransaction re) {
           return new Statistics(
                   acc.totalRegister + 1,
                   acc.totalIsFraud + (Boolean.TRUE.equals(re.isFraud) ? 1 : 0),
                   acc.totalAmount.add(re.amount));
       }

        private Statistics add(Statistics other) {
            return new Statistics(totalRegister + other.totalRegister, totalIsFraud + other.totalIsFraud, totalAmount.add(other.totalAmount));
        }
    }

    private static Optional<ReportTransaction> mapLineToReportTransaction(final String line) {
        String[] fields = line.split(",");

        try {
            return Optional.of(
                    new ReportTransaction(
                            parseBigDecimal(fields[2], line),
                            fields[9].equals("1")
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

    private static BigDecimal parseBigDecimal(String value, String line) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Campo numérico vazio: amount | linha: " + line);
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor inválido para " + "amount" + ": " + value + " | linha: " + line, ex);
        }
    }
}