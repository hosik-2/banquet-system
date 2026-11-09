package com.convention.banquet_system.domain;

import com.convention.banquet_system.exception.BusinessException;
import com.convention.banquet_system.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class BanquetTest {

    @Test
    void confirm_성공() {
        BanquetSchedule banquetSchedule = new BanquetSchedule(
                LocalDate.now().minusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0));
        Banquet banquet = Banquet.restore(
                1L, "test",
                banquetSchedule, 1L,
                null, Venue.BRISE_HALL,
                null, 1L, BanquetStatus.PENDING);

        banquet.confirm();
        BanquetStatus status = banquet.getStatus();
        assertThat(status).isEqualTo(BanquetStatus.CONFIRMED);
    }

    @Test
    void confirm_실패() {
        BanquetSchedule banquetSchedule = new BanquetSchedule(
                LocalDate.now().minusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0));
        Banquet banquet = Banquet.restore(
                1L, "test",
                banquetSchedule, 1L,
                null, Venue.BRISE_HALL,
                null, 1L, BanquetStatus.CANCELLED);

        BusinessException exception = catchThrowableOfType(() -> banquet.confirm(), BusinessException.class);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_STATUS_TRANSITION);
    }

    @Test
    void cancel_성공() {
        BanquetSchedule banquetSchedule = new BanquetSchedule(
                LocalDate.now().minusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0));
        Banquet banquet = Banquet.restore(
                1L, "test",
                banquetSchedule, 1L,
                null, Venue.BRISE_HALL,
                null, 1L, BanquetStatus.PENDING);

        Banquet banquet2 = Banquet.restore(
                1L, "test",
                banquetSchedule, 1L,
                null, Venue.BRISE_HALL,
                null, 1L, BanquetStatus.CONFIRMED);

        banquet.cancel();
        banquet2.cancel();
        BanquetStatus status = banquet.getStatus();
        BanquetStatus status2 = banquet2.getStatus();
        assertThat(status).isEqualTo(BanquetStatus.CANCELLED);
        assertThat(status2).isEqualTo(BanquetStatus.CANCELLED);
    }

    @Test
    void cancel_실패() {
        BanquetSchedule banquetSchedule = new BanquetSchedule(
                LocalDate.now().minusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0));
        Banquet banquet1 = Banquet.restore(
                1L, "test",
                banquetSchedule, 1L,
                null, Venue.BRISE_HALL,
                null, 1L, BanquetStatus.COMPLETED);
        Banquet banquet2 = Banquet.restore(
                1L, "test",
                banquetSchedule, 1L,
                null, Venue.BRISE_HALL,
                null, 1L, BanquetStatus.CANCELLED);

        BusinessException exception1 = catchThrowableOfType(() -> banquet1.cancel(), BusinessException.class);
        BusinessException exception2 = catchThrowableOfType(() -> banquet2.cancel(), BusinessException.class);

        assertThat(exception1.getErrorCode()).isEqualTo(ErrorCode.INVALID_STATUS_TRANSITION);
        assertThat(exception2.getErrorCode()).isEqualTo(ErrorCode.INVALID_STATUS_TRANSITION);

    }

    @Test
    void complete_성공() {
        BanquetSchedule banquetSchedule = new BanquetSchedule(
                LocalDate.now().minusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0));
        Banquet banquet = Banquet.restore(
                1L, "test",
                banquetSchedule, 1L,
                null, Venue.BRISE_HALL,
                null, 1L, BanquetStatus.CONFIRMED);

        banquet.complete();
        BanquetStatus status = banquet.getStatus();
        assertThat(status).isEqualTo(BanquetStatus.COMPLETED);
    }

    @Test
    void complete_실패() {
        BanquetSchedule banquetSchedule1 = new BanquetSchedule(
                LocalDate.of(2099, 11, 6),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0));
        BanquetSchedule banquetSchedule2 = new BanquetSchedule(
                LocalDate.now().minusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0));

        Banquet banquet1 = Banquet.restore(
                1L, "test",
                banquetSchedule1, 1L,
                null, Venue.BRISE_HALL,
                null, 1L, BanquetStatus.CONFIRMED);
        Banquet banquet2 = Banquet.restore(
                1L, "test",
                banquetSchedule2, 1L,
                null, Venue.BRISE_HALL,
                null, 1L, BanquetStatus.CANCELLED);
        Banquet banquet3 = Banquet.restore(
                1L, "test",
                banquetSchedule2, 1L,
                null, Venue.BRISE_HALL,
                null, 1L, BanquetStatus.PENDING);

        // 행사 종료시간이 지나지 않은 시점에 complete() 예외 발생
        BusinessException exception1 = catchThrowableOfType(() -> banquet1.complete(), BusinessException.class);
        // CANCELLED 상테에서 complete() 예외 발생
        BusinessException exception2 = catchThrowableOfType(() -> banquet2.complete(), BusinessException.class);
        // PENDING 상테에서 complete() 예외 발생
        BusinessException exception3 = catchThrowableOfType(() -> banquet3.complete(), BusinessException.class);

        assertThat(exception1.getErrorCode()).isEqualTo(ErrorCode.BANQUET_TIME_INVALID);
        assertThat(exception2.getErrorCode()).isEqualTo(ErrorCode.INVALID_STATUS_TRANSITION);
        assertThat(exception3.getErrorCode()).isEqualTo(ErrorCode.INVALID_STATUS_TRANSITION);
    }
}