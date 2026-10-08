package com.convention.banquet_system.controller;

import com.convention.banquet_system.auth.HeaderLoginMemberProvider;
import com.convention.banquet_system.auth.LoginMemberArgumentResolver;
import com.convention.banquet_system.domain.Department;
import com.convention.banquet_system.domain.Member;
import com.convention.banquet_system.domain.Role;
import com.convention.banquet_system.exception.BusinessException;
import com.convention.banquet_system.exception.ErrorCode;
import com.convention.banquet_system.repository.MemberRepository;
import com.convention.banquet_system.service.BanquetService;
import com.convention.banquet_system.service.result.BanquetUpdateResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BanquetApiController.class)
@Import({LoginMemberArgumentResolver.class, HeaderLoginMemberProvider.class})
class BanquetApiControllerTest {

    @MockitoBean // 이걸로 스프링 컨텍스트 안에 가짜 빈 등록하기
    BanquetService banquetService;

    @MockitoBean
    MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 등록_성공() throws Exception {
        //given

        String jsonRequest = """
                {
                            "banquetName" : "test1",
                            "banquetDate" : "2026-08-01",
                            "startTime" : "18:00",
                            "endTime" : "21:00",
                            "venue" : "CHAMBER_HALL"
                        }
                """;

        Member member = new Member();
        member.setMemberId(1L);
        member.setDepartment(Department.Convention);
        member.setRole(Role.PROMOTER);
        member.setMemberName("testPromoter");

        given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        given(banquetService.registerBanquet(any(), any())).willReturn(member.getMemberId());


        mockMvc.perform(post("/api/banquets")
                        .header("X-Member-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("행사 생성 완료."))
                .andExpect(jsonPath("$.banquetId").value(1L));

    }

    @Test
    void 중복_행사_등록시_409_반환() throws Exception {
        String jsonRequest = """
                {
                            "banquetName" : "test1",
                            "banquetDate" : "2026-08-01",
                            "startTime" : "18:00",
                            "endTime" : "21:00",
                            "venue" : "CHAMBER_HALL"
                        }
                """;

        doThrow(new BusinessException(ErrorCode.BANQUET_DUPLICATE))
                .when(banquetService)
                .registerBanquet(any(), any());

        Member member = new Member();
        member.setMemberId(1L);
        member.setRole(Role.PROMOTER);

        given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        mockMvc.perform(post("/api/banquets")// 요청 정보 만드는 메서드 -> perform()
                        .header("X-Member-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON) // 요청 헤더 설정(Content-type)
                        .content(jsonRequest)) // @RequestBody로 들어가는 내용 설정
                .andDo(print()) // 콘솔찍을 메서드
                .andExpect(status().isConflict()) //검증 메서드
                .andExpect(jsonPath("$.errorCode")
                        .value("BANQUET_DUPLICATE"))
                .andExpect(jsonPath("$.status")
                        .value("409 CONFLICT"));


    }

    @Test
    void 인증헤더가_없으면_401_반환() throws Exception {

        String jsonRequest = """
                {
                            "banquetName" : "test1",
                            "banquetDate" : "2026-08-01",
                            "startTime" : "18:00",
                            "endTime" : "21:00",
                            "venue" : "CHAMBER_HALL"
                        }
                """;

        mockMvc.perform(post("/api/banquets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("인증 정보가 올바르지 않습니다."))
                .andExpect(jsonPath("$.errorCode")
                        .value("AUTHENTICATION_ERROR"));

    }

    @Test
    void 존재하지_않는_회원이면_401_반환() throws Exception {
        String jsonRequest = """
                {
                            "banquetName" : "test1",
                            "banquetDate" : "2026-08-01",
                            "startTime" : "18:00",
                            "endTime" : "21:00",
                            "venue" : "CHAMBER_HALL"
                        }
                """;

        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        mockMvc.perform(post("/api/banquets")
                        .header("X-Member-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("인증 정보가 올바르지 않습니다."))
                .andExpect(jsonPath("$.errorCode")
                        .value("AUTHENTICATION_ERROR"));
    }

    @Test
    void 인증헤더에_문자열이_들어가면_401_반환() throws Exception {
        String jsonRequest = """
                {
                            "banquetName" : "test1",
                            "banquetDate" : "2026-08-01",
                            "startTime" : "18:00",
                            "endTime" : "21:00",
                            "venue" : "CHAMBER_HALL"
                        }
                """;

        mockMvc.perform(post("/api/banquets")
                        .header("X-Member-Id", "aaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("인증 정보가 올바르지 않습니다."))
                .andExpect(jsonPath("$.errorCode")
                        .value("AUTHENTICATION_ERROR"));
    }

    @Test
    void 등록_권한이_없으면_403_반환() throws Exception {
        String jsonRequest = """
                {
                            "banquetName" : "test1",
                            "banquetDate" : "2026-08-01",
                            "startTime" : "18:00",
                            "endTime" : "21:00",
                            "venue" : "CHAMBER_HALL"
                        }
                """;

        doThrow(new BusinessException(ErrorCode.BANQUET_REGISTER_FORBIDDEN))
                .when(banquetService)
                .registerBanquet(any(), any());

        Member member = new Member();
        member.setRole(Role.STAFF);
        member.setMemberId(1L);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        mockMvc.perform(post("/api/banquets")
                        .content(jsonRequest)
                        .header("X-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode")
                        .value("BANQUET_REGISTER_FORBIDDEN"))
                .andExpect(jsonPath("$.message")
                        .value("행사를 등록할 권한이 없습니다."));

    }

    @Test
    void 수정_성공시_200_반환() throws Exception {

        String jsonRequest = """
                {
                            "banquetName" : "test1",
                            "banquetDate" : "2026-08-01",
                            "startTime" : "18:00",
                            "endTime" : "21:00",
                            "venue" : "CHAMBER_HALL",
                            "version" : 0
                        }
                """;

        Member member = new Member();
        member.setMemberId(1L);
        member.setDepartment(Department.Convention);
        member.setRole(Role.PROMOTER);
        member.setMemberName("testPromoter");

        BanquetUpdateResult result = new BanquetUpdateResult(1L, 1L);

        given(banquetService.updateBanquet(any(), any(), any())).willReturn(result);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        mockMvc.perform(put("/api/banquets/1")
                        .header("X-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.banquetId")
                        .value(1L))
                .andExpect(jsonPath("$.message")
                        .value("행사 수정 완료."))
                .andExpect(jsonPath("$.version")
                        .value(1L));
    }

    @Test
    void version_다른_요청_시_409() throws Exception {
        String jsonRequest = """
                {
                            "banquetName" : "test1",
                            "banquetDate" : "2026-08-01",
                            "startTime" : "18:00",
                            "endTime" : "21:00",
                            "venue" : "CHAMBER_HALL",
                            "version" : 1
                        }
                """;

        Member member = new Member();
        member.setMemberId(1L);
        member.setDepartment(Department.Convention);
        member.setRole(Role.PROMOTER);
        member.setMemberName("testPromoter");

        given(banquetService.updateBanquet(any(), any(), any()))
                .willThrow(new BusinessException(ErrorCode.CONFLICT_MODIFIED));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        mockMvc.perform(put("/api/banquets/1")
                        .header("X-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("다른 사용자에 의해 행사가 수정되었습니다. 최신 정보를 다시 조회해 주세요."));
    }

    @Test
    void validation_검증() throws Exception {

        String jsonRequest = """
                {
                            "banquetName" : "test1",
                            "banquetDate" : "2026-08-01",
                            "startTime" : "18:00",
                            "endTime" : "21:00",
                            "version" : 0
                        }
                """;

        Member member = new Member();
        member.setMemberId(1L);
        member.setDepartment(Department.Convention);
        member.setRole(Role.PROMOTER);
        member.setMemberName("testPromoter");

        BanquetUpdateResult result = new BanquetUpdateResult(1L, 1L);

        given(banquetService.updateBanquet(any(), any(), any())).willReturn(result);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        mockMvc.perform(put("/api/banquets/1")
                        .header("X-Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

}