package com.ammar.checkout.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class HealthResourceTest {

    @Test
    void shouldReturnOkFromCheckoutHealthEndpoint() {
        given()
            .when()
            .get("/checkout/health")
            .then()
            .statusCode(200)
            .body(is("ok"));
    }
}
