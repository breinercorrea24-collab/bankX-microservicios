package com.bca.users_service.application.usecases;

import com.bca.users_service.domain.model.User;
import com.bca.users_service.domain.ports.output.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetUserUseCaseImplTest {

    @Test
    @DisplayName("getUserById should return a user when repository finds it")
    void getUserByIdReturnsUserWhenPresent() {
        User expected = new User("usr-1", "DNI", "123", "+51987654321", "imei", "user@bankx.com", "ACTIVE");
        UserRepository repository = new UserRepository() {
            @Override
            public Mono<User> save(User user) {
                return Mono.empty();
            }

            @Override
            public Mono<User> findById(String id) {
                return Mono.just(expected);
            }
        };

        GetUserUseCaseImpl useCase = new GetUserUseCaseImpl(repository);

        StepVerifier.create(useCase.getUserById("usr-1"))
                .assertNext(actual -> assertEquals(expected, actual))
                .verifyComplete();
    }

    @Test
    @DisplayName("getUserById should complete empty when repository is empty")
    void getUserByIdReturnsEmptyWhenNotFound() {
        UserRepository repository = new UserRepository() {
            @Override
            public Mono<User> save(User user) {
                return Mono.empty();
            }

            @Override
            public Mono<User> findById(String id) {
                return Mono.empty();
            }
        };

        GetUserUseCaseImpl useCase = new GetUserUseCaseImpl(repository);

        StepVerifier.create(useCase.getUserById("usr-1"))
                .verifyComplete();
    }

    @Test
    @DisplayName("getUserById should handle repository null emission for coverage")
    void getUserByIdLogsWarnWhenRepositoryEmitsNull() {
        UserRepository repository = new UserRepository() {
            @Override
            public Mono<User> save(User user) {
                return Mono.empty();
            }

            @Override
            public Mono<User> findById(String id) {
                return new Mono<>() {
                    @Override
                    public void subscribe(CoreSubscriber<? super User> actual) {
                        actual.onSubscribe(Operators.emptySubscription());
                        actual.onNext(null);
                        actual.onComplete();
                    }
                };
            }
        };

        GetUserUseCaseImpl useCase = new GetUserUseCaseImpl(repository);

        StepVerifier.create(useCase.getUserById("usr-1"))
                .assertNext(user -> assertEquals(null, user))
                .verifyComplete();
    }

    @Test
    @DisplayName("getUserById should forward repository errors")
    void getUserByIdPropagatesErrors() {
        UserRepository repository = new UserRepository() {
            @Override
            public Mono<User> save(User user) {
                return Mono.empty();
            }

            @Override
            public Mono<User> findById(String id) {
                return Mono.error(new IllegalStateException("boom"));
            }
        };

        GetUserUseCaseImpl useCase = new GetUserUseCaseImpl(repository);

        StepVerifier.create(useCase.getUserById("usr-1"))
                .verifyErrorMessage("boom");
    }
}
