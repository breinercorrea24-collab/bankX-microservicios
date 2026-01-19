package com.bca.core_banking_service.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CreditsApiControllerTest {

    @Test
    void usesProvidedDelegateWhenAvailable() {
        CreditsApiDelegate provided = Mockito.mock(CreditsApiDelegate.class);

        CreditsApiController controller = new CreditsApiController(provided);

        assertSame(provided, controller.getDelegate());
    }

    @Test
    void fallsBackToDefaultDelegateWhenNull() {
        CreditsApiController controller = new CreditsApiController(null);

        assertNotNull(controller.getDelegate());
    }
}
