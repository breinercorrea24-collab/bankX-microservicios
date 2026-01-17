package com.bca.core_banking_service.domain.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BusinessExceptionTest {

    @Test
    void constructorStoresMessage() {
        BusinessException exception = new BusinessException("custom message");

        assertEquals("custom message", exception.getMessage());
    }
}
