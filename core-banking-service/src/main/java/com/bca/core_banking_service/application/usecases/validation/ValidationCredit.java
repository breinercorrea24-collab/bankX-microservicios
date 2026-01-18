package com.bca.core_banking_service.application.usecases.validation;

import org.springframework.stereotype.Service;

import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.enums.credit.CreditType;
import com.bca.core_banking_service.domain.ports.output.persistence.CreditRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ValidationCredit {

    private final CreditRepository creditRepository;

    public Mono<Void> validateCreditCreation(
            String customerId,
            CustomerType customerType,
            CreditType creditType) {

        // CREDIT CARD -> permitido para ambos
        /* if (creditType == CreditType.CREDIT_CARD) {
            return Mono.empty();
        } */

        // PERSONAL -> solo 1 crédito
        if (customerType == CustomerType.PERSONAL) {
            return validateSingleCredit(customerId);
        }

        // BUSINESS -> sin límite
        if (customerType == CustomerType.BUSINESS) {
            return Mono.empty();
        }

        return Mono.error(
            new BusinessException("Invalid customer type"));
    }

    private Mono<Void> validateSingleCredit(String customerId) {

        return creditRepository
                .countByCustomerId(customerId)
                .flatMap(count -> {

                    if (count > 0) {
                        return Mono.error(
                           new BusinessException(
                              "PERSONAL customer can only have one credit"));
                    }

                    return Mono.empty();
                });
    }
}