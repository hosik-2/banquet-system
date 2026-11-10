    package com.convention.banquet_system.exception;

    import lombok.Getter;
    import org.springframework.http.HttpStatus;

    @Getter
    public enum ErrorCode {
        BANQUET_REGISTER_FORBIDDEN(HttpStatus.FORBIDDEN, "행사를 등록할 권한이 없습니다."),
        BANQUET_MODIFY_FORBIDDEN(HttpStatus.FORBIDDEN, "행사를 수정할 권한이 없습니다."),
        BANQUET_DUPLICATE(HttpStatus.CONFLICT, "같은 날짜, 같은 베뉴, 같은 시간에 행사가 있습니다."),
        BANQUET_TIME_INVALID(HttpStatus.BAD_REQUEST, "시작시간이 종료시간보다 빨라야 합니다."),
        CONFLICT_MODIFIED(HttpStatus.CONFLICT, "다른 사용자에 의해 행사가 수정되었습니다. 최신 정보를 다시 조회해 주세요."),
        BANQUET_NOT_FOUND(HttpStatus.NOT_FOUND, "행사를 찾을 수 없습니다."),
        BANQUET_CHANGE_STATUS_FORBIDDEN(HttpStatus.FORBIDDEN, "상태를 변경할 권한이 없습니다."),
        NOT_FINISHED(HttpStatus.BAD_REQUEST, "행사 종료시간이 지나지 않았습니다."),
        INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "허용되지 않은 상태 변경입니다.");

        ErrorCode(HttpStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        private final HttpStatus status;

        private final String message;


    }
