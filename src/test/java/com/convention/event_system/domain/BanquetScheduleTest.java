package com.convention.event_system.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BanquetScheduleTest {


    @Test
    void 행사_정상_생성() {

        //given
        LocalDate localDate = LocalDate.of(2026, 9, 20);
        LocalTime startTime = LocalTime.of(12, 00);
        LocalTime endTime = LocalTime.of(18, 00);

        //when
        BanquetSchedule schedule = new BanquetSchedule(localDate, startTime, endTime);

        //then
        assertThat(schedule.getBanquetDate()).isEqualTo(localDate);
    }

    @Test
    void 같은_날짜에_다른_시간이_안_겹치면_False() {
        //given
        LocalDate localDate = LocalDate.of(2026, 9, 20);
        LocalTime startTime = LocalTime.of(12, 00);
        LocalTime endTime = LocalTime.of(18, 00);

        LocalDate otherDate = LocalDate.of(2026, 9, 20);
        LocalTime otherStart = LocalTime.of(20, 00);
        LocalTime otherEnd = LocalTime.of(21, 00);

        //when
        BanquetSchedule schedule = new BanquetSchedule(localDate, startTime, endTime);
        BanquetSchedule otherSchedule = new BanquetSchedule(otherDate, otherStart, otherEnd);

        //then
        assertThat(schedule.getBanquetDate()).isEqualTo(localDate);
        assertThat(schedule.overlaps(otherSchedule)).isFalse();

    }

    @Test
    void 같은_날짜에_같은_시간이_겹치면_True() {
        //given
        LocalDate localDate = LocalDate.of(2026, 9, 20);
        LocalTime startTime = LocalTime.of(12, 00);
        LocalTime endTime = LocalTime.of(18, 00);

        LocalDate otherDate = LocalDate.of(2026, 9, 20);
        LocalTime otherStart = LocalTime.of(17, 00);
        LocalTime otherEnd = LocalTime.of(20, 00);

        //when
        BanquetSchedule schedule = new BanquetSchedule(localDate, startTime, endTime);
        BanquetSchedule otherSchedule = new BanquetSchedule(otherDate, otherStart, otherEnd);

        //then
        assertThat(schedule.overlaps(otherSchedule)).isTrue();
    }

    @Test
    void 행사_시간_시작_종료_역전_예외() {
        //given
        LocalDate localDate = LocalDate.of(2026, 9, 20);
        LocalTime startTime = LocalTime.of(20, 00);
        LocalTime endTime = LocalTime.of(18, 00);

        //when
        //then
        assertThatThrownBy(() -> new BanquetSchedule(localDate, startTime, endTime)).hasMessage("시작시간이 종료시간보다 빨라야 합니다.");

    }

}