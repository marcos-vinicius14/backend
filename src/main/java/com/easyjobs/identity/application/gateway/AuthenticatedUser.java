package com.easyjobs.identity.application.gateway;

import java.util.Set;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "AuthenticatedUser", description = "Usuário autenticado e seus perfis.")
public record AuthenticatedUser(
        @Schema(description = "Identificador do usuário autenticado.", example = "joao.silva@easyjobs.com")
        String username,
        @Schema(description = "Perfis associados ao usuário.", example = "[\"READ\"]")
        Set<String> roles) {
}
