    package com.convention.event_system.exception;

    import lombok.Getter;
    import org.springframework.http.HttpStatus;

    @Getter
    public enum ErrorCode {
        BANQUET_REGISTER_FORBIDDEN(HttpStatus.FORBIDDEN, "행사를 등록할 권한이 없습니다."),
        BANQUET_DUPLICATE(HttpStatus.BAD_REQUEST, "같은 날짜, 같은 베뉴, 같은 시간에 행사가 있습니다."),
        BANQUET_TIME_INVALID(HttpStatus.BAD_REQUEST, "시작시간이 종료시간보다 빨라야 합니다."),
        ;

        ErrorCode(HttpStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        private final HttpStatus status;

        private final String message;


    }
