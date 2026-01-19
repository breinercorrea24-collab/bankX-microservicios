package com.bca.core_banking_service.application.usecases.validation;

import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.ports.output.persistence.AccountRepository;
import com.bca.core_banking_service.domain.ports.output.persistence.CreditRepository;
import com.bca.core_banking_service.infrastructure.output.rest.ExternalCardsWebClientAdapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class ValidationProduct {

    private final ExternalCardsWebClientAdapter externalCardsClient;
    private final AccountRepository accountRepository;
    private final CreditRepository creditRepository;

    /**
     * Validates business rules for account creation.
     *
     * - Enforces PERSONAL/BUSINESS rules (max one account per type for PERSONAL,
     * no SAVINGS/FIXED_TERM for BUSINESS).
     * - Enforces VIP_SAVINGS and PYME_CHECKING credit-card requirement.
     * - Checks for overdue credits (TODO: implement real check against
     * CreditRepository).
     */
    public Mono<Void> validateAccountCreation(
            String customerId,
            AccountType type,
            CustomerType customerType) {

        log.info("Starting account validation: customerId={}, type={}, customerType={}",
                customerId, type, customerType);

        ValidationCustomer validator = new ValidationCustomer(accountRepository, externalCardsClient);

        return hasOverdueCredits(customerId)
                .doOnNext(hasOverdue -> log.info("Overdue check for {} => {}", customerId, hasOverdue))
                .flatMap(hasOverdue -> {
                    if (hasOverdue) {
                        log.warn("Customer {} has overdue credit debt", customerId);
                        return Mono.error(new BusinessException("Customer has overdue credit debt"));
                    }

                    return applyCustomerRules(validator, customerId, type, customerType);
                })
                .doOnSuccess(v -> log.info("Account validation SUCCESS for {}", customerId))
                .doOnError(err -> log.error("Account validation FAILED for {}: {}",
                        customerId, err.getMessage()));
    }

    private Mono<Void> applyCustomerRules(
            ValidationCustomer validator,
            String customerId,
            AccountType type,
            CustomerType customerType) {

        if (customerType == null) {
            log.info("No specific rules -> allowing creation (customerType null)");
            return Mono.empty();
        }

        switch (customerType) {

            case PERSONAL:
                log.info("Applying PERSONAL rules");
                return validator
                        .validatePersonalCustomer(customerId, type, customerType);

            case BUSINESS:
                log.info("Applying BUSINESS rules");
                return validator
                        .validateBusinessCustomer(customerId, type, customerType);

            case VIPPERSONAL:
                log.info("Applying VIPPERSONAL rules");
                return validator
                        .validateVipPersonalCustomer(customerId, type, customerType);

            case PYMEBUSINESS:
                log.info("Applying PYMEBUSINESS rules");
                return validator
                        .validatePymeBusinessCustomer(customerId, type, customerType);

            default:
                log.info("No specific rules -> allowing creation");
                return Mono.empty();
        }
    }

    private Mono<Void> validateByCustomerType(String customerId, AccountType type, CustomerType customerType) {
        ValidationCustomer validationCustomer = new ValidationCustomer(accountRepository, externalCardsClient);

        if (customerType == CustomerType.PERSONAL) {
            return validationCustomer.validatePersonalCustomer(customerId, type, customerType);
        }

        if (customerType == CustomerType.BUSINESS) {
            return validationCustomer.validateBusinessCustomer(customerId, type, customerType);
        }

        if (customerType == CustomerType.VIPPERSONAL) {
            return validationCustomer.validateVipPersonalCustomer(customerId, type, customerType);
        }

        if (customerType == CustomerType.PYMEBUSINESS) {
            return validationCustomer.validatePymeBusinessCustomer(customerId, type, customerType);
        }

        return Mono.empty();
    }

    /**
     * Placeholder implementation. Replace with real check against CreditRepository
     * to detect overdue debts for the customer.
     */
    protected Mono<Boolean> hasOverdueCredits(String customerId) {
        return creditRepository
                .hasOverdueCredits(customerId)
                .doOnSubscribe(s -> log.info("Checking overdue credits (call to CreditRepository) for {}", customerId))
                .doOnSuccess(result -> log.info("CreditRepository.hasOverdueCredits result for {} => {}", customerId,
                        result));
    }

}
