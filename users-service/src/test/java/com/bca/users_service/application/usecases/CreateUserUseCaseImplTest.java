package com.bca.users_service.application.usecases;

import com.bca.users_service.application.usecases.CreateUserUseCaseImpl;
import com.bca.users_service.domain.model.User;
import com.bca.users_service.domain.ports.output.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateUserUseCaseImplTest {

    @Test
    @DisplayName("createUser should persist generated user and return it")
    void createUserPersistsAndReturnsUser() {
        AtomicReference<User> savedUser = new AtomicReference<>();
        UserRepository repository = new UserRepository() {
            @Override
            public Mono<User> save(User user) {
                savedUser.set(user);
                return Mono.just(user);
            }

            @Override
            public Mono<User> findById(String id) {
                return Mono.empty();
            }
        };

        CreateUserUseCaseImpl useCase = new CreateUserUseCaseImpl(repository);

        StepVerifier.create(useCase.createUser("DNI", "12345678", "+51987654321", "imei", "user@bankx.com"))
                .assertNext(created -> {
                    assertNotNull(created.getId());
                    assertEquals("DNI", created.getTypeDocument());
                    assertEquals("12345678", created.getNumberDocument());
                    assertEquals("+51987654321", created.getNroPhone());
                    assertEquals("imei", created.getImeiPhone());
                    assertEquals("user@bankx.com", created.getEmail());
                    assertEquals("ACTIVE", created.getStatus());
                    assertEquals("usr-", created.getId().substring(0, 4));
                })
                .verifyComplete();

        User recorded = savedUser.get();
        assertNotNull(recorded);
        assertEquals("DNI", recorded.getTypeDocument());
        assertEquals("12345678", recorded.getNumberDocument());
        assertEquals("+51987654321", recorded.getNroPhone());
        assertEquals("imei", recorded.getImeiPhone());
        assertEquals("user@bankx.com", recorded.getEmail());
        assertEquals("ACTIVE", recorded.getStatus());
    }

    @Test
    @DisplayName("createUser should forward repository errors")
    void createUserFailsWhenRepositoryErrors() {
        UserRepository repository = new UserRepository() {
            @Override
            public Mono<User> save(User user) {
                return Mono.error(new IllegalStateException("boom"));
            }

            @Override
            public Mono<User> findById(String id) {
                return Mono.empty();
            }
        };

        CreateUserUseCaseImpl useCase = new CreateUserUseCaseImpl(repository);

        StepVerifier.create(useCase.createUser("DNI", "12345678", "+51987654321", "imei", "user@bankx.com"))
                .verifyErrorMessage("boom");
    }
}
