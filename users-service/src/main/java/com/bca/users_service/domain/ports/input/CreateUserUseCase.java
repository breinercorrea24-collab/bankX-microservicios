package com.bca.users_service.domain.ports.input;

import com.bca.users_service.domain.model.User;
import reactor.core.publisher.Mono;

public interface CreateUserUseCase {
    Mono<User> createUser(String typeDocument, String numberDocument, String nroPhone, String imeiPhone, String email);
}