package com.bca.wallets_service.domain.ports.input;

import com.bca.wallets_service.domain.model.YankiWallet;
import reactor.core.publisher.Mono;

public interface CreateYankiWalletUseCase {
    Mono<YankiWallet> createYankiWallet(String customerId, String phone);
}