    package com.convention.event_system.exception;

    import lombok.Getter;
    import org.springframework.http.HttpStatus;

    @Getter
    public enum ErrorCode {
        BANQUET_REGISTER_FORBIDDEN(HttpStatus.FORBIDDEN, "행사를 등록할 권한이 없습니다.");

        ErrorCode(HttpStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        private final HttpStatus status;

        private final String message;


    }
