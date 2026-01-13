package com.bca.core_banking_service.infrastructure.input.rest.exception;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(OffsetDateTime.now());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setError("Conflict");
        error.setMessage(ex.getMessage());
        error.setPath(exchange.getRequest().getPath().value());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(OffsetDateTime.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("Bad Request");
        error.setMessage("Solicitud inválida");
        error.setPath(exchange.getRequest().getPath().value());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonError(
            HttpMessageNotReadableException ex,
            ServerWebExchange exchange) {

        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(OffsetDateTime.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError("Bad Request");
        error.setMessage("JSON inválido o enum incorrecto");
        error.setPath(exchange.getRequest().getPath().value());

        return ResponseEntity.badRequest().body(error);
    }
}
