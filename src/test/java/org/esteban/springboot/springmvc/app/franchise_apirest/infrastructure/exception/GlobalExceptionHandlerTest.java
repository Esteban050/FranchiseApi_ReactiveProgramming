package org.esteban.springboot.springmvc.app.franchise_apirest.infrastructure.exception;

import org.esteban.springboot.springmvc.app.franchise_apirest.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should handle ResourceNotFoundException")
    void shouldHandleResourceNotFoundException() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Franchise not found");

        // When
        Mono<ResponseEntity<ErrorResponse>> result = exceptionHandler.handleResourceNotFoundException(exception);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals(404, response.getBody().getStatus());
                    assertEquals("Not Found", response.getBody().getError());
                    assertEquals("Franchise not found", response.getBody().getMessage());
                    assertNotNull(response.getBody().getTimestamp());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException")
    void shouldHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Stock cannot be negative");

        // When
        Mono<ResponseEntity<ErrorResponse>> result = exceptionHandler.handleIllegalArgumentException(exception);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals(400, response.getBody().getStatus());
                    assertEquals("Bad Request", response.getBody().getError());
                    assertEquals("Stock cannot be negative", response.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle WebExchangeBindException with validation errors")
    void shouldHandleWebExchangeBindException() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("franchiseRequest", "name", "Name is required");
        FieldError fieldError2 = new FieldError("franchiseRequest", "stock", "Stock must be positive");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        WebExchangeBindException exception = new WebExchangeBindException(null, bindingResult);

        // When
        Mono<ResponseEntity<Map<String, Object>>> result = exceptionHandler.handleValidationException(exception);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertNotNull(response.getBody());

                    Map<String, Object> body = response.getBody();
                    assertEquals(400, body.get("status"));
                    assertEquals("Validation Failed", body.get("error"));
                    assertNotNull(body.get("timestamp"));

                    @SuppressWarnings("unchecked")
                    Map<String, String> validationErrors = (Map<String, String>) body.get("validationErrors");
                    assertNotNull(validationErrors);
                    assertEquals("Name is required", validationErrors.get("name"));
                    assertEquals("Stock must be positive", validationErrors.get("stock"));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void shouldHandleGenericException() {
        // Given
        Exception exception = new Exception("Unexpected error occurred");

        // When
        Mono<ResponseEntity<ErrorResponse>> result = exceptionHandler.handleGenericException(exception);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals(500, response.getBody().getStatus());
                    assertEquals("Internal Server Error", response.getBody().getError());
                    assertTrue(response.getBody().getMessage().contains("An unexpected error occurred"));
                    assertTrue(response.getBody().getMessage().contains("Unexpected error occurred"));
                })
                .verifyComplete();
    }
}
