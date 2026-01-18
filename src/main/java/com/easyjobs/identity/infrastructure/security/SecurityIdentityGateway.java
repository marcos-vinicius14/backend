package com.easyjobs.identity.infrastructure.security;

import java.security.Principal;
import java.util.Set;

import com.easyjobs.identity.application.gateway.AuthenticatedUser;
import com.easyjobs.identity.application.gateway.AuthenticatedUserGateway;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SecurityIdentityGateway implements AuthenticatedUserGateway {

    private final SecurityIdentity identity;

    public SecurityIdentityGateway(SecurityIdentity identity) {
        this.identity = identity;
    }

    @Override
    public AuthenticatedUser authenticatedUser() {
        Principal principal = identity.getPrincipal();
        String username = principal.getName();
        Set<String> roles = identity.getRoles();
        return new AuthenticatedUser(username, roles);
    }
}
