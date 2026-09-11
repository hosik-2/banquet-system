package com.convention.event_system.controller;

import com.convention.event_system.auth.HeaderLoginMemberProvider;
import com.convention.event_system.auth.LoginMemberArgumentResolver;
import com.convention.event_system.domain.Department;
import com.convention.event_system.domain.Member;
import com.convention.event_system.domain.Role;
import com.convention.event_system.repository.MemberRepository;
import com.convention.event_system.service.BanquetService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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


        mockMvc.perform(post("/api/banquets")
                        .header("X-Member-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("행사 등록 완료."));

    }

    @Test
    void 중복_행사_등록시_400_반환() throws Exception {
        String jsonRequest = """
                {
                            "banquetName" : "test1",
                            "banquetDate" : "2026-08-01",
                            "startTime" : "18:00",
                            "endTime" : "21:00",
                            "venue" : "CHAMBER_HALL"
                        }
                """;

        doThrow(new IllegalArgumentException("같은 날짜, 같은 베뉴에 행사가 있습니다."))
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
                .andExpect(status().isBadRequest()) //검증 메서드
                .andExpect(jsonPath("$.errorCode")
                        .value("BANQUET_DUPLICATE"))
                .andExpect(jsonPath("$.status")
                        .value("400 BAD_REQUEST"));


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

}