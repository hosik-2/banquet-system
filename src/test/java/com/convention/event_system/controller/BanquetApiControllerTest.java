package com.convention.event_system.controller;

import com.convention.event_system.auth.LoginMemberArgumentResolver;
import com.convention.event_system.domain.Member;
import com.convention.event_system.domain.Role;
import com.convention.event_system.repository.BanquetRepository;
import com.convention.event_system.dto.BanquetCreateRequest;
import com.convention.event_system.service.BanquetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    void 중복_행사_등록시_400_반환() throws Exception {
        /**
            실제 통합테스트가 컨트롤러 테스트이며, 어떤 요청이 들어오던 IllegalArgumentException("같은 날짜, 같은 베뉴에 행사가 있습니다.")을
            반환하게 만들어 행사 중복 예외를 호출하게 만듦
         **/


        //given
        // void 메서드에서 예외를 발생시키기 위해 doThrow().when() 사용
        // 리턴값이 있는 메서드는 when().thenReturn() 사용
        doThrow(new IllegalArgumentException("같은 날짜, 같은 베뉴에 행사가 있습니다."))
                .when(banquetService)
                .registerBanquet(any(BanquetCreateRequest.class));

        String jsonRequest = """
                {
                            "banquetName" : "test1",
                            "banquetDate" : "2026-08-01",
                            "startTime" : "18:00",
                            "endTime" : "21:00",
                            "promoterId" : 1,
                            "venue" : "Chamber Hall"
                        }
                """;

        //when
        mockMvc.perform(post("/api/banquets") // 요청 정보 만드는 메서드 -> perform()
                        .contentType(MediaType.APPLICATION_JSON) // 요청 헤더 설정(Content-type)
                        .content(jsonRequest)) // @RequestBody로 들어가는 내용 설정
                .andDo(print()) // 콘솔찍을 메서드
                .andExpect(status().isBadRequest()) //검증 메서드
                .andExpect(jsonPath("$.errorCode")
                        .value("BANQUET_DUPLICATE"))
                .andExpect(jsonPath("$.status")
                        .value("400 BAD_REQUEST"));


    }

}