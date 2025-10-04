package org.esteban.springboot.springmvc.app.franchise_apirest.domain.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
