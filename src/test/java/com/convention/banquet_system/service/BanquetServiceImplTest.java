package com.convention.banquet_system.service;

import com.convention.banquet_system.auth.BanquetPolicy;
import com.convention.banquet_system.auth.LoginMember;
import com.convention.banquet_system.domain.Banquet;
import com.convention.banquet_system.domain.BanquetSchedule;
import com.convention.banquet_system.domain.Role;
import com.convention.banquet_system.domain.Venue;
import com.convention.banquet_system.dto.BanquetCreateRequest;
import com.convention.banquet_system.exception.BusinessException;
import com.convention.banquet_system.exception.ErrorCode;
import com.convention.banquet_system.query.AuditInfo;
import com.convention.banquet_system.query.BanquetDetail;
import com.convention.banquet_system.repository.BanquetRepository;
import com.convention.banquet_system.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BanquetServiceImplTest {

    @Mock
    private BanquetRepository banquetRepository;
    @Mock
    private BanquetPolicy banquetPolicy;
    @Mock
    private VenueRepository venueRepository;
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
                .venue(Venue.valueOf("CHAMBER_HALL"))
                .build();

        BanquetSchedule banquetSchedule = new BanquetSchedule(request.getBanquetDate(), request.getStartTime(), request.getEndTime());

        Banquet banquet = Banquet.register(request.getBanquetName(), banquetSchedule, request.getVenue(), null, 1L);
        banquet.assignId(1L);

        //가짜 로그인 멤버
        LoginMember actor = new LoginMember(1L, Role.PROMOTER);
        given(banquetRepository.save(any(), any())).willReturn(banquet);

        //when
        //then
        assertThat(banquetService.registerBanquet(request, actor)).isEqualTo(1L);
    }

    @Test
    void 행사_중복검사_오류_실패() {
        //given
        BanquetCreateRequest request = BanquetCreateRequest.builder()
                .banquetName("test1")
                .banquetDate(LocalDate.of(2026, 8, 1))
                .startTime(LocalTime.of(18, 00))
                .endTime(LocalTime.of(21, 00))
                .venue(Venue.valueOf("CHAMBER_HALL"))
                .build();

        //가짜 로그인 멤버
        LoginMember actor = new LoginMember(1L, Role.PROMOTER);

        given(venueRepository.findIdForUpdate(any(Venue.class))).willReturn(1L);

        given(banquetRepository.existsOverlappingForCreate(eq(1L), any(BanquetSchedule.class)))
                .willReturn(true);

        BusinessException exception = catchThrowableOfType(
                () -> banquetService.registerBanquet(request, actor),
                BusinessException.class
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BANQUET_DUPLICATE);

    }

    @Test
    void 권한_검증_오류() {
        //given
        BanquetCreateRequest request = BanquetCreateRequest.builder()
                .banquetName("test1")
                .banquetDate(LocalDate.of(2026, 8, 1))
                .startTime(LocalTime.of(18, 00))
                .endTime(LocalTime.of(20, 00))
                .venue(Venue.valueOf("CHAMBER_HALL"))
                .build();

        //가짜 로그인 멤버
        //스테프일 시 예외 발생
        LoginMember actor = new LoginMember(1L, Role.STAFF);

        //when
        willThrow(new BusinessException(ErrorCode.BANQUET_REGISTER_FORBIDDEN))
                .given(banquetPolicy)
                .ensureCanRegister(actor);

        //then
        assertThatThrownBy(() -> {
            banquetService.registerBanquet(request, actor);
        }).isInstanceOf(BusinessException.class)
                .hasMessage("행사를 등록할 권한이 없습니다.");
    }

    @Test
    void 행사_단건_조회_성공() {

        BanquetSchedule schedule = new BanquetSchedule(
                LocalDate.of(2026, 10, 10),
                LocalTime.of(10, 00, 00),
                LocalTime.of(12, 00, 00)
        );

        Banquet banquet = Banquet.register(
                "test1111",
                schedule,
                Venue.CAFE_TERRACE,
                30,
                1L
        );

        AuditInfo auditInfo = new AuditInfo(
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                1L
        );

        BanquetDetail expect = new BanquetDetail(banquet, auditInfo);

        given(banquetRepository.findById(1L)).willReturn(expect);

        BanquetDetail result = banquetService.getBanquetDetail(1L);

        assertThat(result).isSameAs(expect);

        assertThat(result.getBanquet().getVenue()).isEqualTo(Venue.CAFE_TERRACE);
        assertThat(result.getAuditInfo().getCreatedBy()).isEqualTo(1L);
        assertThat(result.getBanquet().getSchedule().getBanquetDate()).isEqualTo(LocalDate.of(2026, 10, 10));
    }

    @Test
    void 없는_행사_조회_시_404() {

        given(banquetRepository.findById(123L)).willThrow(EmptyResultDataAccessException.class);

        BusinessException exception = catchThrowableOfType(() ->
                banquetService.getBanquetDetail(123L), BusinessException.class);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BANQUET_NOT_FOUND);

    }

}