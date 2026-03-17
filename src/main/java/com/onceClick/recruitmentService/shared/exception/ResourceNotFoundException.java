package com.onceClick.recruitmentService.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when oauth2ResourceServer.md requested resource is not found
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND"
        );
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(
                String.format("%s not found with id: %d", resourceName, id),
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND"
        );
    }
}
