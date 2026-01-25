package com.bca.bootcoin_service.infrastructure.output.persistence;

import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import com.bca.bootcoin_service.infrastructure.output.persistence.entity.BootCoinWalletDocument;
import com.bca.bootcoin_service.infrastructure.output.persistence.mapper.BootCoinWalletMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BootCoinWalletMapperTest {

    private final BootCoinWalletMapper mapper = new BootCoinWalletMapper();

    @Test
    void shouldMapDocumentToDomain() {
        var document = new BootCoinWalletDocument(
            "wallet-1",
            "customer-1",
            "doc",
            new BigDecimal("5"),
            BootCoinWallet.WalletStatus.ACTIVE.name(),
            LocalDateTime.now()
        );

        var domain = mapper.toDomain(document);

        assertEquals(document.getWalletId(), domain.getWalletId());
        assertEquals(BootCoinWallet.WalletStatus.ACTIVE, domain.getStatus());
    }

    @Test
    void shouldMapDomainToDocument() {
        var domain = new BootCoinWallet(
            "wallet-2",
            "customer-2",
            "doc",
            new BigDecimal("7"),
            BootCoinWallet.WalletStatus.INACTIVE,
            LocalDateTime.now()
        );

        var document = mapper.toDocument(domain);

        assertEquals(domain.getCustomerId(), document.getCustomerId());
        assertEquals(BootCoinWallet.WalletStatus.INACTIVE.name(), document.getStatus());
    }
}
