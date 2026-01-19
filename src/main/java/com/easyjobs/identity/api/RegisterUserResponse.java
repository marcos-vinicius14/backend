package com.easyjobs.identity.api;

import java.util.Set;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "RegisterUserResponse", description = "Resposta de cadastro de usuário.")
public record RegisterUserResponse(
        @Schema(description = "Identificador do usuário.", example = "9f7b2c16-3a35-4d23-9d2e-6c2d5b8b8f7a")
        String id,
        @Schema(description = "E-mail registrado.", example = "joao.silva@easyjobs.com")
        String email,
        @Schema(description = "Perfis atribuídos ao usuário.", example = "[\"READ\"]")
        Set<String> roles) {
}
