package com.easyjobs.identity.infrastructure.keycloak;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "keycloak.admin")
public interface KeycloakAdminConfig {

    String adminRealm();

    String userRealm();

    String clientId();

    String username();

    String password();
}
