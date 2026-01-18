package com.easyjobs.identity.infrastructure.keycloak;

public record KeycloakCredentialRepresentation(String type, String value, boolean temporary) {
}
