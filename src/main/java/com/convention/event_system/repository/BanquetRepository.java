package com.convention.event_system.repository;

import com.convention.event_system.domain.Banquet;
import com.convention.event_system.domain.BanquetSchedule;
import com.convention.event_system.query.BanquetDetail;

//Repository는 DTO가 아닌 도메인(엔티티)를 주고받는 객체임 명심하셈
public interface BanquetRepository {

    Banquet save(Banquet banquet, Long venueId); // 추후 JPA 전환 가능성 + 서비스 레벨에서 행사 전체 필드를 쉽게 다루려면 반환값을
    //Event로 하는 것도 나쁘지 않음 그래서 Long이 아닌 Event로 하겠음

    //boolean existsByBanquetDateAndVenue(LocalDate banquetDate, Venue venue);
    // 행사 등록 시 중복 검사를 위한 검증 메서드

    Boolean existsOverlappingForCreate(Long venueId, BanquetSchedule banquetSchedule);

    Boolean existsOverlappingForUpdate(Long banquetId, Long venueId, BanquetSchedule banquetSchedule);

    Integer update(Long banquetId, Long venueId, Banquet banquet, Long version);

    BanquetDetail findById(Long banquetId);

}
