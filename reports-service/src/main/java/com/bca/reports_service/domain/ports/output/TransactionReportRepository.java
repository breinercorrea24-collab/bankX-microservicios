package com.bca.reports_service.domain.ports.output;

import com.bca.reports_service.domain.model.TransactionReport;
import reactor.core.publisher.Mono;

public interface TransactionReportRepository {
    Mono<TransactionReport> findByCardId(String cardId);
}