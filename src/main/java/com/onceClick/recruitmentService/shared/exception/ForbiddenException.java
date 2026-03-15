package com.onceClick.recruitmentService.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user doesn't have permission to access resource
 */
public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }

    public ForbiddenException() {
        super("You don't have permission to access this resource",
                HttpStatus.FORBIDDEN,
                "FORBIDDEN");
    }

    public ForbiddenException(String resource, String action) {
        super(
                String.format("You don't have permission to %s %s", action, resource),
                HttpStatus.FORBIDDEN,
                "FORBIDDEN"
        );
    }
}
