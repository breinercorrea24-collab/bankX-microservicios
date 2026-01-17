package org.openapitools.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

class HomeControllerTest {

    private final HomeController controller = new HomeController();
    private final WebTestClient client = WebTestClient
            .bindToRouterFunction(controller.index())
            .configureClient()
            .build();

    @Test
    void indexRedirectsToSwaggerUi() {
        client.get()
                .uri("/")
                .exchange()
                .expectStatus().isTemporaryRedirect()
                .expectHeader().valueEquals("Location", "swagger-ui.html");
    }
}
