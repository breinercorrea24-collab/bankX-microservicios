package com.bca.core_banking_service.application.usecases.validation;

import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.ports.output.persistence.AccountRepository;
import com.bca.core_banking_service.infrastructure.output.rest.ExternalCardsWebClientAdapter;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ValidationProduct {

    private final ExternalCardsWebClientAdapter externalCardsClient;
    private final AccountRepository accountRepository;
                            


    /**
     * Validates business rules for account creation.
     *
     * - Enforces PERSONAL/BUSINESS rules (max one account per type for PERSONAL,
     * no SAVINGS/FIXED_TERM for BUSINESS).
     * - Enforces VIP_SAVINGS and PYME_CHECKING credit-card requirement.
     * - Checks for overdue credits (TODO: implement real check against
     * CreditRepository).
     */
    public Mono<Void> validateAccountCreation(String customerId, AccountType type, CustomerType customerType) {
        return hasOverdueCredits(customerId)
                .flatMap(hasOverdue -> {
                    if (Boolean.TRUE.equals(hasOverdue)) {
                        return Mono.error(new BusinessException("Customer has overdue credit debt"));
                    }
                    return validateByCustomerType(customerId, type, customerType);
                });
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
        // TODO: inject CreditRepository and implement real overdue debt check
        return Mono.just(false);
    }

}
