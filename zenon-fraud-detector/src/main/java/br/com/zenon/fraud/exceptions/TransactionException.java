package br.com.zenon.fraud.exceptions;

public class TransactionException extends RuntimeException {

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }
}