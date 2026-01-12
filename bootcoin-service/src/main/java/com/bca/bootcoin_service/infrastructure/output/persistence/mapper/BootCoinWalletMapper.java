package com.bca.bootcoin_service.infrastructure.output.persistence.mapper;

import org.springframework.stereotype.Component;

import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import com.bca.bootcoin_service.infrastructure.output.persistence.entity.BootCoinWalletDocument;

@Component
public class BootCoinWalletMapper {

    public BootCoinWallet toDomain(BootCoinWalletDocument document) {
        BootCoinWallet wallet = new BootCoinWallet();
        wallet.setWalletId(document.getWalletId());
        wallet.setCustomerId(document.getCustomerId());
        wallet.setDocument(document.getDocument());
        wallet.setBalanceBTC(document.getBalanceBTC());
        wallet.setStatus(BootCoinWallet.WalletStatus.valueOf(document.getStatus()));
        wallet.setCreatedAt(document.getCreatedAt());
        return wallet;
    }

    public BootCoinWalletDocument toDocument(BootCoinWallet wallet) {
        return new BootCoinWalletDocument(
            wallet.getWalletId(),
            wallet.getCustomerId(),
            wallet.getDocument(),
            wallet.getBalanceBTC(),
            wallet.getStatus().name(),
            wallet.getCreatedAt()
        );
    }
}
