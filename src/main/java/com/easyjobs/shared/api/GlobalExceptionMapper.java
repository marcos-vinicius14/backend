package com.easyjobs.shared.api;

import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.easyjobs.shared.exception.BusinessException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable throwable) {
        ErrorResponse error = errorResponse(throwable);
        int status = error.status();
        Response.ResponseBuilder builder = Response.status(status);
        builder.type(MediaType.APPLICATION_JSON);
        builder.entity(error);
        return builder.build();
    }

    private ErrorResponse errorResponse(Throwable throwable) {
        if (throwable instanceof BusinessException exception) {
            return new ErrorResponse(exception.getStatus().getStatusCode(), exception.getMessage());
        }
        if (throwable instanceof ConstraintViolationException exception) {
            String message = constraintMessage(exception);
            return ErrorResponse.badRequest(message);
        }
        if (throwable instanceof WebApplicationException exception) {
            return errorResponse(exception);
        }
        return ErrorResponse.internalError();
    }

    private String constraintMessage(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        if (violations == null || violations.isEmpty()) {
            return "Dados invalidos.";
        }
        Stream<ConstraintViolation<?>> stream = violations.stream();
        Stream<String> messages = stream.map(ConstraintViolation::getMessage);
        Collector<CharSequence, ?, String> joiner = Collectors.joining("; ");
        return messages.collect(joiner);
    }

    private ErrorResponse errorResponse(WebApplicationException exception) {
        Response response = exception.getResponse();
        int status = response.getStatus();
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return new ErrorResponse(status, "Erro de requisicao.");
        }
        return new ErrorResponse(status, message);
    }
}