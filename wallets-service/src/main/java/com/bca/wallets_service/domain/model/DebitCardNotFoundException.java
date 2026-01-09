package com.bca.wallets_service.domain.model;

public class DebitCardNotFoundException extends RuntimeException {
    public DebitCardNotFoundException(String message) {
        super(message);
    }
}