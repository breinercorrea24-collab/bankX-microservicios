package com.bca.bootcoin_service.infrastructure.output.persistence;

import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import com.bca.bootcoin_service.infrastructure.output.persistence.entity.BootCoinWalletDocument;
import com.bca.bootcoin_service.infrastructure.output.persistence.mapper.BootCoinWalletMapper;
import com.bca.bootcoin_service.infrastructure.output.persistence.repository.BootCoinWalletMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootCoinWalletRepositoryAdapterTest {

    @Mock
    BootCoinWalletMongoRepository mongoRepository;

    BootCoinWalletMapper mapper = new BootCoinWalletMapper();

    private BootCoinWalletRepositoryAdapter adapter() {
        return new BootCoinWalletRepositoryAdapter(mongoRepository, mapper);
    }

    @Test
    void shouldSaveWalletSuccessfully() {
        var wallet = buildWallet("wallet-1");
        var document = mapper.toDocument(wallet);
        when(mongoRepository.save(any())).thenReturn(Mono.just(document));

        StepVerifier.create(adapter().save(wallet))
            .assertNext(saved -> assertEquals(wallet.getWalletId(), saved.getWalletId()))
            .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenMapperDropsToNull() {
        var wallet = buildWallet("wallet-2");
        var mockMapper = mock(BootCoinWalletMapper.class);
        when(mockMapper.toDocument(any())).thenReturn(null);
        var adapter = new BootCoinWalletRepositoryAdapter(mongoRepository, mockMapper);

        StepVerifier.create(adapter.save(wallet))
            .verifyComplete();
    }

    @Test
    void shouldPropagateErrorWhenSaveFails() {
        var wallet = buildWallet("wallet-3");
        var document = mapper.toDocument(wallet);
        when(mongoRepository.save(any())).thenReturn(Mono.error(new RuntimeException("boom")));

        StepVerifier.create(adapter().save(wallet))
            .expectErrorMessage("boom")
            .verify();
    }

    @Test
    void shouldFindByIdAndReturnDomain() {
        var document = buildDocument("wallet-1");
        when(mongoRepository.findByWalletId("wallet-1")).thenReturn(Mono.just(document));

        StepVerifier.create(adapter().findById("wallet-1"))
            .assertNext(wallet -> assertEquals(document.getWalletId(), wallet.getWalletId()))
            .verifyComplete();
    }

    @Test
    void shouldFindByCustomerIdAndReturnDomain() {
        var document = buildDocument("wallet-2");
        when(mongoRepository.findByCustomerId("customer-2")).thenReturn(Mono.just(document));

        StepVerifier.create(adapter().findByCustomerId("customer-2"))
            .assertNext(wallet -> assertEquals(document.getCustomerId(), wallet.getCustomerId()))
            .verifyComplete();
    }

    @Test
    void shouldUpdateBalanceSuccessfully() {
        var walletId = "wallet-update";
        var document = buildDocument(walletId);
        when(mongoRepository.findByWalletId(walletId)).thenReturn(Mono.just(document));
        when(mongoRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(adapter().updateBalance(walletId, new BigDecimal("9")))
            .assertNext(updated -> assertEquals(new BigDecimal("9"), updated.getBalanceBTC()))
            .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenUpdatingMissingWallet() {
        when(mongoRepository.findByWalletId("missing")).thenReturn(Mono.empty());

        StepVerifier.create(adapter().updateBalance("missing", BigDecimal.ZERO))
            .verifyComplete();
    }

    @Test
    void shouldPropagateErrorWhenUpdateBalanceFails() {
        var walletId = "wallet-null";
        var document = buildDocument(walletId);
        when(mongoRepository.findByWalletId(walletId)).thenReturn(Mono.just(document));
        when(mongoRepository.save(any())).thenReturn(Mono.error(new RuntimeException("boom")));

        StepVerifier.create(adapter().updateBalance(walletId, new BigDecimal("7")))
            .expectErrorMessage("boom")
            .verify();
    }

    private static BootCoinWallet buildWallet(String walletId) {
        return new BootCoinWallet(walletId, "customer", "doc", BigDecimal.ZERO, BootCoinWallet.WalletStatus.ACTIVE, LocalDateTime.now());
    }

    private static BootCoinWalletDocument buildDocument(String walletId) {
        var wallet = buildWallet(walletId);
        var mapper = new BootCoinWalletMapper();
        return mapper.toDocument(wallet);
    }
}
