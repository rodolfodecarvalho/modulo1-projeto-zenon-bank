package br.com.zenon.fraud;

import br.com.zenon.fraud.db.ConnectionFactory;
import br.com.zenon.fraud.model.Transaction;
import br.com.zenon.fraud.repositoy.TransactionSQLRepository;
import br.com.zenon.fraud.service.TransactionIngestor;

import java.util.List;

public class DBMainSaveAll {
    void main() {

        ConnectionFactory.getConnection();
        IO.println("Conexão com o Banco de Dados criada!");

        TransactionSQLRepository repository = new TransactionSQLRepository();

        List<Transaction> transactions = TransactionIngestor.readTransactions("data/PS_20174392719_1491204439457_log.csv");
        IO.println(transactions.size());

        long startTimeSQL = System.nanoTime();
        IO.println("Iniciando adicao das transacoes no BD em Batch...");
        repository.saveAll(transactions);
        long endTimeSQL = System.nanoTime();
        IO.println("Tempo de inserção Batch - SQL (ms): " + (endTimeSQL - startTimeSQL) / 1_000_000.0);
    }
}