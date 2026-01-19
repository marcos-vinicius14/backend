package com.easyjobs.identity.domain.model.exception;

import com.easyjobs.shared.exception.BusinessException;
import jakarta.ws.rs.core.Response.Status;

public class InsufficientCreditsException extends BusinessException {
    public InsufficientCreditsException(String message) {
        super(message);
    }

    @Override
    public Status getStatus() {
        return Status.CONFLICT;
    }
}