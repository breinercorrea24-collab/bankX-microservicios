package com.bca.cards_service.domain.model;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public record CardId(String value) {
    public CardId {
        Objects.requireNonNull(value, "CardId cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("CardId cannot be empty");
        }
        log.debug("Created CardId: {}", value);
    }

    @Override
    public String toString() {
        return value;
    }
}