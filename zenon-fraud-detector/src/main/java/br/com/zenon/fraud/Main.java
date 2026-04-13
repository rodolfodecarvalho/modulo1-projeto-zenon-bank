package br.com.zenon.fraud;

import br.com.zenon.fraud.enums.TransactionType;
import br.com.zenon.fraud.model.Transaction;
import br.com.zenon.fraud.model.TransactionCustomer;
import br.com.zenon.fraud.repositoy.TransactionListRepository;
import br.com.zenon.fraud.repositoy.TransactionMapRepository;
import br.com.zenon.fraud.repositoy.TransactionRepository;
import br.com.zenon.fraud.service.TransactionIngestor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static br.com.zenon.fraud.service.FraudAnalyzer.*;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static void main() {

        Transaction transaction1 = new Transaction(
                1, TransactionType.PAYMENT, BigDecimal.valueOf(9839.64),
                new TransactionCustomer("C1231006816", BigDecimal.valueOf(170136.0), BigDecimal.valueOf(160296.36)),
                new TransactionCustomer("M1979787155", BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.0)),
                false, false);

        Transaction transaction2 = new Transaction(
                743, TransactionType.CASH_OUT, BigDecimal.valueOf(850002.52),
                new TransactionCustomer("C1280323807", BigDecimal.valueOf(850002.52), BigDecimal.valueOf(0.0)),
                new TransactionCustomer("C873221189", BigDecimal.valueOf(6510099.11), BigDecimal.valueOf(7360101.63)),
                true, false);

        LOGGER.info(() -> "Transação 1: " + transaction1);
        LOGGER.info(() -> "Transação 2: " + transaction2);

        LOGGER.info("----------------------------------------------------------------------------------------------");

        List<Transaction> transactions = TransactionIngestor.readTransactions("data/PS_20174392719_1491204439457_log.csv", 100_000);

        LOGGER.info(() -> "Transactions size: " + transactions.size());

        transactions.forEach(transaction -> LOGGER.info(transaction.toString()));

        LOGGER.info("----------------------------------------------------------------------------------------------");

        LOGGER.info(() -> "Total de Fraudes: " + isFraudTrue(transactions));
        int countFraude = 3;
        int countSuspeito = 5;

        Set<Transaction> topNByTransaction = topNByTransaction(transactions, countFraude);

        LOGGER.info(() -> "Top " + countFraude + " Fraudes de Maior Valor:");
        List<BigDecimal> bigDecimalList = getAmounts(topNByTransaction);
        bigDecimalList.forEach(amount -> LOGGER.info(amount.toPlainString()));

        LOGGER.info(() -> "Top " + countFraude + " Clientes suspeitos:");
        Set<String> topNByNameOrig = topNSuspectByNameOrig(transactions, countSuspeito);
        topNByNameOrig.forEach(LOGGER::info);

        LOGGER.info(() -> "Prejuízo Total:");
        LOGGER.info(() -> totalAmount(transactions).toPlainString());

        LOGGER.info(() -> "Fraudes por Tipo:");
        Map<TransactionType, Long> transactionTypeLongMap = totalByTransactionType(transactions);
        transactionTypeLongMap.forEach((type, count) -> LOGGER.info("- %s: %d".formatted(type, count)));

        TransactionRepository transactionRepository;
        transactionRepository = new TransactionListRepository(transactions);

        Optional<Transaction> transactionByOrigemName = transactionRepository.getTransactionByOrigemName("C1231006815");

        LOGGER.log(Level.INFO, String.format("%s", transactionByOrigemName));

        long startTime = System.nanoTime();
        transactionByOrigemName = transactionRepository.getTransactionByOrigemName("C1868032458");
        LOGGER.log(Level.INFO, String.format("%s", transactionByOrigemName));
        long endTime = System.nanoTime();


        LOGGER.log(Level.INFO, String.format("%s", TimeUnit.NANOSECONDS.toMillis(endTime - startTime)));

        transactionRepository = new TransactionMapRepository(transactions);

        transactionByOrigemName = transactionRepository.getTransactionByOrigemName("C1231006815");

        LOGGER.log(Level.INFO, String.format("%s", transactionByOrigemName));

        startTime = System.nanoTime();
        transactionByOrigemName = transactionRepository.getTransactionByOrigemName("C1868032458");
        LOGGER.log(Level.INFO, String.format("%s", transactionByOrigemName));
        endTime = System.nanoTime();

        LOGGER.log(Level.INFO, String.format("%s", TimeUnit.NANOSECONDS.toMillis(endTime - startTime)));
    }
}