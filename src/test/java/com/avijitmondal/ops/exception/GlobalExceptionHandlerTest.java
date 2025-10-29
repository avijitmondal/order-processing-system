package com.avijitmondal.ops.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private BindingResult bindingResult;

    @Test
    void handleOrderNotFoundException_returnsNotFound() {
        OrderNotFoundException exception = new OrderNotFoundException("Order not found");

        ResponseEntity<?> response = handler.handleOrderNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        GlobalExceptionHandler.ErrorResponse body = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertEquals("Order not found", body.getMessage());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleUserNotFoundException_returnsNotFound() {
        UserNotFoundException exception = new UserNotFoundException("User not found");

        ResponseEntity<?> response = handler.handleUserNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        GlobalExceptionHandler.ErrorResponse body = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertEquals("User not found", body.getMessage());
    }

    @Test
    void handleInvalidOrderStatusException_returnsBadRequest() {
        InvalidOrderStatusException exception = new InvalidOrderStatusException("Invalid status");

        ResponseEntity<?> response = handler.handleInvalidOrderStatusException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GlobalExceptionHandler.ErrorResponse body = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Invalid status", body.getMessage());
    }

    @Test
    void handleInsufficientStockException_returnsBadRequest() {
        InsufficientStockException exception = new InsufficientStockException("Insufficient stock");

        ResponseEntity<?> response = handler.handleInsufficientStockException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        GlobalExceptionHandler.ErrorResponse body = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertNotNull(body);
        assertEquals(409, body.getStatus());
        assertEquals("Insufficient stock", body.getMessage());
    }

    @Test
    void handleValidationExceptions_returnsBadRequest() {
        FieldError fieldError = new FieldError("object", "field", "Field error message");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<?> response = handler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GlobalExceptionHandler.ValidationErrorResponse body = (GlobalExceptionHandler.ValidationErrorResponse) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Validation failed", body.getMessage());
        assertNotNull(body.getErrors());
        assertEquals("Field error message", body.getErrors().get("field"));
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        Exception exception = new Exception("Generic error");

        ResponseEntity<?> response = handler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        GlobalExceptionHandler.ErrorResponse body = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertNotNull(body);
        assertEquals(500, body.getStatus());
        assertTrue(body.getMessage().contains("Generic error"));
    }

    @Test
    void handleMethodArgumentTypeMismatch_returnsBadRequest() {
        MethodParameter methodParameter = mock(MethodParameter.class);
        
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
            "invalid", String.class, "id", methodParameter, null
        );

        ResponseEntity<?> response = handler.handleMethodArgumentTypeMismatch(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GlobalExceptionHandler.ErrorResponse body = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertTrue(body.getMessage().contains("Invalid value"));
    }

    @Test
    void handleUsernameNotFound_returnsNotFound() {
        UsernameNotFoundException exception = new UsernameNotFoundException("Username not found");

        ResponseEntity<?> response = handler.handleUsernameNotFound(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        GlobalExceptionHandler.ErrorResponse body = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("Invalid authentication credentials", body.getMessage());
    }
}
