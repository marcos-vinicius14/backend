package com.easyjobs.identity.infrastructure.keycloak;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

@Path("/")
@RegisterRestClient(configKey = "keycloak-admin")
public interface KeycloakAdminClient {

    @POST
    @Path("/realms/{realm}/protocol/openid-connect/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    KeycloakTokenResponse token(@PathParam("realm") String realm, MultivaluedMap<String, String> form);

    @POST
    @Path("/admin/realms/{realm}/users")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createUser(
            @HeaderParam("Authorization") String authorization,
            @PathParam("realm") String realm,
            KeycloakUserRepresentation user);

    @GET
    @Path("/admin/realms/{realm}/roles/{roleName}")
    @Produces(MediaType.APPLICATION_JSON)
    KeycloakRoleRepresentation role(
            @HeaderParam("Authorization") String authorization,
            @PathParam("realm") String realm,
            @PathParam("roleName") String roleName);

    @POST
    @Path("/admin/realms/{realm}/users/{userId}/role-mappings/realm")
    @Consumes(MediaType.APPLICATION_JSON)
    void assignRole(
            @HeaderParam("Authorization") String authorization,
            @PathParam("realm") String realm,
            @PathParam("userId") String userId,
            List<KeycloakRoleRepresentation> roles);

    @DELETE
    @Path("/admin/realms/{realm}/users/{userId}")
    void deleteUser(
            @HeaderParam("Authorization") String authorization,
            @PathParam("realm") String realm,
            @PathParam("userId") String userId);
}
