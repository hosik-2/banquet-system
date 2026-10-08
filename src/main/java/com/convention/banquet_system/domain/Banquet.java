package com.convention.banquet_system.domain;

import lombok.Getter;

@Getter
public class Banquet {

    private Long banquetId; // 기본형 말고 래퍼클래스 쓰기(null값 허용 여부 -> PK필드는 래퍼클래스 쓰기)

    private String banquetName;

    private BanquetSchedule schedule;

    private final Long promoterId;

    private Long inChargeId;

    private Venue venue;

    private Integer guarantee;

    private Long version;

    private BanquetStatus status;


    private Banquet(String banquetName, BanquetSchedule schedule, Venue venue, Integer guarantee, Long promoterId) {
        this.banquetName = banquetName;
        this.schedule = schedule;
        this.venue = venue;
        this.guarantee = guarantee;
        this.promoterId = promoterId;

        // 여기서 인차지 아이디는 따로 메서드 생성, 판촉자는 로그인 정보에서 주입, 방켓아이디는 DB생성
    }

    private Banquet(Long banquetId, String banquetName, BanquetSchedule schedule, Long promoterId, Long inChargeId, Venue venue, Integer guarantee, Long version, BanquetStatus status) {
        this.banquetId = banquetId;
        this.banquetName = banquetName;
        this.schedule = schedule;
        this.promoterId = promoterId;
        this.inChargeId = inChargeId;
        this.venue = venue;
        this.guarantee = guarantee;
        this.version = version;
        this.status = status;
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

    public static Banquet restore(
            Long banquetId, String banquetName, BanquetSchedule banquetSchedule,
            Long promoterId, Long inChargeId, Venue venue,
            Integer guarantee, Long version, BanquetStatus status
    ) {
        return new Banquet(
                banquetId, banquetName, banquetSchedule,
                promoterId, inChargeId, venue, guarantee, version, status);
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

    public void rename(String banquetName) {
        this.banquetName = banquetName;
    }

    public void changeVenue(Venue venue) {
        this.venue = venue;
    }

    public void assignVersion(Long version) {
        this.version = version;}

    public void cancel() {
        this.status = BanquetStatus.CANCELLED;
    }

    public void confirm() {
        this.status = BanquetStatus.CONFIRMED;
    }
}

