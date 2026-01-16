package com.bca.core_banking_service.api;

import com.bca.core_banking_service.dto.AccountPolymorphicResponse;
import com.bca.core_banking_service.dto.ErrorResponse;
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
 * A delegate to be called by the {@link CustomersApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-16T18:51:45.932800-05:00[America/Lima]")
public interface CustomersApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /customers/{customerId}/accounts : Listar cuentas por cliente
     *
     * @param customerId  (required)
     * @return OK (status code 200)
     *         or Solicitud inválida (status code 400)
     *         or Cliente no encontrado (status code 404)
     *         or Error interno del servidor (status code 500)
     * @see CustomersApi#customersCustomerIdAccountsGet
     */
    default Mono<ResponseEntity<Flux<AccountPolymorphicResponse>>> customersCustomerIdAccountsGet(String customerId,
        ServerWebExchange exchange) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.NOT_IMPLEMENTED);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "[ { }, { } ]";
                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                break;
            }
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"path\" : \"/customers/cus-1001/accounts\", \"error\" : \"Not Found\", \"message\" : \"Cliente no encontrado\", \"timestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"status\" : 404 }";
                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                break;
            }
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"path\" : \"/customers/cus-1001/accounts\", \"error\" : \"Not Found\", \"message\" : \"Cliente no encontrado\", \"timestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"status\" : 404 }";
                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                break;
            }
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                String exampleString = "{ \"path\" : \"/customers/cus-1001/accounts\", \"error\" : \"Not Found\", \"message\" : \"Cliente no encontrado\", \"timestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"status\" : 404 }";
                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                break;
            }
        }
        return result.then(Mono.empty());

    }

}
