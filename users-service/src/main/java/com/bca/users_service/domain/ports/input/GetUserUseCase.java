package com.bca.users_service.domain.ports.input;

import com.bca.users_service.domain.model.User;
import reactor.core.publisher.Mono;

public interface GetUserUseCase {
    Mono<User> getUserById(String id);
}