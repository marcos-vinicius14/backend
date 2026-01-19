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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/auth")
@Tag(
        name = "Autenticação",
        description = "Operações de sessão e cadastro. O login segue o fluxo OIDC com redirect: ao acessar "
                + "uma rota protegida (ex.: /api/auth/login ou /api/auth), o Quarkus responde 302 para o "
                + "Keycloak e, após autenticar, o usuário retorna com sessão ativa.")
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
    @Operation(
            summary = "Consultar sessão",
            description = "Retorna o usuário autenticado atual. Se não houver sessão, o fluxo OIDC "
                    + "redireciona para o Keycloak.")
    @APIResponse(
            responseCode = "200",
            description = "Usuário autenticado.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = AuthenticatedUser.class)))
    @APIResponse(responseCode = "302", description = "Redireciona para o login do Keycloak.")
    @APIResponse(responseCode = "401", description = "Usuário não autenticado.")
    @APIResponse(responseCode = "403", description = "Acesso negado.")
    public AuthenticatedUser session() {
        return sessionUseCase.execute();
    }

    @GET
    @Path("/login")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Iniciar login (OIDC)",
            description = "Endpoint gatilho para o fluxo OIDC. Se não houver sessão, o Quarkus responde 302 "
                    + "para o endpoint de autorização do Keycloak. Após autenticar, o usuário retorna e a "
                    + "sessão é criada automaticamente.")
    @APIResponse(
            responseCode = "200",
            description = "Usuário autenticado.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = AuthenticatedUser.class)))
    @APIResponse(responseCode = "302", description = "Redireciona para o login do Keycloak.")
    @APIResponse(responseCode = "401", description = "Usuário não autenticado.")
    @APIResponse(responseCode = "403", description = "Acesso negado.")
    public AuthenticatedUser login() {
        return sessionUseCase.execute();
    }

    @POST
    @Path("/register")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Registrar usuário", description = "Cria um novo usuário com perfil padrão.")
    @APIResponse(
            responseCode = "201",
            description = "Usuário registrado.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RegisterUserResponse.class)))
    @APIResponse(responseCode = "400", description = "Dados inválidos.")
    @APIResponse(responseCode = "500", description = "Erro interno ao registrar o usuário.")
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RegisterUserRequest.class)))
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
    @Operation(summary = "Logout do usuário", description = "Encerra a sessão do usuário autenticado.")
    @APIResponse(responseCode = "204", description = "Logout concluído.")
    @APIResponse(responseCode = "401", description = "Usuário não autenticado.")
    @APIResponse(responseCode = "403", description = "Acesso negado.")
    @APIResponse(responseCode = "500", description = "Erro interno ao realizar logout.")
    public CompletionStage<Response> logout() {
        CompletionStage<Void> logout = logoutUseCase.execute();
        return logout.thenApply(ignore -> Response.noContent().build());
    }
}
