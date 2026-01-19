package com.easyjobs.identity.infrastructure.keycloak;

import java.util.List;
import java.util.Set;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.easyjobs.identity.application.exception.UserRegistrationException;
import com.easyjobs.identity.application.gateway.RegisteredUser;
import com.easyjobs.identity.application.gateway.UserRegistrationGateway;
import com.easyjobs.identity.application.usecase.RegisterUserCommand;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class KeycloakUserRegistrationGateway implements UserRegistrationGateway {

    private static final String PASSWORD_TYPE = "password";
    private static final String TOKEN_GRANT_TYPE = "password";

    private final KeycloakAdminClient client;
    private final KeycloakAdminConfig config;

    public KeycloakUserRegistrationGateway(@RestClient KeycloakAdminClient client, KeycloakAdminConfig config) {
        this.client = client;
        this.config = config;
    }

    @Override
    public RegisteredUser register(RegisterUserCommand command) {
        try {
            String authorization = authorizationHeader();
            String userId = createUser(authorization, command);
            assignRoleWithCompensation(authorization, userId, command.role());
            return new RegisteredUser(userId, command.email(), Set.of(command.role()));
        } catch (WebApplicationException exception) {
            throw new UserRegistrationException("Não foi possível registrar o usuário no Keycloak.", exception);
        }
    }

    private String authorizationHeader() {
        String realm = config.adminRealm();
        KeycloakTokenResponse token = client.token(realm, tokenForm());
        String accessToken = token.accessToken();
        if (accessToken == null || accessToken.isBlank()) {
            throw new UserRegistrationException("Não foi possível obter o token administrativo do Keycloak.");
        }
        return "Bearer " + accessToken;
    }

    private MultivaluedMap<String, String> tokenForm() {
        MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
        form.putSingle("grant_type", TOKEN_GRANT_TYPE);
        form.putSingle("client_id", config.clientId());
        form.putSingle("username", config.username());
        form.putSingle("password", config.password());
        return form;
    }

    private String createUser(String authorization, RegisterUserCommand command) {
        KeycloakUserRepresentation user = userRepresentation(command);
        String realm = config.userRealm();
        try (Response response = client.createUser(authorization, realm, user)) {
            Response.StatusType status = response.getStatusInfo();
            Response.Status.Family family = status.getFamily();
            if (family != Response.Status.Family.SUCCESSFUL) {
                throw new UserRegistrationException("Não foi possível criar o usuário no Keycloak.");
            }
            String location = response.getHeaderString("Location");
            return userIdFromLocation(location);
        }
    }

    private KeycloakUserRepresentation userRepresentation(RegisterUserCommand command) {
        List<KeycloakCredentialRepresentation> credentials = List.of(credential(command.password()));
        return new KeycloakUserRepresentation(
                command.email(),
                command.email(),
                command.name(),
                "",
                true,
                false,
                credentials);
    }

    private KeycloakCredentialRepresentation credential(String password) {
        return new KeycloakCredentialRepresentation(PASSWORD_TYPE, password, false);
    }

    private String userIdFromLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new UserRegistrationException("Não foi possível obter o identificador do usuário no Keycloak.");
        }
        int lastSlash = location.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash == location.length() - 1) {
            throw new UserRegistrationException("Não foi possível obter o identificador do usuário no Keycloak.");
        }
        return location.substring(lastSlash + 1);
    }

    private void assignRole(String authorization, String userId, String roleName) {
        String realm = config.userRealm();
        KeycloakRoleRepresentation role = client.role(authorization, realm, roleName);
        client.assignRole(authorization, realm, userId, List.of(role));
    }

    private void assignRoleWithCompensation(String authorization, String userId, String roleName) {
        try {
            assignRole(authorization, userId, roleName);
        } catch (WebApplicationException exception) {
            deleteUserSafely(authorization, userId);
            throw new UserRegistrationException("Não foi possível atribuir o perfil ao usuário no Keycloak.", exception);
        }
    }

    private void deleteUserSafely(String authorization, String userId) {
        try {
            deleteUser(authorization, userId);
        } catch (WebApplicationException deleteException) {
            throw new UserRegistrationException(
                    "Não foi possível atribuir o perfil ao usuário e não foi possível reverter o cadastro no Keycloak.",
                    deleteException);
        }
    }

    private void deleteUser(String authorization, String userId) {
        String realm = config.userRealm();
        client.deleteUser(authorization, realm, userId);
    }
}
