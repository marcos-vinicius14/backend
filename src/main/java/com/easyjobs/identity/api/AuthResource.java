package com.easyjobs.identity.api;

import com.easyjobs.identity.application.gateway.AuthenticatedUser;
import com.easyjobs.identity.application.usecase.GetAuthenticatedUserUseCase;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/auth")
@Authenticated
public class AuthResource {

    private final GetAuthenticatedUserUseCase useCase;

    public AuthResource(GetAuthenticatedUserUseCase useCase) {
        this.useCase = useCase;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticatedUser session() {
        return useCase.execute();
    }
}
