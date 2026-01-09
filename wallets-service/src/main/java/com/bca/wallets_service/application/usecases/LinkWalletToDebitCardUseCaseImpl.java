package com.bca.wallets_service.application.usecases;

import com.bca.wallets_service.domain.model.*;
import com.bca.wallets_service.domain.ports.input.LinkWalletToDebitCardUseCase;
import com.bca.wallets_service.domain.ports.output.YankiWalletRepository;

import reactor.core.publisher.Mono;

import com.bca.wallets_service.domain.ports.output.DebitCardRepository;
import com.bca.wallets_service.domain.ports.output.WalletDebitLinkRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LinkWalletToDebitCardUseCaseImpl implements LinkWalletToDebitCardUseCase {

    private final YankiWalletRepository walletRepository;
    private final DebitCardRepository debitCardRepository;
    private final WalletDebitLinkRepository linkRepository;

    public LinkWalletToDebitCardUseCaseImpl(YankiWalletRepository walletRepository, DebitCardRepository debitCardRepository, WalletDebitLinkRepository linkRepository) {
        this.walletRepository = walletRepository;
        this.debitCardRepository = debitCardRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public Mono<WalletDebitLink> link(String walletId, String walletType, String debitCardId) {
        // Validate wallet type
        WalletDebitLink.WalletType type;
        try {
            type = WalletDebitLink.WalletType.valueOf(walletType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Mono.error(new InvalidWalletTypeException("Invalid wallet type"));
        }

        return linkRepository.findByWalletId(walletId)
            .hasElement()
            .flatMap(hasLinked -> {
                if (hasLinked) {
                    return Mono.error(new IllegalArgumentException("Wallet already linked to a debit card"));
                } else {
                    return walletRepository.findById(walletId)
                        .switchIfEmpty(Mono.error(new WalletNotFoundException("Wallet not found")))
                        .zipWith(debitCardRepository.findById(debitCardId)
                            .switchIfEmpty(Mono.error(new DebitCardNotFoundException("Debit card not found"))))
                        .flatMap(tuple -> {
                            YankiWallet wallet = tuple.getT1();
                            DebitCard debitCard = tuple.getT2();

                            WalletDebitLink link = new WalletDebitLink(
                                walletId,
                                type,
                                debitCardId,
                                debitCard.getMainAccountId(),
                                WalletDebitLink.LinkStatus.LINKED,
                                LocalDateTime.now()
                            );

                            return linkRepository.save(link);
                        });
                }
            });
    }
}