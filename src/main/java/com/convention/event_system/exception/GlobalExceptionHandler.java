package com.convention.event_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {

        //TODO 추후에 커스텀 예외 만들어서 리펙토링 하기
        if ("같은 날짜, 같은 베뉴에 행사가 있습니다.".equals(e.getMessage())) {
            ErrorResponse banquetDuplicate = new ErrorResponse(LocalDateTime.now(), "BANQUET_DUPLICATE", HttpStatus.BAD_REQUEST, e.getMessage());
            return new ResponseEntity<>(banquetDuplicate, HttpStatus.BAD_REQUEST);
        }

        if ("시작시간이 종료시간보다 늦습니다.".equals(e.getMessage())) {
            ErrorResponse timeError = new ErrorResponse(LocalDateTime.now(), "TIME_ERROR", HttpStatus.BAD_REQUEST, e.getMessage());
            return new ResponseEntity<>(timeError, HttpStatus.BAD_REQUEST);
        }

        if ("판촉자가 아니면 행사를 등록할 수 없습니다.".equals(e.getMessage())) {
            ErrorResponse authorityError = new ErrorResponse(LocalDateTime.now(), "AUTHORITY_ERROR", HttpStatus.BAD_REQUEST, e.getMessage());
            return new ResponseEntity<>(authorityError, HttpStatus.BAD_REQUEST);
        }

        ErrorResponse badRequest = new ErrorResponse(LocalDateTime.now(), "BAD_REQUEST", HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(badRequest, HttpStatus.BAD_REQUEST);

        }

}
