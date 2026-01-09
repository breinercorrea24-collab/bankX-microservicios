package com.bca.bootcoin_service.infrastructure.input.rest;

import com.bca.bootcoin_service.api.WalletsApiDelegate;
import com.bca.bootcoin_service.domain.ports.input.CreateBootCoinWalletUseCase;
import com.bca.bootcoin_service.dto.BootCoinWalletRequest;
import com.bca.bootcoin_service.dto.BootCoinWalletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
public class BootCoinWalletApiDelegateImpl implements WalletsApiDelegate {

    private final CreateBootCoinWalletUseCase createBootCoinWalletUseCase;
    private static final Logger logger = LoggerFactory.getLogger(BootCoinWalletApiDelegateImpl.class);

    public BootCoinWalletApiDelegateImpl(CreateBootCoinWalletUseCase createBootCoinWalletUseCase) {
        this.createBootCoinWalletUseCase = createBootCoinWalletUseCase;
    }

    @Override
    public Mono<ResponseEntity<BootCoinWalletResponse>> walletsBootcoinPost(Mono<BootCoinWalletRequest> bootCoinWalletRequest,
                                                                           ServerWebExchange exchange) {
        return bootCoinWalletRequest
            .flatMap(request -> {
                logger.info("walletsBootcoinPost called customerId={} document={}", request.getCustomerId(), request.getDocument());
                return createBootCoinWalletUseCase.createWallet(request.getCustomerId(), request.getDocument());
            })
            .map(wallet -> {
                logger.info("Wallet created id={} customerId={}", wallet.getWalletId(), wallet.getCustomerId());
                BootCoinWalletResponse response = new BootCoinWalletResponse();
                response.setWalletId(wallet.getWalletId());
                response.setCustomerId(wallet.getCustomerId());
                response.setDocument(wallet.getDocument());
                response.setBalanceBTC( wallet.getBalanceBTC() != null ? Float.valueOf(wallet.getBalanceBTC().floatValue()) : null);
                response.setStatus(wallet.getStatus() != null ? wallet.getStatus().name() : null);
                response.setCreatedAt(wallet.getCreatedAt() != null ? java.time.OffsetDateTime.of(wallet.getCreatedAt(), java.time.ZoneOffset.UTC) : null);
                return ResponseEntity.status(201).body(response);
            })
            .onErrorResume(IllegalArgumentException.class, ex ->
                Mono.just(ResponseEntity.badRequest().build())
            );
    }
}