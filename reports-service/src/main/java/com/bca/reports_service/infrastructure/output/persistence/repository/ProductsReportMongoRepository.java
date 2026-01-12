package com.bca.reports_service.infrastructure.output.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.reports_service.infrastructure.output.persistence.entity.ProductsReportEventDocument;

import reactor.core.publisher.Mono;

@Repository
public interface ProductsReportMongoRepository extends ReactiveMongoRepository<ProductsReportEventDocument, String> {
    Mono<ProductsReportEventDocument> findByProductId(String productId);
}
