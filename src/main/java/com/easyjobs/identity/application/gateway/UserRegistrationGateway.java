package com.easyjobs.identity.application.gateway;

import com.easyjobs.identity.application.usecase.RegisterUserCommand;

public interface UserRegistrationGateway {
    RegisteredUser register(RegisterUserCommand command);
}
