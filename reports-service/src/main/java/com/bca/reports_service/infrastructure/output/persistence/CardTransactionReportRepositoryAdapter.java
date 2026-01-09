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
                .map(this::toDomain);
    }

    private TransactionReport toDomain(List<CardTransactionReportDocument> docs) {

        if (docs == null || docs.isEmpty()) {
            return TransactionReport.empty();
        }

        CardTransactionReportDocument first = docs.get(0);

        return new TransactionReport(
                new TransactionReport.Card(
                        first.getCardId(),
                        first.getCardType(),
                        first.getMaskedNumber(),
                        first.getCurrency()
                ),
                docs.stream()
                        .map(d -> new TransactionReport.Transaction(
                                null, // transactionId
                                d.getMovementType(),
                                d.getAmount() != null ? d.getAmount() : 0.0,
                                d.getStatus(),
                                d.getTransactionDate() != null ? d.getTransactionDate().atZone(ZoneOffset.UTC).toLocalDateTime() : null,
                                d.getMerchant()
                        ))
                        .toList(),
                new TransactionReport.Metadata(docs.size(), 10) // Metadata
        );
    }

}
