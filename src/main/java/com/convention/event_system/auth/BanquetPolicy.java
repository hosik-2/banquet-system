package com.convention.event_system.auth;

import com.convention.event_system.domain.Role;
import org.springframework.stereotype.Component;

@Component
public class BanquetPolicy {

    public void ensureCanRegister(LoginMember actor) {
        if (actor.getRole() != Role.PROMOTER) {
            throw new IllegalArgumentException("판촉자가 아니면 행사를 등록할 수 없습니다.");
        }
    }

}
