package com.bca.bootcoin_service.infrastructure.input.rest;

import com.bca.bootcoin_service.domain.model.BootCoinTrade;
import com.bca.bootcoin_service.domain.ports.input.AcceptBootCoinTradeUseCase;
import com.bca.bootcoin_service.domain.ports.input.CreateBootCoinTradeUseCase;
import com.bca.bootcoin_service.dto.BootCoinTradeAcceptRequest;
import com.bca.bootcoin_service.dto.BootCoinTradeRequest;
import com.bca.bootcoin_service.dto.BootCoinTradeResponse;
import com.bca.bootcoin_service.infrastructure.input.rest.mapper.BootCoinTradeApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootCoinTradeApiDelegateImplTest {

    @Mock
    CreateBootCoinTradeUseCase createUseCase;

    @Mock
    AcceptBootCoinTradeUseCase acceptUseCase;

    @Mock
    ServerWebExchange exchange;

    private BootCoinTradeApiMapper mapper;
    private BootCoinTradeApiDelegateImpl delegate;

    @BeforeEach
    void setUp() {
        mapper = new BootCoinTradeApiMapper();
        delegate = new BootCoinTradeApiDelegateImpl(createUseCase, acceptUseCase, mapper);
    }

    @Test
    void shouldReturnCreatedWhenRequestValid() {
        BootCoinTradeRequest request = new BootCoinTradeRequest();
        request.setSellerWalletId("seller");
        request.setTradeType(BootCoinTradeRequest.TradeTypeEnum.SELL);
        request.setAmountBTC(1.0f);
        request.setPricePEN(10.0f);

        var trade = buildTrade();
        when(createUseCase.createTrade(any(), any(), any(), any())).thenReturn(Mono.just(trade));

        StepVerifier.create(delegate.bootcoinTradesPost(Mono.just(request), exchange))
            .assertNext(response -> {
                ResponseEntity<BootCoinTradeResponse> entity = response;
                assert entity.getStatusCodeValue() == 201;
                assert entity.getBody() != null;
            })
            .verifyComplete();

        verify(createUseCase).createTrade(eq("seller"), eq(BootCoinTrade.TradeType.SELL), eq(BigDecimal.valueOf(1.0f)), eq(BigDecimal.valueOf(10.0f)));
    }

    @Test
    void shouldRespondBadRequestWhenSellerMissing() {
        BootCoinTradeRequest request = new BootCoinTradeRequest();
        request.setSellerWalletId("");
        request.setTradeType(BootCoinTradeRequest.TradeTypeEnum.BUY);
        request.setAmountBTC(1.0f);
        request.setPricePEN(10.0f);

        StepVerifier.create(delegate.bootcoinTradesPost(Mono.just(request), exchange))
            .expectNextMatches(response -> response.getStatusCode().is4xxClientError())
            .verifyComplete();
    }

    @Test
    void shouldRespondBadRequestWhenTradeTypeMissing() {
        BootCoinTradeRequest request = new BootCoinTradeRequest();
        request.setSellerWalletId("seller");
        request.setAmountBTC(1.0f);
        request.setPricePEN(10.0f);

        StepVerifier.create(delegate.bootcoinTradesPost(Mono.just(request), exchange))
            .expectNextMatches(response -> response.getStatusCode().is4xxClientError())
            .verifyComplete();
    }

    @Test
    void shouldRespondBadRequestWhenBodyMissing() {
        StepVerifier.create(delegate.bootcoinTradesPost(Mono.empty(), exchange))
            .expectNextMatches(response -> response.getStatusCode().is4xxClientError())
            .verifyComplete();
    }

    @Test
    void shouldReturnBadRequestWhenCreateThrowsIllegalArgument() {
        BootCoinTradeRequest request = new BootCoinTradeRequest();
        request.setSellerWalletId("seller");
        request.setTradeType(BootCoinTradeRequest.TradeTypeEnum.SELL);

        when(createUseCase.createTrade(any(), any(), any(), any()))
            .thenReturn(Mono.error(new IllegalArgumentException("boom")));

        StepVerifier.create(delegate.bootcoinTradesPost(Mono.just(request), exchange))
            .expectNextMatches(response -> response.getStatusCode().is4xxClientError())
            .verifyComplete();
    }

    @Test
    void shouldAcceptTradeWhenRequestValid() {
        var acceptRequest = new BootCoinTradeAcceptRequest();
        acceptRequest.setBuyerWalletId("buyer");
        var trade = buildTrade();
        trade.setBuyerWalletId("buyer");
        trade.setCompletedAt(LocalDateTime.now());

        when(acceptUseCase.acceptTrade("trade-id", "buyer")).thenReturn(Mono.just(trade));

        StepVerifier.create(delegate.bootcoinTradesTradeIdAcceptPost("trade-id", Mono.just(acceptRequest), exchange))
            .assertNext(response -> {
                assert response.getStatusCode().is2xxSuccessful();
                assert response.getBody() != null;
                assert response.getBody().getBuyerWalletId().equals("buyer");
            })
            .verifyComplete();
    }

    @Test
    void shouldBadRequestWhenAcceptRequestMissingBody() {
        StepVerifier.create(delegate.bootcoinTradesTradeIdAcceptPost("trade-id", Mono.empty(), exchange))
            .expectNextMatches(response -> response.getStatusCode().is4xxClientError())
            .verifyComplete();
    }

    @Test
    void shouldBadRequestWhenBuyerMissing() {
        var acceptRequest = new BootCoinTradeAcceptRequest();
        acceptRequest.setBuyerWalletId("");

        StepVerifier.create(delegate.bootcoinTradesTradeIdAcceptPost("trade-id", Mono.just(acceptRequest), exchange))
            .expectNextMatches(response -> response.getStatusCode().is4xxClientError())
            .verifyComplete();
    }

    @Test
    void shouldBadRequestWhenAcceptThrows() {
        var acceptRequest = new BootCoinTradeAcceptRequest();
        acceptRequest.setBuyerWalletId("buyer");

        when(acceptUseCase.acceptTrade("trade-id", "buyer"))
            .thenReturn(Mono.error(new IllegalArgumentException("boom")));

        StepVerifier.create(delegate.bootcoinTradesTradeIdAcceptPost("trade-id", Mono.just(acceptRequest), exchange))
            .expectNextMatches(response -> response.getStatusCode().is4xxClientError())
            .verifyComplete();
    }

    private static BootCoinTrade buildTrade() {
        var trade = new BootCoinTrade();
        trade.setTradeId("trade-id");
        trade.setSellerWalletId("seller");
        trade.setTradeType(BootCoinTrade.TradeType.SELL);
        trade.setAmountBTC(new BigDecimal("1"));
        trade.setPricePEN(new BigDecimal("10"));
        trade.setStatus(BootCoinTrade.TradeStatus.OPEN);
        trade.setCreatedAt(LocalDateTime.now());
        return trade;
    }
}
