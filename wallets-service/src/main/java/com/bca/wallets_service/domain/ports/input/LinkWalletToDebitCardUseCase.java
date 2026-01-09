package com.bca.wallets_service.domain.ports.input;

import com.bca.wallets_service.domain.model.WalletDebitLink;
import reactor.core.publisher.Mono;

public interface LinkWalletToDebitCardUseCase {
    Mono<WalletDebitLink> link(String walletId, String walletType, String debitCardId);
}