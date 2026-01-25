package com.bca.bootcoin_service.infrastructure.input.rest;

import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import com.bca.bootcoin_service.domain.ports.input.CreateBootCoinWalletUseCase;
import com.bca.bootcoin_service.dto.BootCoinWalletRequest;
import com.bca.bootcoin_service.infrastructure.input.rest.mapper.BootCoinWalletApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootCoinWalletApiDelegateImplTest {

    @Mock
    CreateBootCoinWalletUseCase useCase;

    @Mock
    ServerWebExchange exchange;

    private BootCoinWalletApiDelegateImpl delegate;

    @BeforeEach
    void setUp() {
        delegate = new BootCoinWalletApiDelegateImpl(useCase, new BootCoinWalletApiMapper());
    }

    @Test
    void shouldReturnCreatedWhenWalletCreated() {
        var request = new BootCoinWalletRequest();
        request.setCustomerId("customer");
        request.setDocument("doc");

        var wallet = new BootCoinWallet("wallet-1", "customer", "doc",
            new BigDecimal("5"), BootCoinWallet.WalletStatus.ACTIVE, LocalDateTime.now());

        when(useCase.createWallet(eq("customer"), eq("doc"))).thenReturn(Mono.just(wallet));

        StepVerifier.create(delegate.walletsBootcoinPost(Mono.just(request), exchange))
            .assertNext(response -> {
                ResponseEntity<?> entity = response;
                assert entity.getStatusCodeValue() == 201;
                assert entity.getBody() != null;
            })
            .verifyComplete();
    }

    @Test
    void shouldReturnBadRequestWhenUseCaseThrows() {
        var request = new BootCoinWalletRequest();
        request.setCustomerId("customer");
        request.setDocument("doc");

        when(useCase.createWallet(eq("customer"), eq("doc")))
            .thenReturn(Mono.error(new IllegalArgumentException("boom")));

        StepVerifier.create(delegate.walletsBootcoinPost(Mono.just(request), exchange))
            .expectNextMatches(response -> response.getStatusCode().is4xxClientError())
            .verifyComplete();
    }
}
