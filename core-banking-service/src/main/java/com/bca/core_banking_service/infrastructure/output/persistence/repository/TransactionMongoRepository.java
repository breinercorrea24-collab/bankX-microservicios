package com.bca.core_banking_service.infrastructure.output.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.core_banking_service.infrastructure.output.persistence.entity.TransactionEntity;

@Repository
public interface TransactionMongoRepository extends ReactiveMongoRepository<TransactionEntity, String> {
}
