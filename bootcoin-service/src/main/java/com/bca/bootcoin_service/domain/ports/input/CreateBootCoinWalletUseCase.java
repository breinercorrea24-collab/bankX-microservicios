package com.bca.bootcoin_service.domain.ports.input;

import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import reactor.core.publisher.Mono;

public interface CreateBootCoinWalletUseCase {
    Mono<BootCoinWallet> createWallet(String customerId, String document);
}