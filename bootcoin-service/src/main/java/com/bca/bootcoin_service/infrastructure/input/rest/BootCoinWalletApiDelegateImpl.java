package com.bca.bootcoin_service.infrastructure.input.rest;

import com.bca.bootcoin_service.api.WalletsApiDelegate;
import com.bca.bootcoin_service.domain.ports.input.CreateBootCoinWalletUseCase;
import com.bca.bootcoin_service.dto.BootCoinWalletRequest;
import com.bca.bootcoin_service.dto.BootCoinWalletResponse;
import com.bca.bootcoin_service.infrastructure.input.rest.mapper.BootCoinWalletApiMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
public class BootCoinWalletApiDelegateImpl implements WalletsApiDelegate {

    private final CreateBootCoinWalletUseCase createBootCoinWalletUseCase;
    private final BootCoinWalletApiMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(BootCoinWalletApiDelegateImpl.class);

    public BootCoinWalletApiDelegateImpl(CreateBootCoinWalletUseCase createBootCoinWalletUseCase, BootCoinWalletApiMapper mapper) {
        this.createBootCoinWalletUseCase = createBootCoinWalletUseCase;
        this.mapper = mapper;
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
                return ResponseEntity.status(201).body(mapper.toResponse(wallet));
            })
            .onErrorResume(IllegalArgumentException.class, ex ->
                Mono.just(ResponseEntity.badRequest().build())
            );
    }
}