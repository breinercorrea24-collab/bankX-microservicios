package com.bca.reports_service.infrastructure.output.persistence;

import com.bca.reports_service.domain.model.CustomerConsolidateReport;
import com.bca.reports_service.domain.ports.output.CustomerConsolidateReportRepository;
import com.bca.reports_service.infrastructure.output.persistence.mapper.CustomerConsolidateReportMapper;
import com.bca.reports_service.infrastructure.output.persistence.repository.CustomerConsolidateReportMongoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerConsolidateReportRepositoryAdapter implements CustomerConsolidateReportRepository {

    private final CustomerConsolidateReportMongoRepository mongoRepository;

    @Override
    public Mono<CustomerConsolidateReport> findByCustomerId(String customerId) {
        log.info("Finding customer consolidate report for customerId: {}", customerId);
        return mongoRepository.findByCustomerId(customerId)
                .doOnNext(document -> log.debug("Found customer consolidate document: {}", document.getId()))
                .map(CustomerConsolidateReportMapper::toDomain)
                .doOnNext(domain -> log.info("Successfully mapped customer consolidate report for customer: {}",
                        domain.getCustomerId()));
    }

}
