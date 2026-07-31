package com.convention.event_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
//@AllArgsConstructor 스프링은 JSON 글자를 자바 DTO 객체로 변환할 때 기본 생성자를 먼저 만든 뒤 값을채워 넣는 방식을 사용함
//그래서 @AllArgsConstructor 이거 하나만 쓰면 기본 생성자가 사라져서 나중에 요청을 받을 때 에러가 발생할 수 있음 그래서 @NoArgsConstructor를
//같이 쓰거나 기본 생성자만 씀
@NoArgsConstructor
public class BanquetCreateRequest {

//    private Long eventId; // 기본형 말고 래퍼클래스 쓰기(null값 허용 여부 -> PK필드는 래퍼클래스 쓰기)

    @NotBlank
    private String banquetName;

    @NotNull
    private LocalDate banquetDate; //Date 쓰지 않기

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private Long promoterId; // JSON데이터가 넘어올 때 멤버 객체 필드 전체를 받지 않을 거기 때문에 이름만 받고
    //이름으로 id를 찾는 단계를 거쳐 id를 삽입

    private Long inChargeId; //널값 관련 검증 스티커가 없어서 널이 가능 -> 추후에 추가해도 문제 없음

    @NotBlank
    private String venue;

    private Integer guarantee;

}
