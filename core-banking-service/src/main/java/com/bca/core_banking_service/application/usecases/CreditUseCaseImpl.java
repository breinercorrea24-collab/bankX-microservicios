package com.bca.core_banking_service.application.usecases;

import com.bca.core_banking_service.application.ports.input.usecases.CreditUseCase;
import com.bca.core_banking_service.application.usecases.validation.ValidationCredit;
import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.enums.credit.CreditType;
import com.bca.core_banking_service.domain.ports.output.persistence.CreditRepository;
import com.bca.core_banking_service.infrastructure.input.dto.Credit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreditUseCaseImpl implements CreditUseCase {

    private final CreditRepository creditRepository;
    private final ValidationCredit validationCredit;

    

    @Override
    public Mono<Credit> createCredit(String customerId,
            Credit.CreditType creditType,
            BigDecimal amount,
            Integer termMonths,
            BigDecimal interestRate) {

        if (amount.compareTo(BigDecimal.valueOf(50000)) > 0) {
            return Mono.error(new BusinessException(
                "Credit amount exceeds maximum allowed"));
        }


        return validationCredit
            .validateCreditCreation(customerId, CustomerType.PERSONAL, CreditType.valueOf(creditType.name()) )
            .then(Mono.fromSupplier(() -> buildCredit(
                customerId,
                creditType,
                amount,
                termMonths,
                interestRate)))
            .flatMap(creditRepository::save);
    }

    @Override
    public Mono<Credit> payCredit(String creditId, BigDecimal amount) {
        return creditRepository.findById(creditId)
                .flatMap(credit -> {
                    if (credit.getPendingDebt().compareTo(amount) < 0) {
                        return Mono.error(new RuntimeException("Payment amount exceeds pending debt"));
                    }

                    credit.setPendingDebt(credit.getPendingDebt().subtract(amount));

                    if (credit.getPendingDebt().compareTo(BigDecimal.ZERO) == 0) {
                        credit.setStatus(Credit.CreditStatus.PAID);
                    }

                    return creditRepository.save(credit);
                });
    }

    private Credit buildCredit(
            String customerId,
            Credit.CreditType creditType,
            BigDecimal amount,
            Integer termMonths,
            BigDecimal interestRate) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime due = termMonths == null ? now : now.plusMonths(termMonths);

        return new Credit(
                null,
                customerId,
                creditType,
                amount, // originalAmount
                amount, // pendingDebt initially equals amount
                interestRate,
                termMonths,
                Credit.CreditStatus.ACTIVE,
                now,
                due
        );
    }
}