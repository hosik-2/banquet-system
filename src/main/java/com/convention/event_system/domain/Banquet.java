package com.convention.event_system.domain;

import lombok.Getter;

@Getter
public class Banquet {

    private Long banquetId; // 기본형 말고 래퍼클래스 쓰기(null값 허용 여부 -> PK필드는 래퍼클래스 쓰기)

    private final String banquetName;

    private BanquetSchedule schedule;

    private final Long promoterId;

    private Long inChargeId;

    private final Venue venue; //TODO: to Enum

    private Integer guarantee;

    private Banquet(String banquetName, BanquetSchedule schedule, Venue venue, Integer guarantee, Long promoterId) {
        this.banquetName = banquetName;
        this.schedule = schedule;
        this.venue = venue;
        this.guarantee = guarantee;
        this.promoterId = promoterId;

        // 여기서 인차지 아이디는 따로 메서드 생성, 판촉자는 로그인 정보에서 주입, 방켓아이디는 DB생성
    }

    public static Banquet register(String banquetName, BanquetSchedule schedule, Venue venue, Integer guarantee, Long promoterId) {
        return new Banquet(
                banquetName,
                schedule,
                venue,
                guarantee,
                promoterId
        );
    }

    public void assignId(Long banquetId) {
        this.banquetId = banquetId;
    }

    public void reschedule(BanquetSchedule schedule) {
        this.schedule = schedule;
    }

    public void assignInCharge(Long inChargeId) {
        this.inChargeId = inChargeId;
    }

    public void changeGuarantee(Integer guarantee) {
        this.guarantee = guarantee;
    }
}

