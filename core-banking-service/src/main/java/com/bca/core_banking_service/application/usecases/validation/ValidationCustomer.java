package com.bca.core_banking_service.application.usecases.validation;

import org.springframework.http.ResponseEntity;

import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.ports.output.persistence.AccountRepository;
import com.bca.core_banking_service.domain.ports.output.rest.ExternalCardsClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ValidationCustomer {

    private final AccountRepository accountRepository;
    private final ExternalCardsClient externalCardsClient;

    public Mono<Void> validatePersonalCustomer(String customerId, AccountType type, CustomerType customerType) {

        // Personal customers: only one account per type (SAVINGS, CHECKING, FIXED_TERM)
        if (type == AccountType.SAVINGS || type == AccountType.CHECKING || type == AccountType.FIXED_TERM) {
            return accountRepository.findByCustomerId(customerId)
                    .filter(a -> a.getType() == type)
                    .hasElements()
                    .flatMap(exists -> exists
                            ? Mono.<Void>error(new BusinessException(
                                    "Only one account of this type is allowed for personal customers"))
                            : Mono.empty());
        }
        return Mono.empty();
    }

    public Mono<Void> validateBusinessCustomer(String customerId, AccountType type, CustomerType customerType) {
        // Business customers cannot open SAVINGS or FIXED_TERM
        if (type == AccountType.SAVINGS || type == AccountType.FIXED_TERM) {
            return Mono.error(new BusinessException("Business customer cannot open " + type));
        }
        // multiple checking accounts are allowed -> no check needed
        return Mono.empty();
    }

    public Mono<Void> validateVipPersonalCustomer(String customerId, AccountType type, CustomerType customerType) {
        // If product is VIP_SAVINGS ensure customer is VIP personal
        if (type == AccountType.VIP_SAVINGS) {
            return externalCardsClient.hasCreditCard(customerId)
                    .map(ResponseEntity::getBody)
                    .flatMap(hasCard -> hasCard
                            ? Mono.empty()
                            : Mono.error(
                                    new BusinessException("This product requires active credit card")));
        }
        return Mono.empty();
    }

    public Mono<Void> validatePymeBusinessCustomer(String customerId, AccountType type, CustomerType customerType) {
        if (type == AccountType.PYME_CHECKING) {
            return externalCardsClient.hasCreditCard(customerId)
                    .map(ResponseEntity::getBody)
                    .flatMap(hasCard -> hasCard
                            ? Mono.empty()
                            : Mono.error(
                                    new BusinessException("This product requires active credit card")));
        }
        return Mono.empty();
    }
}
