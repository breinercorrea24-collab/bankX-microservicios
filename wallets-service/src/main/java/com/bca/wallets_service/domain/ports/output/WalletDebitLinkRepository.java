package com.bca.wallets_service.domain.ports.output;

import com.bca.wallets_service.domain.model.WalletDebitLink;

import reactor.core.publisher.Mono;

public interface WalletDebitLinkRepository {
    Mono<WalletDebitLink> save(WalletDebitLink link);
    Mono<WalletDebitLink> findByWalletId(String walletId);
}