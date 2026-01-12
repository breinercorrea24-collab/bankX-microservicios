package com.bca.users_service.infrastructure.output.persistence;

import com.bca.users_service.domain.model.User;
import com.bca.users_service.domain.ports.output.UserRepository;
import com.bca.users_service.infrastructure.output.persistence.entity.UserDocument;
import com.bca.users_service.infrastructure.output.persistence.mapper.UserMapper;
import com.bca.users_service.infrastructure.output.persistence.repository.UserMongoRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserMongoRepository mongoUserRepository;

    public UserRepositoryAdapter(UserMongoRepository mongoUserRepository) {
        this.mongoUserRepository = mongoUserRepository;
    }

    @Override
    public Mono<User> save(User user) {
        log.debug("Saving user to database: {}", user.getId());
        UserDocument document = UserMapper.toDocument(user);
        return mongoUserRepository.save(document)
                .map(UserMapper::toDomain)
                .doOnError(error -> log.error("Error saving user: {}", error.getMessage()));
    }

    @Override
    public Mono<User> findById(String id) {
        log.debug("Finding user by ID: {}", id);
        return mongoUserRepository.findById(id)
                .map(UserMapper::toDomain)
                .doOnError(error -> log.error("Error finding user by ID {}: {}", id, error.getMessage()));
    }
}