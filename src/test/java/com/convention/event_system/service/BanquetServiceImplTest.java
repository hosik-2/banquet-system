package com.convention.event_system.service;

import com.convention.event_system.auth.BanquetPolicy;
import com.convention.event_system.auth.LoginMember;
import com.convention.event_system.domain.BanquetSchedule;
import com.convention.event_system.domain.Role;
import com.convention.event_system.domain.Venue;
import com.convention.event_system.dto.BanquetCreateRequest;
import com.convention.event_system.repository.BanquetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BanquetServiceImplTest {

    @Mock
    private BanquetRepository banquetRepository;
    @Mock
    private BanquetPolicy banquetPolicy;
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
                .venue("CHAMBER_HALL")
                .build();

        //가짜 로그인 멤버
        LoginMember actor = new LoginMember(1L, Role.PROMOTER);

        //when
        //then
        assertDoesNotThrow(() -> banquetService.registerBanquet(request, actor));
    }

    @Test
    void 행사_중복검사_오류_실패() {
        //given
        BanquetCreateRequest request = BanquetCreateRequest.builder()
                .banquetName("test1")
                .banquetDate(LocalDate.of(2026, 8, 1))
                .startTime(LocalTime.of(18, 00))
                .endTime(LocalTime.of(21, 00))
                .venue("CHAMBER_HALL")
                .build();

        //가짜 로그인 멤버
        LoginMember actor = new LoginMember(1L, Role.PROMOTER);

        //when
        BanquetSchedule existingSchedule = new BanquetSchedule(
                LocalDate.of(2026, 8, 1),
                LocalTime.of(18, 00),
                LocalTime.of(21, 00)
        );
        given(banquetRepository.findSchedulesByDateAndVenue(any(), any())).willReturn(List.of(existingSchedule));

        //then
        assertThatThrownBy(() -> {
            banquetService.registerBanquet(request, actor);
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void 권한_검증_오류() {
        //given
        BanquetCreateRequest request = BanquetCreateRequest.builder()
                .banquetName("test1")
                .banquetDate(LocalDate.of(2026, 8, 1))
                .startTime(LocalTime.of(18, 00))
                .endTime(LocalTime.of(20, 00))
                .venue("CHAMBER_HALL")
                .build();

        //가짜 로그인 멤버
        //스테프일 시 예외 발생
        LoginMember actor = new LoginMember(1L, Role.STAFF);

        //when
        willThrow(new IllegalArgumentException("판촉자가 아니면 행사를 등록할 수 없습니다."))
                .given(banquetPolicy)
                .ensureCanRegister(actor);

        //then
        assertThatThrownBy(() -> {
            banquetService.registerBanquet(request, actor);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("판촉자가 아니면 행사를 등록할 수 없습니다.");
    }
}