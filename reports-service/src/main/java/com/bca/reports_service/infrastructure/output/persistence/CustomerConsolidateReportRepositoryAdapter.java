package com.bca.reports_service.infrastructure.output.persistence;

import com.bca.reports_service.domain.model.CustomerConsolidateReport;
import com.bca.reports_service.domain.ports.output.CustomerConsolidateReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

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
                .map(this::toDomain)
                .doOnNext(domain -> log.info("Successfully mapped customer consolidate report for customer: {}",
                        domain.getCustomerId()));
    }

}
