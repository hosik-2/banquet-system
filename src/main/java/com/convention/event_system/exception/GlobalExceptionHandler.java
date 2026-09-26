package com.convention.event_system.exception;

import com.convention.event_system.auth.UnauthenticatedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthenticatedException(UnauthenticatedException e) {
        ErrorResponse authenticationError = new ErrorResponse(LocalDateTime.now(), "AUTHENTICATION_ERROR", HttpStatus.UNAUTHORIZED, e.getMessage());
        return new ResponseEntity<>(authenticationError, authenticationError.getStatus());

    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), errorCode.name(), errorCode.getStatus(), errorCode.getMessage());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }
}
