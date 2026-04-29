package br.com.zenon.fraud.db;

import br.com.zenon.fraud.exceptions.TransactionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private ConnectionFactory() {
    }

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/zenon_fraude", System.getenv("MYSQL_ROOT_USER"), System.getenv("MYSQL_ROOT_PASSWORD"));
        } catch (SQLException e) {
            throw new TransactionException("Erro ao conectar com o Banco de Dados", e);
        }
    }
}