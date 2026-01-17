package org.openapitools.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import com.bca.core_banking_service.dto.AccountType;

class EnumConverterConfigurationTest {

    private final EnumConverterConfiguration configuration = new EnumConverterConfiguration();

    @Test
    void accountTypeConverterMapsValidEnum() {
        Converter<String, AccountType> converter = configuration.accountTypeConverter();
        assertEquals(AccountType.SAVINGS, converter.convert("SAVINGS"));
    }

    @Test
    void accountTypeConverterPropagatesInvalidValues() {
        Converter<String, AccountType> converter = configuration.accountTypeConverter();
        assertThrows(IllegalArgumentException.class, () -> converter.convert("UNKNOWN"));
    }
}
