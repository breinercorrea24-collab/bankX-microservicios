package com.bca.reports_service.infrastructure.output.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.reports_service.infrastructure.output.persistence.entity.CommissionEventDocument;

@Repository
public interface CommissionEventMongoRepository extends ReactiveMongoRepository<CommissionEventDocument, String> {

}
