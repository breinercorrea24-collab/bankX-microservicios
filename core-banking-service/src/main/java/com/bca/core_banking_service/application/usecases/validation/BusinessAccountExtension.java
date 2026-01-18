package com.bca.core_banking_service.application.usecases.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bca.core_banking_service.domain.exceptions.BusinessException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessAccountExtension {

    private List<String> holders;           // Titulares
    private List<String> authorizedSigners; // Firmantes

    /* ========= VALIDACIÓN GLOBAL ========= */

    public void validateBusinessRules() {
        validate();
    }

    /* ========= AUTORIZACIÓN ========= */

    public void validateOperation(String userId) {

        boolean isHolder = isHolder(userId);
        boolean isSigner = isSigner(userId);

        if (!isHolder && !isSigner) {
            throw new BusinessException(
                "User not authorized to operate this account");
        }
    }

    /* ========= VALIDACIONES ========= */

    public void validate() {

        if (holders == null || holders.isEmpty()) {
            throw new BusinessException(
                "Business account must have at least one holder");
        }

        validateUniqueHolders();
        validateUniqueSigners();
        validateSignerNotHolder();
    }

    private void validateUniqueHolders() {

        Set<String> unique = new HashSet<>(holders);

        if (unique.size() != holders.size()) {
            throw new BusinessException(
                "Duplicated holders are not allowed");
        }
    }

    private void validateUniqueSigners() {

        if (authorizedSigners == null) return;

        Set<String> unique = new HashSet<>(authorizedSigners);

        if (unique.size() != authorizedSigners.size()) {
            throw new BusinessException(
                "Duplicated signers are not allowed");
        }
    }

    private void validateSignerNotHolder() {

        if (authorizedSigners == null) return;

        for (String signer : authorizedSigners) {
            if (holders.contains(signer)) {
                throw new BusinessException(
                    "Signer cannot be holder at same time");
            }
        }
    }

    /* ========= REGLAS ========= */

    public boolean isHolder(String userId) {
        return holders != null && holders.contains(userId);
    }

    public boolean isSigner(String userId) {
        return authorizedSigners != null &&
               authorizedSigners.contains(userId);
    }

    public void addHolder(String userId) {

        holders.add(userId);
        validateUniqueHolders();
    }

    public void addSigner(String userId) {

        if (holders.contains(userId)) {
            throw new BusinessException(
                "Holder cannot be signer");
        }

        authorizedSigners.add(userId);
        validateUniqueSigners();
    }

    public void removeSigner(String userId) {

        if (authorizedSigners != null) {
            authorizedSigners.remove(userId);
        }
    }
}
