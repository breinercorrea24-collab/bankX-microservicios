package com.bca.core_banking_service.infrastructure.input.rest.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import javax.security.auth.login.AccountNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.dto.ErrorResponse;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/test")
                    .build());

    @Test
    void handleBusinessReturnsConflictResponse() {
        BusinessException exception = new BusinessException("business issue");

        ResponseEntity<ErrorResponse> response = handler.handleBusiness(exception, exchange);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("business issue", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleValidationReturnsBadRequest() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(exception, exchange);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Solicitud inválida", response.getBody().getMessage());
    }

    @Test
    void handleJsonErrorReturnsBadRequest() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("bad json",
                (HttpInputMessage) null);

        ResponseEntity<ErrorResponse> response = handler.handleJsonError(exception, exchange);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("JSON inválido o enum incorrecto", response.getBody().getMessage());
    }

    @Test
    void handleAccountNotFoundReturnsNotFound() {
        AccountNotFoundException exception = new AccountNotFoundException("missing account");

        ResponseEntity<ErrorResponse> response = handler.handleAccountNotFound(exception, exchange);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("missing account", response.getBody().getMessage());
    }
}
