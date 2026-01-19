package com.easyjobs.identity.infrastructure.keycloak;

import java.util.List;

public record KeycloakUserRepresentation(
        String username,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        boolean emailVerified,
        List<KeycloakCredentialRepresentation> credentials) {
}
