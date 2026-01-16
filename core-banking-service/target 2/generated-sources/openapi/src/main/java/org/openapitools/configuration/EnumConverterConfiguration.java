package org.openapitools.configuration;

import com.bca.core_banking_service.dto.AccountType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.accountTypeConverter")
    Converter<String, AccountType> accountTypeConverter() {
        return new Converter<String, AccountType>() {
            @Override
            public AccountType convert(String source) {
                return AccountType.fromValue(source);
            }
        };
    }

}
