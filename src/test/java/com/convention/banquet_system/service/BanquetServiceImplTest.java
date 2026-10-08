package com.convention.banquet_system.service;

import com.convention.banquet_system.auth.BanquetPolicy;
import com.convention.banquet_system.auth.LoginMember;
import com.convention.banquet_system.domain.Banquet;
import com.convention.banquet_system.domain.BanquetSchedule;
import com.convention.banquet_system.domain.Role;
import com.convention.banquet_system.domain.Venue;
import com.convention.banquet_system.dto.BanquetCreateRequest;
import com.convention.banquet_system.dto.BanquetUpdateRequest;
import com.convention.banquet_system.exception.BusinessException;
import com.convention.banquet_system.exception.ErrorCode;
import com.convention.banquet_system.query.AuditInfo;
import com.convention.banquet_system.query.BanquetDetail;
import com.convention.banquet_system.repository.BanquetRepository;
import com.convention.banquet_system.repository.VenueRepository;
import com.convention.banquet_system.service.result.BanquetUpdateResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

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
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(21, 0))
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
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(21, 0))
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
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(20, 0))
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
                LocalTime.of(10, 0, 0),
                LocalTime.of(12, 0, 0)
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
    void 없는_행사_조회_시_BANQUET_NOTFOUND() {

        given(banquetRepository.findById(123L)).willThrow(EmptyResultDataAccessException.class);

        BusinessException exception = catchThrowableOfType(() ->
                banquetService.getBanquetDetail(123L), BusinessException.class);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BANQUET_NOT_FOUND);

    }

    @Test
    void 행사_정상_수정() {
        LoginMember actor = new LoginMember(10L, Role.PROMOTER);
        BanquetSchedule banquetSchedule = new BanquetSchedule(
                LocalDate.now(), LocalTime.of(10, 0),
                LocalTime.of(20, 0));

        Banquet banquet = Banquet.register(
                "test1",
                banquetSchedule,
                Venue.CHAMBER_HALL,
                null,
                10L

        );
        banquet.assignId(10L);

        BanquetUpdateRequest updateRequest = new BanquetUpdateRequest(
                "test1",
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(20, 0),
                null,
                Venue.GALLERY_HALL,
                null, 0L
        );

        AuditInfo auditInfo = new AuditInfo(
                LocalDateTime.now(),
                LocalDateTime.now(),
                10L,
                10L
        );
        BanquetDetail banquetDetail = new BanquetDetail(banquet, auditInfo);

        given(banquetRepository.findById(banquet.getBanquetId())).willReturn(banquetDetail);
        given(banquetRepository.existsOverlappingForUpdate(any(), any(), any())).willReturn(false);
        given(banquetRepository.update(any(), any(), any(), any())).willReturn(1);
        given(venueRepository.findIdForUpdate(Venue.GALLERY_HALL)).willReturn(3L);

        BanquetUpdateResult updateResult = banquetService.updateBanquet(10L, updateRequest, actor);

        assertThat(updateResult.getVersion()).isEqualTo(1L);
        assertThat(updateResult.getBanquetId()).isEqualTo(10L);
        assertThat(banquet.getVenue()).isEqualTo(Venue.GALLERY_HALL);

    }

    @Test
    void 중복_행사_있을_시_BANQUET_DUPLICATE() {
        LoginMember actor = new LoginMember(10L, Role.PROMOTER);
        BanquetSchedule banquetSchedule = new BanquetSchedule(
                LocalDate.now(), LocalTime.of(10, 0),
                LocalTime.of(20, 0));

        Banquet banquet = Banquet.register(
                "test1",
                banquetSchedule,
                Venue.CHAMBER_HALL,
                null,
                10L

        );
        banquet.assignId(10L);

        BanquetUpdateRequest updateRequest = new BanquetUpdateRequest(
                "test1",
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(20, 0),
                null,
                Venue.GALLERY_HALL,
                null, 0L
        );

        AuditInfo auditInfo = new AuditInfo(
                LocalDateTime.now(),
                LocalDateTime.now(),
                10L,
                10L
        );
        BanquetDetail banquetDetail = new BanquetDetail(banquet, auditInfo);

        given(banquetRepository.findById(banquet.getBanquetId())).willReturn(banquetDetail);
        given(banquetRepository.existsOverlappingForUpdate(any(), any(), any())).willReturn(true);
        given(venueRepository.findIdForUpdate(Venue.GALLERY_HALL)).willReturn(3L);

        BusinessException exception = catchThrowableOfType(() -> banquetService.updateBanquet(10L, updateRequest, actor), BusinessException.class);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BANQUET_DUPLICATE);
    }

    @Test
    void 인가되지_않은_회원_수정_시_BANQUET_MODIFY_FORBIDDEN() {
        LoginMember actor = new LoginMember(14L, Role.PROMOTER);
        BanquetSchedule banquetSchedule = new BanquetSchedule(
                LocalDate.now(), LocalTime.of(10, 0),
                LocalTime.of(20, 0));

        Banquet banquet = Banquet.register(
                "test1",
                banquetSchedule,
                Venue.CHAMBER_HALL,
                null,
                10L

        );
        banquet.assignId(10L);

        BanquetUpdateRequest updateRequest = new BanquetUpdateRequest(
                "test1",
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(20, 0),
                null,
                Venue.GALLERY_HALL,
                null, 0L
        );

        AuditInfo auditInfo = new AuditInfo(
                LocalDateTime.now(),
                LocalDateTime.now(),
                10L,
                10L
        );
        BanquetDetail banquetDetail = new BanquetDetail(banquet, auditInfo);

        given(banquetRepository.findById(banquet.getBanquetId())).willReturn(banquetDetail);
        willThrow(new BusinessException(ErrorCode.BANQUET_MODIFY_FORBIDDEN))
                .given(banquetPolicy)
                .ensureCanModifyBanquet(actor, 10L);

        BusinessException exception = catchThrowableOfType(() -> banquetService.updateBanquet(10L, updateRequest, actor), BusinessException.class);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BANQUET_MODIFY_FORBIDDEN);
    }

    @Test
    void 행사_수정_충돌_시_CONFLICT_MODIFIED() {
        LoginMember actor = new LoginMember(10L, Role.PROMOTER);
        BanquetSchedule banquetSchedule = new BanquetSchedule(
                LocalDate.now(), LocalTime.of(10, 0),
                LocalTime.of(20, 0));

        Banquet banquet = Banquet.register(
                "test1",
                banquetSchedule,
                Venue.CHAMBER_HALL,
                null,
                10L

        );
        banquet.assignId(10L);

        BanquetUpdateRequest updateRequest = new BanquetUpdateRequest(
                "test1",
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(20, 0),
                null,
                Venue.CHAMBER_HALL,
                null, 1L
        );

        AuditInfo auditInfo = new AuditInfo(
                LocalDateTime.now(),
                LocalDateTime.now(),
                10L,
                10L
        );
        BanquetDetail banquetDetail = new BanquetDetail(banquet, auditInfo);

        given(venueRepository.findIdForUpdate(updateRequest.getVenue())).willReturn(3L);

        given(banquetRepository.findById(10L)).willReturn(banquetDetail);
        given(banquetRepository.update(
                banquet.getBanquetId(),
                3L,
                banquet,
                updateRequest.getVersion()
        )).willReturn(0);
        given(banquetRepository.existsOverlappingForUpdate(any(), any(), any())).willReturn(false);

        BusinessException exception = catchThrowableOfType(() -> banquetService.updateBanquet(10L, updateRequest, actor), BusinessException.class);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CONFLICT_MODIFIED);
    }

}