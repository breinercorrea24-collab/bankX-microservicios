package com.bca.reports_service.infrastructure.output.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductsReportMongoRepository extends ReactiveMongoRepository<ProductsReportEventDocument, String> {
    Mono<ProductsReportEventDocument> findByProductId(String productId);
}
