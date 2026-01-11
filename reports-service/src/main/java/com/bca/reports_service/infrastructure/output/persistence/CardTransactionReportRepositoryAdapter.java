package com.bca.reports_service.infrastructure.output.persistence;

import com.bca.reports_service.domain.model.TransactionReport;
import com.bca.reports_service.domain.ports.output.TransactionReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;

import java.util.List;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardTransactionReportRepositoryAdapter implements TransactionReportRepository {

    private final CardTransactionReportMongoRepository mongoRepository;

    public Mono<TransactionReport> findByCardId(String cardId) {

        return mongoRepository
                .findTop10ByCardIdOrderByTransactionDateDesc(cardId)
                .collectList()
                .map(CardTransactionReportMapper::toDomain);
    }

}
