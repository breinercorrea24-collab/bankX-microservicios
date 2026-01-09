package com.bca.users_service.application.usecases;

import com.bca.users_service.domain.model.User;
import com.bca.users_service.domain.ports.input.GetUserUseCase;
import com.bca.users_service.domain.ports.output.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class GetUserUseCaseImpl implements GetUserUseCase {

    private final UserRepository userRepository;

    public GetUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<User> getUserById(String id) {
        log.info("Retrieving user with ID: {}", id);
        return userRepository.findById(id)
                .doOnNext(user -> {
                    if (user != null) {
                        log.info("User found with ID: {}", id);
                    } else {
                        log.warn("User not found with ID: {}", id);
                    }
                })
                .doOnError(error -> log.error("Error retrieving user with ID {}: {}", id, error.getMessage()));
    }
}