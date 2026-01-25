package com.bca.users_service.infrastructure.input.rest;

import com.bca.users_service.domain.model.User;
import com.bca.users_service.domain.ports.input.CreateUserUseCase;
import com.bca.users_service.domain.ports.input.GetUserUseCase;
import com.bca.users_service.dto.UserCreate;
import com.bca.users_service.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UsersApiDelegateImplTest {

    @Test
    @DisplayName("usersPost should return 201 when use case succeeds with valid payload")
    void usersPostReturnsCreatedWhenValidPayload() {
        UserCreate payload = validUserCreate();
        User created = new User("usr-123", payload.getTypeDocument().toString(), payload.getNumberDocument(),
                payload.getNroPhone(), payload.getImeiPhone(), payload.getEmail(), "ACTIVE");

        CreateUserUseCase createUserUseCase = (typeDocument, numberDocument, nroPhone, imeiPhone, email) ->
                buildMonoUser(created, typeDocument, numberDocument, nroPhone, imeiPhone, email);
        GetUserUseCase getUserUseCase = id -> Mono.empty();
        UsersApiDelegateImpl delegate = new UsersApiDelegateImpl(createUserUseCase, getUserUseCase);

        ServerWebExchange exchange = postExchange();

        StepVerifier.create(delegate.usersPost(Mono.just(payload), exchange))
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    UserResponse body = response.getBody();
                    assertEquals(created.getId(), body.getId());
                    assertEquals(created.getEmail(), body.getEmail());
                    assertEquals(created.getStatus(), body.getStatus());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("usersPost should short-circuit with 400 when payload is missing required fields")
    void usersPostReturnsBadRequestWhenValidationFails() {
        UserCreate invalidPayload = new UserCreate()
                .numberDocument("123")
                .nroPhone("+51987654321")
                .imeiPhone("356938035643809");
        assertValidationRejected(invalidPayload);
    }

    @Test
    @DisplayName("usersPost should reject when numberDocument null")
    void usersPostRejectsWhenNumberDocumentNull() {
        UserCreate payload = validUserCreate();
        payload.setNumberDocument(null);
        assertValidationRejected(payload);
    }

    @Test
    @DisplayName("usersPost should reject when numberDocument empty")
    void usersPostRejectsWhenNumberDocumentEmpty() {
        UserCreate payload = validUserCreate();
        payload.setNumberDocument("");
        assertValidationRejected(payload);
    }

    @Test
    @DisplayName("usersPost should reject when nroPhone null")
    void usersPostRejectsWhenNroPhoneNull() {
        UserCreate payload = validUserCreate();
        payload.setNroPhone(null);
        assertValidationRejected(payload);
    }

    @Test
    @DisplayName("usersPost should reject when nroPhone empty")
    void usersPostRejectsWhenNroPhoneEmpty() {
        UserCreate payload = validUserCreate();
        payload.setNroPhone("");
        assertValidationRejected(payload);
    }

    @Test
    @DisplayName("usersPost should reject when imeiPhone null")
    void usersPostRejectsWhenImeiPhoneNull() {
        UserCreate payload = validUserCreate();
        payload.setImeiPhone(null);
        assertValidationRejected(payload);
    }

    @Test
    @DisplayName("usersPost should reject when imeiPhone empty")
    void usersPostRejectsWhenImeiPhoneEmpty() {
        UserCreate payload = validUserCreate();
        payload.setImeiPhone("");
        assertValidationRejected(payload);
    }

    @Test
    @DisplayName("usersPost should reject when email null")
    void usersPostRejectsWhenEmailNull() {
        UserCreate payload = validUserCreate();
        payload.setEmail(null);
        assertValidationRejected(payload);
    }

    @Test
    @DisplayName("usersPost should reject when email empty")
    void usersPostRejectsWhenEmailEmpty() {
        UserCreate payload = validUserCreate();
        payload.setEmail("");
        assertValidationRejected(payload);
    }

    @Test
    @DisplayName("usersPost should propagate errors from the create use case")
    void usersPostPropagatesUseCaseErrors() {
        UserCreate payload = validUserCreate();
        CreateUserUseCase createUserUseCase = (typeDocument, numberDocument, nroPhone, imeiPhone, email) ->
                Mono.error(new IllegalStateException("boom"));
        GetUserUseCase getUserUseCase = id -> Mono.empty();
        UsersApiDelegateImpl delegate = new UsersApiDelegateImpl(createUserUseCase, getUserUseCase);

        ServerWebExchange exchange = postExchange();

        StepVerifier.create(delegate.usersPost(Mono.just(payload), exchange))
                .verifyErrorSatisfies(error -> assertEquals("boom", error.getMessage()));
    }

    @Test
    @DisplayName("usersUserIdGet should return 200 with user payload when found")
    void usersUserIdGetReturnsOkWhenFound() {
        String userId = "usr-123";
        User user = new User(userId, "DNI", "12345678", "+51987654321", "imei", "usuario@bankx.com", "ACTIVE");
        CreateUserUseCase createUserUseCase = (typeDocument, numberDocument, nroPhone, imeiPhone, email) -> Mono.empty();
        GetUserUseCase getUserUseCase = id -> Mono.just(user);
        UsersApiDelegateImpl delegate = new UsersApiDelegateImpl(createUserUseCase, getUserUseCase);

        ServerWebExchange exchange = getExchange(userId);

        StepVerifier.create(delegate.usersUserIdGet(userId, exchange))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    UserResponse body = response.getBody();
                    assertEquals(user.getId(), body.getId());
                    assertEquals(user.getEmail(), body.getEmail());
                    assertEquals(user.getStatus(), body.getStatus());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("usersUserIdGet should return 404 when user is missing")
    void usersUserIdGetReturnsNotFoundWhenMissing() {
        String userId = "usr-123";
        CreateUserUseCase createUserUseCase = (typeDocument, numberDocument, nroPhone, imeiPhone, email) -> Mono.empty();
        GetUserUseCase getUserUseCase = id -> Mono.empty();
        UsersApiDelegateImpl delegate = new UsersApiDelegateImpl(createUserUseCase, getUserUseCase);

        ServerWebExchange exchange = getExchange(userId);

        StepVerifier.create(delegate.usersUserIdGet(userId, exchange))
                .assertNext(response -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()))
                .verifyComplete();

    }

    @Test
    @DisplayName("usersUserIdGet should propagate errors from get use case")
    void usersUserIdGetPropagatesUseCaseErrors() {
        String userId = "usr-123";
        CreateUserUseCase createUserUseCase = (typeDocument, numberDocument, nroPhone, imeiPhone, email) -> Mono.empty();
        GetUserUseCase getUserUseCase = id -> Mono.error(new IllegalArgumentException("boom"));
        UsersApiDelegateImpl delegate = new UsersApiDelegateImpl(createUserUseCase, getUserUseCase);

        ServerWebExchange exchange = getExchange(userId);

        StepVerifier.create(delegate.usersUserIdGet(userId, exchange))
                .verifyErrorSatisfies(error -> assertEquals("boom", error.getMessage()));
    }

    private ServerWebExchange postExchange() {
        return MockServerWebExchange.from(MockServerHttpRequest.post("/users"));
    }

    private ServerWebExchange getExchange(String userId) {
        return MockServerWebExchange.from(MockServerHttpRequest.get("/users/" + userId));
    }

    private UserCreate validUserCreate() {
        return new UserCreate()
                .typeDocument(UserCreate.TypeDocumentEnum.DNI)
                .numberDocument("12345678")
                .nroPhone("+51987654321")
                .imeiPhone("356938035643809")
                .email("usuario@bankx.com");
    }

    private Mono<User> buildMonoUser(User user, String typeDocument, String numberDocument,
                                     String nroPhone, String imeiPhone, String email) {
        assertEquals(typeDocument, user.getTypeDocument());
        assertEquals(numberDocument, user.getNumberDocument());
        assertEquals(nroPhone, user.getNroPhone());
        assertEquals(imeiPhone, user.getImeiPhone());
        assertEquals(email, user.getEmail());
        return Mono.just(user);
    }

    private void assertValidationRejected(UserCreate payload) {
        AtomicBoolean createCalled = new AtomicBoolean(false);
        UsersApiDelegateImpl delegate = delegateWithSpy(createCalled);

        StepVerifier.create(delegate.usersPost(Mono.just(payload), postExchange()))
                .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()))
                .verifyComplete();

        assertFalse(createCalled.get(), "Create user use case should not execute when validation fails");
    }

    private UsersApiDelegateImpl delegateWithSpy(AtomicBoolean called) {
        CreateUserUseCase createUserUseCase = (typeDocument, numberDocument, nroPhone, imeiPhone, email) -> {
            called.set(true);
            return Mono.just(new User("usr-xxx", typeDocument, numberDocument, nroPhone, imeiPhone, email, "ACTIVE"));
        };
        GetUserUseCase getUserUseCase = id -> Mono.empty();
        return new UsersApiDelegateImpl(createUserUseCase, getUserUseCase);
    }
}
