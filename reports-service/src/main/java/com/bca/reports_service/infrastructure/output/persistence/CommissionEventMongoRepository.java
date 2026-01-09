package com.bca.reports_service.infrastructure.output.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionEventMongoRepository extends ReactiveMongoRepository<CommissionEventDocument, String> {

}
