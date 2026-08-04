package com.convention.event_system.service;

import com.convention.event_system.domain.Department;
import com.convention.event_system.domain.Member;
import com.convention.event_system.dto.BanquetCreateRequest;
import com.convention.event_system.repository.BanquetRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BanquetServiceImplTest {

    @Mock
    private BanquetRepository banquetRepository;
    @InjectMocks
    private BanquetServiceImpl banquetService;

    @Test
    void 행사_등록_성공() throws IllegalAccessException {
        //given
        BanquetCreateRequest request = BanquetCreateRequest.builder()
                .banquetName("test1")
                .banquetDate(LocalDate.of(2026, 8, 1))
                .startTime(LocalTime.of(18, 00))
                .endTime(LocalTime.of(21, 00))
                .promoterId(1L)
                .venue("Chamber Hall")
                .build();

        //가짜 멤버 구현
        Member member = new Member();
        member.setMemberId(1L);
        member.setDepartment(Department.Convention);
        member.setMemberName("testMember");

        //리포지토리가 멤버아이디가 1인 사람을 조회하면 내가 위에 저장한 멤버를 리턴해라
        given(banquetRepository.findMemberById(1L)).willReturn(member);

        //중복 검사를 실행하면 어떤 값이 들어오던 false를 리턴해라
        given(banquetRepository.existsByBanquetDateAndVenue(any(), any())).willReturn(false);
        //when
        //then
        assertDoesNotThrow(() -> banquetService.registerBanquet(request));
    }

    @Test
    void 행사_중복검사_오류_실패() {
        //given
        BanquetCreateRequest request = BanquetCreateRequest.builder()
                .banquetName("test1")
                .banquetDate(LocalDate.of(2026, 8, 1))
                .startTime(LocalTime.of(18, 00))
                .endTime(LocalTime.of(21, 00))
                .promoterId(1L)
                .venue("Chamber Hall")
                .build();

        //가짜 멤버 구현
        Member member = new Member();
        member.setMemberId(1L);
        member.setDepartment(Department.Convention);
        member.setMemberName("testMember");

        given(banquetRepository.findMemberById(1L)).willReturn(member);
        //이미 있는 값이라고 치고 중복 검사 메서드가 작동하는지 확인
        given(banquetRepository.existsByBanquetDateAndVenue(LocalDate.of(2026, 8, 1), "Chamber Hall")).willReturn(true);

        //when
        //then
        assertThatThrownBy(() -> {
            banquetService.registerBanquet(request);
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void 시작시간_설정_오류() {
        //given
        BanquetCreateRequest request = BanquetCreateRequest.builder()
                .banquetName("test1")
                .banquetDate(LocalDate.of(2026, 8, 1))
                .startTime(LocalTime.of(18, 00))
                .endTime(LocalTime.of(12, 00))
                .promoterId(1L)
                .venue("Chamber Hall")
                .build();

        //가짜 멤버 구현

        Member member = new Member();
        member.setMemberId(1L);
        member.setDepartment(Department.Convention);
        member.setMemberName("testMember");

        //when
        //then
        assertThatThrownBy(() -> {
            banquetService.registerBanquet(request);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작시간이 종료시간보다 늦습니다.");

    }

    @Test
    void 권한_검증_오류() {
        //given
        BanquetCreateRequest request = BanquetCreateRequest.builder()
                .banquetName("test1")
                .banquetDate(LocalDate.of(2026, 8, 1))
                .startTime(LocalTime.of(18, 00))
                .endTime(LocalTime.of(20, 00))
                .promoterId(1L)
                .venue("Chamber Hall")
                .build();

        //가짜 멤버 구현

        Member member = new Member();
        member.setMemberId(1L);
        member.setDepartment(Department.SVC);
        member.setMemberName("testMember");

        given(banquetRepository.findMemberById(1L)).willReturn(member);

        //when
        //then
        assertThatThrownBy(() -> {
            banquetService.registerBanquet(request);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("판촉자가 아니면 행사를 등록할 수 없습니다.");
    }
}