package org.openapitools;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringApplication;

class OpenApiGeneratorApplicationTest {

    @Test
    void mainDelegatesToSpringApplicationRun() {
        String[] args = new String[] { "--demo" };

        try (var springApp = Mockito.mockStatic(SpringApplication.class)) {
            OpenApiGeneratorApplication.main(args);
            springApp.verify(() -> SpringApplication.run(OpenApiGeneratorApplication.class, args));
        }
    }

    @Test
    void jsonNullableModuleProvidesBean() {
        OpenApiGeneratorApplication application = new OpenApiGeneratorApplication();
        assertInstanceOf(JsonNullableModule.class, application.jsonNullableModule());
    }
}
