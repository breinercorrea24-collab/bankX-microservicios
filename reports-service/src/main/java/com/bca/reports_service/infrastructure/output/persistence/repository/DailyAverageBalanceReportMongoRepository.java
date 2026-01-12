package com.bca.reports_service.infrastructure.output.persistence.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.reports_service.infrastructure.output.persistence.entity.DailyAverageBalanceReportDocument;

import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Repository
public interface DailyAverageBalanceReportMongoRepository extends ReactiveMongoRepository<DailyAverageBalanceReportDocument, String> {

    @Query("{ 'customerId': ?0, 'date': { $gte: ?1, $lte: ?2 } }")
    Flux<DailyAverageBalanceReportDocument> findByCustomerIdAndDateBetween(
            String customerId, 
            LocalDateTime startDateTime,
            LocalDateTime endDateTime);
}