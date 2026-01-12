package com.bca.users_service.infrastructure.output.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bca.users_service.infrastructure.output.persistence.entity.UserDocument;

@Repository
public interface UserMongoRepository extends ReactiveMongoRepository<UserDocument, String> {
}