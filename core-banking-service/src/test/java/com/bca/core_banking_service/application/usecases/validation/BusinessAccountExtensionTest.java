package com.bca.core_banking_service.application.usecases.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.exceptions.BusinessException;

class BusinessAccountExtensionTest {

    @Test
    void validateBusinessRules_succeedsWithValidData() {
        BusinessAccountExtension ext = new BusinessAccountExtension(
                new ArrayList<>(List.of("holder-1", "holder-2")),
                new ArrayList<>(List.of("signer-1", "signer-2")));

        assertDoesNotThrow(ext::validateBusinessRules);
    }

    @Test
    void validateBusinessRules_throwsWhenNoHolders() {
        BusinessAccountExtension ext = new BusinessAccountExtension(new ArrayList<>(), new ArrayList<>());

        BusinessException ex = assertThrows(BusinessException.class, ext::validateBusinessRules);
        assertTrue(ex.getMessage().contains("at least one holder"));
    }

    @Test
    void validateBusinessRules_throwsWhenDuplicateHolders() {
        BusinessAccountExtension ext = new BusinessAccountExtension(
                new ArrayList<>(List.of("holder-1", "holder-1")),
                new ArrayList<>());

        BusinessException ex = assertThrows(BusinessException.class, ext::validateBusinessRules);
        assertTrue(ex.getMessage().contains("Duplicated holders"));
    }

    @Test
    void validateBusinessRules_throwsWhenDuplicateSigners() {
        BusinessAccountExtension ext = new BusinessAccountExtension(
                new ArrayList<>(List.of("holder-1")),
                new ArrayList<>(List.of("signer-1", "signer-1")));

        BusinessException ex = assertThrows(BusinessException.class, ext::validateBusinessRules);
        assertTrue(ex.getMessage().contains("Duplicated signers"));
    }

    @Test
    void validateBusinessRules_throwsWhenSignerIsHolder() {
        BusinessAccountExtension ext = new BusinessAccountExtension(
                new ArrayList<>(List.of("holder-1", "holder-2")),
                new ArrayList<>(List.of("holder-1")));

        BusinessException ex = assertThrows(BusinessException.class, ext::validateBusinessRules);
        assertTrue(ex.getMessage().contains("Signer cannot be holder"));
    }

    @Test
    void validateOperation_allowsHoldersAndSigners() {
        BusinessAccountExtension ext = new BusinessAccountExtension(
                new ArrayList<>(List.of("holder-1")),
                new ArrayList<>(List.of("signer-1")));

        assertDoesNotThrow(() -> ext.validateOperation("holder-1"));
        assertDoesNotThrow(() -> ext.validateOperation("signer-1"));
    }

    @Test
    void validateOperation_blocksUnauthorizedUsers() {
        BusinessAccountExtension ext = new BusinessAccountExtension(
                new ArrayList<>(List.of("holder-1")),
                new ArrayList<>(List.of("signer-1")));

        BusinessException ex = assertThrows(BusinessException.class, () -> ext.validateOperation("intruder"));
        assertTrue(ex.getMessage().contains("not authorized"));
    }

    @Test
    void addHolder_addsAndValidatesUniqueness() {
        BusinessAccountExtension ext = new BusinessAccountExtension(
                new ArrayList<>(List.of("holder-1")),
                new ArrayList<>());

        ext.addHolder("holder-2");
        assertEquals(2, ext.getHolders().size());
        assertTrue(ext.getHolders().contains("holder-2"));

        BusinessException ex = assertThrows(BusinessException.class, () -> ext.addHolder("holder-2"));
        assertTrue(ex.getMessage().contains("Duplicated holders"));
    }

    @Test
    void addSigner_blocksIfUserIsHolder() {
        BusinessAccountExtension ext = new BusinessAccountExtension(
                new ArrayList<>(List.of("holder-1")),
                new ArrayList<>());

        BusinessException ex = assertThrows(BusinessException.class, () -> ext.addSigner("holder-1"));
        assertTrue(ex.getMessage().contains("Holder cannot be signer"));
    }

    @Test
    void addSigner_addsAndValidatesUniqueness() {
        BusinessAccountExtension ext = new BusinessAccountExtension(
                new ArrayList<>(List.of("holder-1")),
                new ArrayList<>(List.of("signer-1")));

        ext.addSigner("signer-2");
        assertTrue(ext.getAuthorizedSigners().contains("signer-2"));

        BusinessException ex = assertThrows(BusinessException.class, () -> ext.addSigner("signer-2"));
        assertTrue(ex.getMessage().contains("Duplicated signers"));
    }

    @Test
    void removeSigner_silentlyRemovesIfPresent() {
        BusinessAccountExtension ext = new BusinessAccountExtension(
                new ArrayList<>(List.of("holder-1")),
                new ArrayList<>(List.of("signer-1", "signer-2")));

        ext.removeSigner("signer-1");

        assertFalse(ext.getAuthorizedSigners().contains("signer-1"));
        assertEquals(1, ext.getAuthorizedSigners().size());
    }

    @Test
    void validate_withNullSigners_skipsSignerChecks() {
        BusinessAccountExtension ext = new BusinessAccountExtension(
                new ArrayList<>(List.of("holder-1")),
                null);

        assertDoesNotThrow(ext::validateBusinessRules);
    }

    @Test
    void isHolder_and_isSigner_handleNullLists() {
        BusinessAccountExtension ext = new BusinessAccountExtension(null, null);

        assertFalse(ext.isHolder("someone"));
        assertFalse(ext.isSigner("someone"));
    }

    @Test
    void removeSigner_whenAuthorizedListNull_doesNothing() {
        BusinessAccountExtension ext = new BusinessAccountExtension(
                new ArrayList<>(List.of("holder-1")),
                null);

        assertDoesNotThrow(() -> ext.removeSigner("any"));
        assertNull(ext.getAuthorizedSigners());
    }

    @Test
    void noArgsConstructor_leavesListsNull() {
        BusinessAccountExtension ext = new BusinessAccountExtension();

        assertNull(ext.getHolders());
        assertNull(ext.getAuthorizedSigners());
    }
}
