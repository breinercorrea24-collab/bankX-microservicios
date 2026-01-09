package com.bca.core_banking_service.application.usecases;

import com.bca.core_banking_service.domain.model.Credit;
import com.bca.core_banking_service.domain.ports.input.CreditUseCase;
import com.bca.core_banking_service.domain.ports.output.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditUseCaseImpl implements CreditUseCase {

    private final CreditRepository creditRepository;

    @Override
    public Mono<Credit> createCredit(String customerId, Credit.CreditType creditType,
                                   BigDecimal amount, Integer termMonths,
                                   BigDecimal interestRate) {
        // Simple credit approval logic - in real app this would be more complex
        if (amount.compareTo(BigDecimal.valueOf(50000)) > 0) {
            return Mono.error(new RuntimeException("Credit amount exceeds maximum allowed"));
        }

        Credit credit = new Credit();
        credit.setId("cred-" + UUID.randomUUID().toString().substring(0, 8));
        credit.setCustomerId(customerId);
        credit.setCreditType(creditType);
        credit.setOriginalAmount(amount);
        credit.setPendingDebt(amount);
        credit.setInterestRate(interestRate);
        credit.setTermMonths(termMonths);
        credit.setStatus(Credit.CreditStatus.ACTIVE);
        credit.setCreatedAt(LocalDateTime.now());

        return creditRepository.save(credit);
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
}