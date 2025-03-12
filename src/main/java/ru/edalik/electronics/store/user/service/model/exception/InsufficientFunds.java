package ru.edalik.electronics.store.user.service.model.exception;

public class InsufficientFunds extends RuntimeException {

    public InsufficientFunds() {
        super("Insufficient funds");
    }

}