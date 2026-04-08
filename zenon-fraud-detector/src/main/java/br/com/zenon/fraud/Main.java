package br.com.zenon.fraud;

import br.com.zenon.fraud.enums.TransactionType;
import br.com.zenon.fraud.model.Transaction;
import br.com.zenon.fraud.model.TransactionCustomer;
import br.com.zenon.fraud.service.TransactionIngestor;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static void main() throws IOException {

        Transaction transaction1 = new Transaction(
                1, TransactionType.PAYMENT, BigDecimal.valueOf(9839.64),
                new TransactionCustomer("C1231006815", BigDecimal.valueOf(170136.0), BigDecimal.valueOf(160296.36)),
                new TransactionCustomer("M1979787155", BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0)),
                false, false);

        Transaction transaction2 = new Transaction(
                743, TransactionType.CASH_OUT, BigDecimal.valueOf(850002.52),
                new TransactionCustomer(" C1280323807", BigDecimal.valueOf(850002.52), BigDecimal.valueOf(0.0)),
                new TransactionCustomer("C873221189", BigDecimal.valueOf(6510099.11), BigDecimal.valueOf(7360101.63)),
                true, false);

        LOGGER.info(() -> "Transação 1: " + transaction1);
        LOGGER.info(() -> "Transação 2: " + transaction2);

        List<Transaction> transactions = TransactionIngestor.readTransactions("data/PS_20174392719_1491204439457_log.csv", 10);

        transactions.forEach(IO::println);
    }
}