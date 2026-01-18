package com.easyjobs.identity.application.usecase;

import com.easyjobs.identity.application.gateway.RegisteredUser;
import com.easyjobs.identity.application.gateway.UserRegistrationGateway;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RegisterUserUseCase {

    private static final String DEFAULT_ROLE = "READ";

    private final UserRegistrationGateway gateway;

    public RegisterUserUseCase(UserRegistrationGateway gateway) {
        this.gateway = gateway;
    }

    public RegisteredUser execute(String name, String email, String password) {
        RegisterUserCommand command = new RegisterUserCommand(name, email, password, DEFAULT_ROLE);
        return gateway.register(command);
    }
}
