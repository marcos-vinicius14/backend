package com.easyjobs.identity.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.easyjobs.identity.application.gateway.RegisteredUser;
import com.easyjobs.identity.application.gateway.UserRegistrationGateway;
import com.easyjobs.identity.application.usecase.RegisterUserCommand;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;

@QuarkusTest
class AuthResourceTest {

    @InjectMock
    UserRegistrationGateway registrationGateway;

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

    @Test
    void shouldRegisterUser() {
        RegisteredUser registered = new RegisteredUser("user-id", "ada@example.com", Set.of("READ"));
        when(registrationGateway.register(any(RegisterUserCommand.class))).thenReturn(registered);

        given()
                .contentType(ContentType.JSON)
                .body(new RegisterUserRequest("Ada Lovelace", "ada@example.com", "secret"))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201)
                .body("id", is("user-id"))
                .body("email", is("ada@example.com"))
                .body("roles", hasItems("READ"));

        ArgumentCaptor<RegisterUserCommand> captor = ArgumentCaptor.forClass(RegisterUserCommand.class);
        verify(registrationGateway).register(captor.capture());
        RegisterUserCommand command = captor.getValue();
        assertEquals("Ada Lovelace", command.name());
        assertEquals("ada@example.com", command.email());
        assertEquals("secret", command.password());
        assertEquals("READ", command.role());
    }
}
