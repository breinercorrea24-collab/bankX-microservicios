package org.openapitools;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullableModule;

class OpenApiGeneratorApplicationTest {

    @Test
    void mainDelegatesToSpringApplicationRun() {
        String[] args = new String[] { "--demo" };

        assertDoesNotThrow(() -> OpenApiGeneratorApplication.main(args));
    }

    @Test
    void jsonNullableModuleProvidesBean() {
        OpenApiGeneratorApplication application = new OpenApiGeneratorApplication();
        assertInstanceOf(JsonNullableModule.class, application.jsonNullableModule());
    }
}
