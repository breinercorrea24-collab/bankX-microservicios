package com.bca.bootcoin_service.infrastructure.input.rest.mapper;

import com.bca.bootcoin_service.domain.model.BootCoinWallet;
import com.bca.bootcoin_service.dto.BootCoinWalletResponse;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class BootCoinWalletApiMapper {

    public BootCoinWalletResponse toResponse(BootCoinWallet wallet) {
        BootCoinWalletResponse response = new BootCoinWalletResponse();
        response.setWalletId(wallet.getWalletId());
        response.setCustomerId(wallet.getCustomerId());
        response.setDocument(wallet.getDocument());
        response.setBalanceBTC(wallet.getBalanceBTC() != null ? Float.valueOf(wallet.getBalanceBTC().floatValue()) : null);
        response.setStatus(wallet.getStatus() != null ? wallet.getStatus().name() : null);
        response.setCreatedAt(wallet.getCreatedAt() != null ? OffsetDateTime.of(wallet.getCreatedAt(), ZoneOffset.UTC) : null);
        return response;
    }
}
