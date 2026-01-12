package com.bca.core_banking_service.domain.exceptions;

public class BusinessException extends RuntimeException {
    public BusinessException(String msg){
        super(msg);
    }
}
