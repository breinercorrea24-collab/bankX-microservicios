package com.bca.wallets_service.domain.ports.output;

import com.bca.wallets_service.domain.model.YankiWallet;

import reactor.core.publisher.Mono;


public interface YankiWalletRepository {
    Mono<YankiWallet>  save(YankiWallet wallet);
    Mono<YankiWallet> findById(String walletId);
    Mono<YankiWallet> findByPhone(String phone);
    Mono<YankiWallet> findByCustomerId(String customerId);
}