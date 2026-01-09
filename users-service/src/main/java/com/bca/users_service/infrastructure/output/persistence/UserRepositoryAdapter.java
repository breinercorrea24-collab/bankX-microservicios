package com.bca.users_service.infrastructure.output.persistence;

import com.bca.users_service.domain.model.User;
import com.bca.users_service.domain.ports.output.UserRepository;
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
        UserDocument document = new UserDocument(
            user.getId(),
            user.getTypeDocument(),
            user.getNumberDocument(),
            user.getNroPhone(),
            user.getImeiPhone(),
            user.getEmail(),
            user.getStatus()
        );
        return mongoUserRepository.save(document)
                .map(doc -> {
                    log.debug("User saved successfully: {}", doc.getId());
                    return new User(
                        doc.getId(),
                        doc.getTypeDocument(),
                        doc.getNumberDocument(),
                        doc.getNroPhone(),
                        doc.getImeiPhone(),
                        doc.getEmail(),
                        doc.getStatus()
                    );
                })
                .doOnError(error -> log.error("Error saving user: {}", error.getMessage()));
    }

    @Override
    public Mono<User> findById(String id) {
        log.debug("Finding user by ID: {}", id);
        return mongoUserRepository.findById(id)
                .map(doc -> {
                    log.debug("User found: {}", id);
                    return new User(
                        doc.getId(),
                        doc.getTypeDocument(),
                        doc.getNumberDocument(),
                        doc.getNroPhone(),
                        doc.getImeiPhone(),
                        doc.getEmail(),
                        doc.getStatus()
                    );
                })
                .doOnError(error -> log.error("Error finding user by ID {}: {}", id, error.getMessage()));
    }
}