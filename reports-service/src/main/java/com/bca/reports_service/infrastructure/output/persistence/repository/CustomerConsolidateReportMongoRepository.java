package com.bca.reports_service.infrastructure.output.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerConsolidateReportMongoRepository extends ReactiveMongoRepository<CustomerConsolidateReportDocument, String> {
    Mono<CustomerConsolidateReportDocument> findByCustomerId(String customerId);
}
