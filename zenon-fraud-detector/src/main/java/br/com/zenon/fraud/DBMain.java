package br.com.zenon.fraud;

import br.com.zenon.fraud.db.ConnectionFactory;
import br.com.zenon.fraud.model.Transaction;
import br.com.zenon.fraud.repositoy.TransactionSQLRepository;
import br.com.zenon.fraud.service.TransactionIngestor;

import java.util.List;

public class DBMain {
    void main() {

        ConnectionFactory.getConnection();
        IO.println("Conexão com o Banco de Dados criada!");

        TransactionSQLRepository repository = new TransactionSQLRepository();

        List<Transaction> transactions = TransactionIngestor.readTransactions("data/PS_20174392719_1491204439457_log.csv", 10_000);
        IO.println(transactions.size());

        long startTimeSQL = System.nanoTime();
        IO.println("Iniciando adicao das transacoes no BD Um por um...");
        transactions.forEach(repository::save);

        long endTimeSQL = System.nanoTime();
        IO.println("Tempo de inserção Um por um - SQL (ms): " + (endTimeSQL - startTimeSQL) / 1_000_000.0);

        startTimeSQL = System.nanoTime();
        IO.println("Iniciando adicao das transacoes no BD em Batch...");
        repository.saveAll(transactions);
        endTimeSQL = System.nanoTime();
        IO.println("Tempo de inserção Batch - SQL (ms): " + (endTimeSQL - startTimeSQL) / 1_000_000.0);

        repository.findByOriginName("C1231006815")
                  .ifPresentOrElse(IO::println, () -> IO.println("Transacao nao encontrada para: C1231006815"));

        repository.findByOriginName("C123100681556")
                  .ifPresentOrElse(IO::println, () -> IO.println("Transacao nao encontrada para: C123100681556"));
    }
}