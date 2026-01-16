package com.bca.core_banking_service.api;

import com.bca.core_banking_service.dto.AmountRequest;
import com.bca.core_banking_service.dto.CreditCreate;
import com.bca.core_banking_service.dto.CreditPaymentResponse;
import com.bca.core_banking_service.dto.CreditResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.codec.multipart.Part;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

/**
 * A delegate to be called by the {@link CreditsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-16T18:51:45.932800-05:00[America/Lima]")
public interface CreditsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /credits/{creditId}/payment : Pago de crédito
     *
     * @param creditId  (required)
     * @param amountRequest  (required)
     * @return Pago exitoso (status code 200)
     *         or Monto inválido (status code 409)
     * @see CreditsApi#creditsCreditIdPaymentPost
     */
    default Mono<ResponseEntity<CreditPaymentResponse>> creditsCreditIdPaymentPost(String creditId,
        Mono<AmountRequest> amountRequest,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"creditId\" : \"cred-9001\", \"paymentId\" : \"pay-001\", \"paidAt\" : \"2025-01-15T10:30:00Z\", \"remainingDebt\" : 9500.0, \"paidAmount\" : 500.0, \"status\" : \"PARTIAL_PAYMENT\" }";
                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                break;
            }
        }
        return result.then(amountRequest).then(Mono.empty());

    }

    /**
     * POST /credits : Crear crédito
     *
     * @param creditCreate  (required)
     * @return Crédito creado (status code 201)
     *         or Crédito no aprobado (status code 400)
     * @see CreditsApi#creditsPost
     */
    default Mono<ResponseEntity<CreditResponse>> creditsPost(Mono<CreditCreate> creditCreate,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"interestRate\" : 18.5, \"createdAt\" : \"2025-01-01T11:00:00Z\", \"originalAmount\" : 10000.0, \"termMonths\" : 24, \"customerId\" : \"cus-1001\", \"id\" : \"cred-9001\", \"creditType\" : \"PERSONAL_LOAN\", \"pendingDebt\" : 10000.0, \"status\" : \"ACTIVE\" }";
                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                break;
            }
        }
        return result.then(creditCreate).then(Mono.empty());

    }

}
