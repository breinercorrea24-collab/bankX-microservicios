package com.bca.wallets_service.domain.model;

public class InvalidWalletTypeException extends RuntimeException {
    public InvalidWalletTypeException(String message) {
        super(message);
    }
}