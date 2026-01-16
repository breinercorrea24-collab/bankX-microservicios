package com.bca.core_banking_service.api;

import com.bca.core_banking_service.dto.AccountCreate;
import com.bca.core_banking_service.dto.AccountResponse;
import com.bca.core_banking_service.dto.AmountRequest;
import com.bca.core_banking_service.dto.ErrorResponse;
import com.bca.core_banking_service.dto.TransactionResponse;
import com.bca.core_banking_service.dto.TransferRequest;
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
 * A delegate to be called by the {@link AccountsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-16T18:51:45.932800-05:00[America/Lima]")
public interface AccountsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /accounts/{accountId}/deposit : Depósito en cuenta
     *
     * @param accountId  (required)
     * @param amountRequest  (required)
     * @return Depósito exitoso (status code 200)
     * @see AccountsApi#accountsAccountIdDepositPost
     */
    default Mono<ResponseEntity<TransactionResponse>> accountsAccountIdDepositPost(String accountId,
        Mono<AmountRequest> amountRequest,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"accountId\" : \"acc-2001\", \"amount\" : 500.0, \"fromAccountId\" : \"acc-2001\", \"balance\" : 2000.0, \"toAccountId\" : \"acc-2002\", \"type\" : \"DEPOSIT\", \"transactionId\" : \"tx-dep-01\", \"timestamp\" : \"2025-01-01T14:30:00Z\" }";
                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                break;
            }
        }
        return result.then(amountRequest).then(Mono.empty());

    }

    /**
     * POST /accounts/{accountId}/withdraw : Retiro de cuenta
     *
     * @param accountId  (required)
     * @param amountRequest  (required)
     * @return Retiro exitoso (status code 200)
     *         or Solicitud inválida (status code 400)
     *         or Saldo insuficiente (status code 409)
     * @see AccountsApi#accountsAccountIdWithdrawPost
     */
    default Mono<ResponseEntity<TransactionResponse>> accountsAccountIdWithdrawPost(String accountId,
        Mono<AmountRequest> amountRequest,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"accountId\" : \"acc-2001\", \"amount\" : 500.0, \"fromAccountId\" : \"acc-2001\", \"balance\" : 2000.0, \"toAccountId\" : \"acc-2002\", \"type\" : \"DEPOSIT\", \"transactionId\" : \"tx-dep-01\", \"timestamp\" : \"2025-01-01T14:30:00Z\" }";
                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                break;
            }
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"path\" : \"/customers/cus-1001/accounts\", \"error\" : \"Not Found\", \"message\" : \"Cliente no encontrado\", \"timestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"status\" : 404 }";
                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                break;
            }
        }
        return result.then(amountRequest).then(Mono.empty());

    }

    /**
     * POST /accounts : Crear cuenta bancaria
     *
     * @param accountCreate  (required)
     * @return Cuenta creada (status code 201)
     *         or Solicitud inválida (status code 400)
     * @see AccountsApi#accountsPost
     */
    default Mono<ResponseEntity<AccountResponse>> accountsPost(Mono<AccountCreate> accountCreate,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"balance\" : 0.8008282, \"customerId\" : \"customerId\", \"currency\" : \"currency\", \"id\" : \"id\", \"status\" : \"ACTIVE\" }";
                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                break;
            }
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"path\" : \"/customers/cus-1001/accounts\", \"error\" : \"Not Found\", \"message\" : \"Cliente no encontrado\", \"timestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"status\" : 404 }";
                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                break;
            }
        }
        return result.then(accountCreate).then(Mono.empty());

    }

    /**
     * POST /accounts/transfer : Transferencia entre cuentas
     *
     * @param transferRequest  (required)
     * @return Transferencia exitosa (status code 200)
     * @see AccountsApi#accountsTransferPost
     */
    default Mono<ResponseEntity<TransactionResponse>> accountsTransferPost(Mono<TransferRequest> transferRequest,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"accountId\" : \"acc-2001\", \"amount\" : 500.0, \"fromAccountId\" : \"acc-2001\", \"balance\" : 2000.0, \"toAccountId\" : \"acc-2002\", \"type\" : \"DEPOSIT\", \"transactionId\" : \"tx-dep-01\", \"timestamp\" : \"2025-01-01T14:30:00Z\" }";
                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                break;
            }
        }
        return result.then(transferRequest).then(Mono.empty());

    }

}
