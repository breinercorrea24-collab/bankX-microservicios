package com.bca.core_banking_service.domain.ports.input;

import com.bca.core_banking_service.domain.model.Credit;
import reactor.core.publisher.Mono;

public interface CreditUseCase {
    Mono<Credit> createCredit(String customerId, Credit.CreditType creditType,
                             java.math.BigDecimal amount, Integer termMonths,
                             java.math.BigDecimal interestRate);
    Mono<Credit> payCredit(String creditId, java.math.BigDecimal amount);
}