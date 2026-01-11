package com.bca.reports_service.infrastructure.output.persistence.mapper;

import java.util.List;

import com.bca.reports_service.domain.model.TransactionReport;
import com.bca.reports_service.infrastructure.output.persistence.entity.CardTransactionReportDocument;

public class CardTransactionReportMapper {
    public static TransactionReport toDomain(List<CardTransactionReportDocument> docs) {

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
