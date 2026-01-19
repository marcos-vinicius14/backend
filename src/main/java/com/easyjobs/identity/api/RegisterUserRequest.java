package com.easyjobs.identity.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "RegisterUserRequest", description = "Dados para cadastro de usuário.")
public record RegisterUserRequest(
        @NotBlank(message = "nome não pode ser vazio.")
        @Schema(description = "Nome completo do usuário.", example = "João Silva")
        String name,
        @NotBlank(message = "email não pode ser vazio.")
        @Email(message = "email inválido.")
        @Schema(description = "E-mail válido para acesso.", example = "joao.silva@easyjobs.com")
        String email,
        @NotBlank(message = "senha não pode ser vazia.")
        @Schema(description = "Senha de acesso.", example = "Senha@123")
        String password) {
}
