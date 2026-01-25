package com.bca.bootcoin_service.application.usecases;

import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import com.bca.bootcoin_service.domain.ports.output.BootCoinWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateBootCoinWalletUseCaseImplTest {

    @Mock
    BootCoinWalletRepository walletRepository;

    CreateBootCoinWalletUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateBootCoinWalletUseCaseImpl(walletRepository);
    }

    @Test
    void shouldRejectExistingWallet() {
        var existing = new BootCoinWallet("boot-123", "customer", "doc", BigDecimal.ZERO, BootCoinWallet.WalletStatus.ACTIVE, LocalDateTime.now());
        when(walletRepository.findByCustomerId("customer")).thenReturn(Mono.just(existing));

        StepVerifier.create(useCase.createWallet("customer", "doc"))
            .expectErrorMessage("Wallet already exists for customer: customer")
            .verify();

        verify(walletRepository, never()).save(any());
    }

    @Test
    void shouldCreateWalletWhenNoneExists() {
        when(walletRepository.findByCustomerId("customer")).thenReturn(Mono.empty());
        when(walletRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.createWallet("customer", "doc"))
            .assertNext(wallet -> {
                assertTrue(wallet.getWalletId().startsWith("boot-"));
                assertEquals(BigDecimal.ZERO, wallet.getBalanceBTC());
                assertEquals(BootCoinWallet.WalletStatus.ACTIVE, wallet.getStatus());
            })
            .verifyComplete();
    }
}
