package com.bca.users_service.infrastructure.input.rest;

import com.bca.users_service.api.UsersApiDelegate;
import com.bca.users_service.domain.ports.input.CreateUserUseCase;
import com.bca.users_service.domain.ports.input.GetUserUseCase;
import com.bca.users_service.dto.UserCreate;
import com.bca.users_service.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UsersApiDelegateImpl implements UsersApiDelegate {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;

    public UsersApiDelegateImpl(CreateUserUseCase createUserUseCase, GetUserUseCase getUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
    }

    @Override
    public Mono<ResponseEntity<UserResponse>> usersPost(Mono<UserCreate> userCreate, ServerWebExchange exchange) {
        log.info("Received request to create user");
        return userCreate.flatMap(create -> {
            log.debug("Creating user with email: {} and phone: {}", create.getEmail(), create.getNroPhone());
            // Validar datos requeridos
            if (create.getTypeDocument() == null || create.getTypeDocument().toString().isEmpty() ||
                create.getNumberDocument() == null || create.getNumberDocument().isEmpty() ||
                create.getNroPhone() == null || create.getNroPhone().isEmpty() ||
                create.getImeiPhone() == null || create.getImeiPhone().isEmpty() ||
                create.getEmail() == null || create.getEmail().isEmpty()) {
                log.warn("Invalid user data: some required fields are empty");
                return Mono.just(ResponseEntity.badRequest().build());
            }

            return createUserUseCase.createUser(
                    create.getTypeDocument().toString(),
                    create.getNumberDocument(),
                    create.getNroPhone(),
                    create.getImeiPhone(),
                    create.getEmail()
                )
                    .map(user -> {
                        UserResponse response = new UserResponse()
                                .id(user.getId())
                                .typeDocument(user.getTypeDocument())
                                .numberDocument(user.getNumberDocument())
                                .nroPhone(user.getNroPhone())
                                .imeiPhone(user.getImeiPhone())
                                .email(user.getEmail())
                                .status(user.getStatus());
                        log.info("User created successfully with ID: {}", user.getId());
                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
                    })
                    .doOnError(error -> log.error("Error creating user: {}", error.getMessage()));
        });
    }

    @Override
    public Mono<ResponseEntity<UserResponse>> usersUserIdGet(String userId, ServerWebExchange exchange) {
        log.info("Received request to get user with ID: {}", userId);
        return getUserUseCase.getUserById(userId)
                .map(user -> {
                    UserResponse response = new UserResponse()
                            .id(user.getId())
                            .typeDocument(user.getTypeDocument())
                            .numberDocument(user.getNumberDocument())
                            .nroPhone(user.getNroPhone())
                            .imeiPhone(user.getImeiPhone())
                            .email(user.getEmail())
                            .status(user.getStatus());
                    log.debug("User retrieved successfully: {}", userId);
                    return ResponseEntity.ok(response);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(error -> log.error("Error retrieving user {}: {}", userId, error.getMessage()));
    }
}