package com.bca.users_service.application.usecases;

import com.bca.users_service.domain.model.User;
import com.bca.users_service.domain.ports.input.CreateUserUseCase;
import com.bca.users_service.domain.ports.output.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;

    public CreateUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<User> createUser(String typeDocument, String numberDocument, String nroPhone, String imeiPhone, String email) {
        log.info("Creating user with email: {} and phone: {}", email, nroPhone);
        // Generar ID único
        String id = "usr-" + UUID.randomUUID().toString().substring(0, 8);
        User user = new User(id, typeDocument, numberDocument, nroPhone, imeiPhone, email, "ACTIVE");
        log.debug("Generated user ID: {}", id);
        return userRepository.save(user)
                .doOnNext(savedUser -> log.info("User created successfully with ID: {}", savedUser.getId()))
                .doOnError(error -> log.error("Error creating user: {}", error.getMessage()));
    }
}