package com.bca.users_service.infrastructure.output.persistence;

import com.bca.users_service.domain.model.User;
import com.bca.users_service.infrastructure.output.persistence.entity.UserDocument;
import com.bca.users_service.infrastructure.output.persistence.repository.UserMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRepositoryAdapterTest {

    @Test
    @DisplayName("save should convert domain to document, persist and return converted domain")
    void saveConvertsAndPersists() {
        UserDocument[] captured = new UserDocument[1];
        UserMongoRepository repository = createProxy(
                document -> {
                    captured[0] = document;
                    return Mono.just(document);
                },
                id -> Mono.empty()
        );

        UserRepositoryAdapter adapter = new UserRepositoryAdapter(repository);
        User domain = new User("usr-1", "DNI", "12345678", "+51987654321", "imei", "user@bankx.com", "ACTIVE");

        StepVerifier.create(adapter.save(domain))
                .assertNext(saved -> assertEquals(domain.getId(), saved.getId()))
                .verifyComplete();

        assertEquals("user@bankx.com", captured[0].getEmail());
    }

    @Test
    @DisplayName("save should propagate repository errors")
    void savePropagatesErrors() {
        UserMongoRepository repository = createProxy(
                document -> Mono.error(new IllegalStateException("boom")),
                id -> Mono.empty()
        );

        UserRepositoryAdapter adapter = new UserRepositoryAdapter(repository);

        StepVerifier.create(adapter.save(new User()))
                .verifyErrorMessage("boom");
    }

    @Test
    @DisplayName("findById should convert document to domain")
    void findByIdConvertsResult() {
        UserDocument document = new UserDocument("usr-2", "CEX", "X123", "+51900000000", "imei2", "other@bankx.com", "ACTIVE");
        UserMongoRepository repository = createProxy(
                doc -> Mono.just(doc),
                id -> Mono.just(document)
        );

        UserRepositoryAdapter adapter = new UserRepositoryAdapter(repository);

        StepVerifier.create(adapter.findById("usr-2"))
                .assertNext(user -> assertEquals(document.getId(), user.getId()))
                .verifyComplete();
    }

    @Test
    @DisplayName("findById should propagate repository errors")
    void findByIdPropagatesErrors() {
        UserMongoRepository repository = createProxy(
                doc -> Mono.empty(),
                id -> Mono.error(new IllegalStateException("boom"))
        );

        UserRepositoryAdapter adapter = new UserRepositoryAdapter(repository);

        StepVerifier.create(adapter.findById("usr-3"))
                .verifyErrorMessage("boom");
    }

    private UserMongoRepository createProxy(Function<UserDocument, Mono<UserDocument>> saveBehavior,
                                            Function<String, Mono<UserDocument>> findBehavior) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                if ("save".equals(method.getName())) {
                    return saveBehavior.apply((UserDocument) args[0]);
                }
                if ("findById".equals(method.getName())) {
                    return findBehavior.apply((String) args[0]);
                }
                return Mono.empty();
            }
        };
        return (UserMongoRepository) Proxy.newProxyInstance(
                UserMongoRepository.class.getClassLoader(),
                new Class[]{UserMongoRepository.class},
                handler
        );
    }
}
