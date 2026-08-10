package com.convention.event_system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;

    private String errorCode; // "BANQUET_DUPLICATE" 같은 예외 출력

    private HttpStatus status;

    private String message;
}
