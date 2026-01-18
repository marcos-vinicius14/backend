package com.easyjobs.identity.application.usecase;

public record RegisterUserCommand(String name, String email, String password, String role) {
}
