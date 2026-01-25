package com.bca.bootcoin_service.infrastructure.input.rest.mapper;

import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import com.bca.bootcoin_service.dto.BootCoinWalletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BootCoinWalletApiMapperTest {

    private BootCoinWalletApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BootCoinWalletApiMapper();
    }

    @Test
    void shouldMapWalletToResponseWithAllFields() {
        var wallet = new BootCoinWallet("wallet-1", "customer", "doc", new BigDecimal("2.5"),
            BootCoinWallet.WalletStatus.ACTIVE, LocalDateTime.now());

        BootCoinWalletResponse response = mapper.toResponse(wallet);

        assertEquals(wallet.getWalletId(), response.getWalletId());
        assertEquals(wallet.getCustomerId(), response.getCustomerId());
        assertEquals(wallet.getDocument(), response.getDocument());
        assertEquals(wallet.getBalanceBTC().floatValue(), response.getBalanceBTC());
        assertEquals(wallet.getStatus().name(), response.getStatus());
        assertEquals(wallet.getCreatedAt().atOffset(response.getCreatedAt().getOffset()), response.getCreatedAt());
    }

    @Test
    void shouldHandleNullFieldsOnResponse() {
        var wallet = new BootCoinWallet();
        wallet.setWalletId("wallet-0");

        BootCoinWalletResponse response = mapper.toResponse(wallet);

        assertEquals("wallet-0", response.getWalletId());
        assertNull(response.getBalanceBTC());
        assertNull(response.getStatus());
        assertNull(response.getCreatedAt());
    }
}
