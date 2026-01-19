package com.easyjobs.shared.api;

import jakarta.ws.rs.core.Response.Status;

public record ErrorResponse(int status, String message) {

    public static ErrorResponse badRequest(String message) {
        Status status = Status.BAD_REQUEST;
        int code = status.getStatusCode();
        return new ErrorResponse(code, message);
    }

    public static ErrorResponse conflict(String message) {
        Status status = Status.CONFLICT;
        int code = status.getStatusCode();
        return new ErrorResponse(code, message);
    }

    public static ErrorResponse badGateway(String message) {
        Status status = Status.BAD_GATEWAY;
        int code = status.getStatusCode();
        return new ErrorResponse(code, message);
    }

    public static ErrorResponse internalError() {
        Status status = Status.INTERNAL_SERVER_ERROR;
        int code = status.getStatusCode();
        return new ErrorResponse(code, "Erro interno inesperado.");
    }
}
