package com.easyjobs.identity.application.usecase;

import com.easyjobs.identity.application.gateway.AuthenticatedUser;
import com.easyjobs.identity.application.gateway.AuthenticatedUserGateway;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetAuthenticatedUserUseCase {

    private final AuthenticatedUserGateway gateway;

    public GetAuthenticatedUserUseCase(AuthenticatedUserGateway gateway) {
        this.gateway = gateway;
    }

    public AuthenticatedUser execute() {
        return gateway.authenticatedUser();
    }
}
