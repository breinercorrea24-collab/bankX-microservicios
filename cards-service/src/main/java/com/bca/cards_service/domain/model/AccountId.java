package com.bca.cards_service.domain.model;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public record AccountId(String value) {
    public AccountId {
        Objects.requireNonNull(value, "AccountId cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("AccountId cannot be empty");
        }
        log.debug("Created AccountId: {}", value);
    }

    @Override
    public String toString() {
        return value;
    }
}