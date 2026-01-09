package com.bca.bootcoin_service.domain.ports.output;

import com.bca.bootcoin_service.domain.model.BootCoinWallet;

import reactor.core.publisher.Mono;

public interface BootCoinWalletRepository {
    Mono<BootCoinWallet> save(BootCoinWallet wallet);
    Mono<BootCoinWallet> findById(String walletId);
    Mono<BootCoinWallet> findByCustomerId(String customerId);
    Mono<BootCoinWallet> updateBalance(String walletId, java.math.BigDecimal newBalance);
}