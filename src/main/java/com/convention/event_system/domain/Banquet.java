package com.convention.event_system.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor
public class Banquet {

    private Long banquetId; // 기본형 말고 래퍼클래스 쓰기(null값 허용 여부 -> PK필드는 래퍼클래스 쓰기)

    private String banquetName;

    private LocalDate banquetDate; //Date 쓰지 않기

    private LocalTime startTime;

    private LocalTime endTime;

    private Member promoter;

    private Member inCharge;

    private String venue;

    private Integer guarantee;

}
