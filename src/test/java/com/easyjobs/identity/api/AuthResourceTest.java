package com.easyjobs.identity.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;

@QuarkusTest
class AuthResourceTest {

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() {
        given()
                .when()
                .get("/api/auth")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "ada", roles = { "ADMIN", "OWNER" })
    void shouldReturnSessionInfoWhenAuthenticated() {
        given()
                .when()
                .get("/api/auth")
                .then()
                .statusCode(200)
                .body("username", is("ada"))
                .body("roles", hasItems("ADMIN", "OWNER"));
    }
}
