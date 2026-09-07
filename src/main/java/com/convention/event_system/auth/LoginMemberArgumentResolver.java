package com.convention.event_system.auth;

import com.convention.event_system.domain.Member;
import com.convention.event_system.repository.BanquetRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * TODO Spring Security 도입 전 X-Member-Id 헤더 값으로 임시 인증방식 도입
 * X-Member-Id 헤더에서 회원 정보를 조회 후 Controller 파라미터에 로그인멤버 객체 주입!
 */

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final BanquetRepository banquetRepository;

    public LoginMemberArgumentResolver(BanquetRepository banquetRepository) {
        this.banquetRepository = banquetRepository;
    }

    @Override
    //어떤 종류의 파라미터를 지원할 것인지 조건을 boolean값이 나오게 지정
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(com.convention.event_system.annotation.LoginMember.class) &&
                parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

        // 요청 정보에서 헤더의 값을 받고, 받은 값으로 아이디를 조회해서 LoginMember 객체로 만들어서 리턴
        String header = webRequest.getHeader("X-Member-Id");
        Long memberId = Long.parseLong(header);

        Member member = banquetRepository.findMemberById(memberId);

        return new LoginMember(member.getMemberId(), member.getRole());

    }
}
