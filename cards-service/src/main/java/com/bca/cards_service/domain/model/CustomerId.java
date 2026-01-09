package com.bca.cards_service.domain.model;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public record CustomerId(String value) {
    public CustomerId {
        Objects.requireNonNull(value, "CustomerId cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("CustomerId cannot be empty");
        }
        log.debug("Created CustomerId: {}", value);
    }

    @Override
    public String toString() {
        return value;
    }
}