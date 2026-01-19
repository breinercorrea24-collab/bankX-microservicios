package com.bca.core_banking_service.infrastructure.input.rest.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import javax.security.auth.login.AccountNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.dto.ErrorResponse;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleBusiness_returnsConflictWithMessageAndPath() {
        BusinessException ex = new BusinessException("business-failure");
        MockServerWebExchange exchange = exchange("/business");

        var response = handler.handleBusiness(ex, exchange);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("business-failure", body.getMessage());
        assertEquals("Conflict", body.getError());
        assertEquals(HttpStatus.CONFLICT.value(), body.getStatus());
        assertEquals("/business", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleValidation_returnsBadRequestWithGenericMessage() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        MockServerWebExchange exchange = exchange("/validation");

        var response = handler.handleValidation(ex, exchange);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Solicitud inválida", body.getMessage());
        assertEquals("Bad Request", body.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("/validation", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleJsonError_returnsBadRequestWithSpecificMessage() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("json-error");
        MockServerWebExchange exchange = exchange("/json");

        var response = handler.handleJsonError(ex, exchange);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("JSON inválido o enum incorrecto", body.getMessage());
        assertEquals("Bad Request", body.getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("/json", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleAccountNotFound_returnsNotFoundWithMessage() {
        AccountNotFoundException ex = new AccountNotFoundException("account missing");
        MockServerWebExchange exchange = exchange("/accounts/123");

        var response = handler.handleAccountNotFound(ex, exchange);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("account missing", body.getMessage());
        assertEquals("Not Found", body.getError());
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
        assertEquals("/accounts/123", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    private MockServerWebExchange exchange(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
    }
}
