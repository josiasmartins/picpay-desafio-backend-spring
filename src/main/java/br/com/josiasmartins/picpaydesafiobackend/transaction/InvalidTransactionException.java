package br.com.josiasmartins.picpaydesafiobackend.transaction;

public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(String message) {
        super(message);
    }

}
