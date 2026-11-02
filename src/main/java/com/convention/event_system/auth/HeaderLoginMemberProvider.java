package com.convention.event_system.auth;

import com.convention.event_system.domain.Member;
import com.convention.event_system.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HeaderLoginMemberProvider implements LoginMemberProvider {

    private final MemberRepository memberRepository;

    @Override
    public LoginMember getLoginMember(NativeWebRequest request) {
        String header = request.getHeader("X-Member-Id");

        Long memberId;
        try {
            memberId = Long.parseLong(header);
        } catch (NumberFormatException e) {
            throw new UnauthenticatedException("인증 정보가 올바르지 않습니다.");
        } // 헤더가 없거나 잘못된 값이 들어갈 경우

        Optional<Member> optionalMember = memberRepository.findById(memberId);

        Member member = optionalMember.orElseThrow(
                () -> new UnauthenticatedException("인증 정보가 올바르지 않습니다.")
        ); //optionalMember가 null일 경우 인증실패 예외

        return new LoginMember(member.getMemberId(), member.getRole());
    }

}
