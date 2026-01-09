package com.bca.reports_service.application.usecases;

import com.bca.reports_service.domain.model.TransactionReport;
import com.bca.reports_service.domain.ports.output.TransactionReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCardTransactionsUseCase {

    private final TransactionReportRepository transactionReportRepository;

    public Mono<TransactionReport> execute(String cardId) {
        log.info("Getting card transactions for cardId: {}", cardId);
        return transactionReportRepository.findByCardId(cardId)
                .doOnNext(report -> log.info("Successfully retrieved transaction report for card: {}", cardId))
                .doOnError(error -> log.error("Error retrieving transaction report for cardId: {}", cardId, error));
    }
}