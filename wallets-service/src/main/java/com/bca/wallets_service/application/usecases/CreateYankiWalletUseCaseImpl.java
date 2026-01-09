package com.bca.wallets_service.application.usecases;

import com.bca.wallets_service.domain.model.YankiWallet;
import com.bca.wallets_service.domain.ports.input.CreateYankiWalletUseCase;
import com.bca.wallets_service.domain.ports.output.YankiWalletRepository;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreateYankiWalletUseCaseImpl implements CreateYankiWalletUseCase {

    private final YankiWalletRepository repository;

    public CreateYankiWalletUseCaseImpl(YankiWalletRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<YankiWallet> createYankiWallet(String customerId, String phone) {
        return repository.findByPhone(phone)
            .hasElement()
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new IllegalArgumentException("Wallet already exists for this phone"));
                } else {
                    String walletId = "yanki-" + UUID.randomUUID().toString().substring(0, 8);
                    YankiWallet wallet = new YankiWallet(
                        walletId,
                        customerId,
                        phone,
                        BigDecimal.ZERO,
                        "PEN",
                        YankiWallet.WalletStatus.ACTIVE,
                        LocalDateTime.now()
                    );
                    return repository.save(wallet);
                }
            });
    }
}