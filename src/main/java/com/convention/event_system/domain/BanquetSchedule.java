package com.convention.event_system.domain;

import com.convention.event_system.exception.BusinessException;
import com.convention.event_system.exception.ErrorCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class BanquetSchedule {

    private final LocalDate banquetDate;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public BanquetSchedule(LocalDate banquetDate, LocalTime startTime, LocalTime endTime) {

        if (!startTime.isBefore(endTime)) {
            throw new BusinessException(ErrorCode.BANQUET_TIME_INVALID);
        }

        this.banquetDate = banquetDate;
        this.startTime = startTime;
        this.endTime = endTime;

    }


    public boolean overlaps(BanquetSchedule other) {
        if (other.getBanquetDate().equals(banquetDate)) {
            if (endTime.isAfter(other.startTime) && startTime.isBefore(other.endTime)) {
                return true;
            }
        }

        return false;
    }
}
