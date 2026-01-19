package com.easyjobs.identity.infrastructure.security;

import java.util.concurrent.CompletionStage;

import com.easyjobs.identity.application.gateway.LogoutGateway;

import io.quarkus.oidc.OidcSession;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OidcSessionLogoutGateway implements LogoutGateway {

    private final OidcSession oidcSession;

    public OidcSessionLogoutGateway(OidcSession oidcSession) {
        this.oidcSession = oidcSession;
    }

    @Override
    public CompletionStage<Void> logout() {
        var logout = oidcSession.logout();
        return logout.subscribeAsCompletionStage();
    }
}
