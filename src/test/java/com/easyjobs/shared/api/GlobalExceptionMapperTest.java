package com.easyjobs.shared.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.easyjobs.shared.exception.BusinessException;

class GlobalExceptionMapperTest {

    private final GlobalExceptionMapper mapper = new GlobalExceptionMapper();

    @Test
    void shouldMapBusinessException() {
        BusinessException exception = new TestBusinessException("Erro de teste", Status.BAD_REQUEST);
        Response response = mapper.toResponse(exception);

        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals("Erro de teste", entity.message());
    }

    @Test
    void shouldMapConstraintViolationException() {
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        when(violation1.getMessage()).thenReturn("Erro 1");
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        when(violation2.getMessage()).thenReturn("Erro 2");

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation1, violation2));
        Response response = mapper.toResponse(exception);

        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        // Set order is not guaranteed, so we check if it contains both parts
        String message = entity.message();
        assertEquals(true, message.contains("Erro 1"));
        assertEquals(true, message.contains("Erro 2"));
        assertEquals(true, message.contains("; "));
    }

    @Test
    void shouldMapWebApplicationException() {
        WebApplicationException exception = new WebApplicationException("Not Found", Status.NOT_FOUND);
        Response response = mapper.toResponse(exception);

        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals("Not Found", entity.message());
    }

    @Test
    void shouldMapUnexpectedException() {
        RuntimeException exception = new RuntimeException("Erro inesperado");
        Response response = mapper.toResponse(exception);

        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals("Erro interno inesperado.", entity.message());
    }

    static class TestBusinessException extends BusinessException {
        private final Status status;

        protected TestBusinessException(String message, Status status) {
            super(message);
            this.status = status;
        }

        @Override
        public Status getStatus() {
            return status;
        }
    }
}
