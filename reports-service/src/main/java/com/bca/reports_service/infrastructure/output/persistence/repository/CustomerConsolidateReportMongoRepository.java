package com.bca.reports_service.infrastructure.output.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.reports_service.infrastructure.output.persistence.entity.CustomerConsolidateReportDocument;

import reactor.core.publisher.Mono;

@Repository
public interface CustomerConsolidateReportMongoRepository extends ReactiveMongoRepository<CustomerConsolidateReportDocument, String> {
    Mono<CustomerConsolidateReportDocument> findByCustomerId(String customerId);
}
