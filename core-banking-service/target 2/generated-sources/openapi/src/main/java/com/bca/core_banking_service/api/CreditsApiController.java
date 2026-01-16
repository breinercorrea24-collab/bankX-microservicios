package com.bca.core_banking_service.api;

import com.bca.core_banking_service.dto.AmountRequest;
import com.bca.core_banking_service.dto.CreditCreate;
import com.bca.core_banking_service.dto.CreditPaymentResponse;
import com.bca.core_banking_service.dto.CreditResponse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-16T18:51:43.266244-05:00[America/Lima]")
@Controller
@RequestMapping("${openapi.bootCoinBankX.base-path:}")
public class CreditsApiController implements CreditsApi {

    private final CreditsApiDelegate delegate;

    public CreditsApiController(@Autowired(required = false) CreditsApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new CreditsApiDelegate() {});
    }

    @Override
    public CreditsApiDelegate getDelegate() {
        return delegate;
    }

}
