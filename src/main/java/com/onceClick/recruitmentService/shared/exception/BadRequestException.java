package com.onceClick.recruitmentService.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when request is invalid
 */
public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public BadRequestException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}