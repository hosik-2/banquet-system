package com.convention.event_system.auth;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Spring Security 도입 전 헤더 값으로 임시 인증방식 도입
 */

@Component
@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final LoginMemberProvider loginMemberProvider;

    @Override
    //어떤 종류의 파라미터를 지원할 것인지 조건을 boolean값이 나오게 지정
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(com.convention.event_system.annotation.LoginMember.class) &&
                parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

        return loginMemberProvider.getLoginMember(webRequest);

    }
}
