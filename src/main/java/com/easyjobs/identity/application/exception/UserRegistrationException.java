package com.easyjobs.identity.application.exception;

import com.easyjobs.shared.exception.BusinessException;
import jakarta.ws.rs.core.Response.Status;

public class UserRegistrationException extends BusinessException {

    public UserRegistrationException(String message) {
        super(message);
    }

    public UserRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public Status getStatus() {
        return Status.BAD_GATEWAY;
    }
}