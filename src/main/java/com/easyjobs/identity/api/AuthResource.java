package com.easyjobs.identity.api;

import com.easyjobs.identity.application.gateway.AuthenticatedUser;
import com.easyjobs.identity.application.gateway.RegisteredUser;
import java.util.concurrent.CompletionStage;

import com.easyjobs.identity.application.usecase.GetAuthenticatedUserUseCase;
import com.easyjobs.identity.application.usecase.LogoutUserUseCase;
import com.easyjobs.identity.application.usecase.RegisterUserUseCase;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/api/auth")
public class AuthResource {

    private final GetAuthenticatedUserUseCase sessionUseCase;
    private final RegisterUserUseCase registerUseCase;
    private final LogoutUserUseCase logoutUseCase;

    public AuthResource(
            GetAuthenticatedUserUseCase sessionUseCase,
            RegisterUserUseCase registerUseCase,
            LogoutUserUseCase logoutUseCase) {
        this.sessionUseCase = sessionUseCase;
        this.registerUseCase = registerUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @GET
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser session() {
        return sessionUseCase.execute();
    }

    @GET
    @Path("/login")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser login() {
        return sessionUseCase.execute();
    }

    @POST
    @Path("/register")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@Valid RegisterUserRequest request) {
        String name = request.name();
        String email = request.email();
        String password = request.password();
        RegisteredUser registered = registerUseCase.execute(name, email, password);
        String id = registered.id();
        String registeredEmail = registered.email();
        RegisterUserResponse response = new RegisterUserResponse(id, registeredEmail, registered.roles());
        return Response.status(Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/logout")
    @Authenticated
    public CompletionStage<Response> logout() {
        CompletionStage<Void> logout = logoutUseCase.execute();
        return logout.thenApply(ignore -> Response.noContent().build());
    }
}
