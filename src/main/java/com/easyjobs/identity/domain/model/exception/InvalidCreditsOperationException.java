package com.easyjobs.identity.domain.model.exception;

import com.easyjobs.shared.exception.BusinessException;
import jakarta.ws.rs.core.Response.Status;

public class InvalidCreditsOperationException extends BusinessException {
    public InvalidCreditsOperationException(String message) {
        super(message);
    }

    @Override
    public Status getStatus() {
        return Status.BAD_REQUEST;
    }
}