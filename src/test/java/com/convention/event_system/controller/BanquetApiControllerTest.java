package com.convention.event_system.controller;

import com.convention.event_system.auth.LoginMemberArgumentResolver;
import com.convention.event_system.domain.Member;
import com.convention.event_system.domain.Role;
import com.convention.event_system.repository.BanquetRepository;
import com.convention.event_system.service.BanquetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BanquetApiController.class)
@Import(LoginMemberArgumentResolver.class)
class BanquetApiControllerTest {

    @MockitoBean // 이걸로 스프링 컨텍스트 안에 가짜 빈 등록하기
    BanquetService banquetService;

    @MockitoBean
    BanquetRepository banquetRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 등록_성공() throws Exception {
        String jsonRequest = """
                {
                            "banquetName" : "test1",
                            "banquetDate" : "2026-08-01",
                            "startTime" : "18:00",
                            "endTime" : "21:00",
                            "venue" : "Chamber Hall"
                        }
                """;

        Member member = new Member();
        member.setMemberId(1L);
        member.setRole(Role.PROMOTER);

        given(banquetRepository.findMemberById(1L))
                .willReturn(member);

        mockMvc.perform(post("/api/banquets") // 요청 정보 만드는 메서드 -> perform()
                        .header("X-Member-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON) // 요청 헤더 설정(Content-type)
                        .content(jsonRequest)) // @RequestBody로 들어가는 내용 설정
                .andDo(print()) // 콘솔찌근 메서드
                .andExpect(status().isCreated()) //검증 메서드
                .andExpect(content().string("행사 등록 완료."));

    }

}