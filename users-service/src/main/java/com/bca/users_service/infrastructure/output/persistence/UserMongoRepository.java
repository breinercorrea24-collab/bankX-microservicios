package com.bca.users_service.infrastructure.output.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMongoRepository extends ReactiveMongoRepository<UserDocument, String> {
}