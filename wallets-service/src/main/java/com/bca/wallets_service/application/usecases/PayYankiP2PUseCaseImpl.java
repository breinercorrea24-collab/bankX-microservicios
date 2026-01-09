package com.bca.wallets_service.application.usecases;

import com.bca.wallets_service.domain.model.*;
import com.bca.wallets_service.domain.ports.input.PayYankiP2PUseCase;
import com.bca.wallets_service.domain.ports.output.YankiWalletRepository;
import com.bca.wallets_service.domain.ports.output.TransactionRepository;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PayYankiP2PUseCaseImpl implements PayYankiP2PUseCase {

    private final YankiWalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public PayYankiP2PUseCaseImpl(YankiWalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Mono<Transaction> pay(String fromId, String toId, BigDecimal amount) {
        return Mono.zip(walletRepository.findById(fromId), walletRepository.findById(toId))
            .flatMap(tuple -> {
                YankiWallet fromWallet = tuple.getT1();
                YankiWallet toWallet = tuple.getT2();

                if (fromWallet == null) {
                    return Mono.error(new WalletNotFoundException("From wallet not found"));
                }
                if (toWallet == null) {
                    return Mono.error(new WalletNotFoundException("To wallet not found"));
                }

                if (fromWallet.getBalance().compareTo(amount) < 0) {
                    return Mono.error(new InsufficientBalanceException("Insufficient balance"));
                }

                // Update balances
                fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
                toWallet.setBalance(toWallet.getBalance().add(amount));

                // Save updated wallets
                return Mono.zip(walletRepository.save(fromWallet), walletRepository.save(toWallet))
                    .map(savedTuple -> {
                        YankiWallet savedFrom = savedTuple.getT1();
                        // Create transaction
                        String transactionId = "yanki-tx-" + UUID.randomUUID().toString().substring(0, 8);
                        Transaction transaction = new Transaction(
                            transactionId,
                            fromId,
                            toId,
                            amount,
                            savedFrom.getCurrency(),
                            savedFrom.getBalance(),
                            Transaction.TransactionStatus.SUCCESS,
                            LocalDateTime.now()
                        );
                        return transactionRepository.save(transaction);
                    });
            })
            .flatMap(mono -> mono);
    }
}