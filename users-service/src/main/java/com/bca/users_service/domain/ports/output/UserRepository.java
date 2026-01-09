package com.bca.users_service.domain.ports.output;

import com.bca.users_service.domain.model.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);
    Mono<User> findById(String id);
}