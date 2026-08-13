package com.convention.event_system.auth;

import com.convention.event_system.auth.annotation.LoginMember;
import com.convention.event_system.domain.Member;
import com.convention.event_system.repository.BanquetRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final BanquetRepository banquetRepository;

    public LoginMemberArgumentResolver(BanquetRepository banquetRepository) {
        this.banquetRepository = banquetRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class) &&
                parameter.getParameterType().equals(com.convention.event_system.auth.LoginMember.class);
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

        String header = webRequest.getHeader("X-Member-Id");
        Long memberId = Long.parseLong(header);

        Member member = banquetRepository.findMemberById(memberId);

        return new com.convention.event_system.auth.LoginMember(member.getMemberId(), member.getRole());

    }
}
