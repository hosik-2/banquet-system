package com.convention.event_system.auth;

import com.convention.event_system.domain.Role;
import com.convention.event_system.exception.BusinessException;
import com.convention.event_system.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class BanquetPolicy {

    public void ensureCanRegister(LoginMember actor) {
        if (actor.getRole() != Role.PROMOTER) {
            throw new BusinessException(ErrorCode.BANQUET_REGISTER_FORBIDDEN);
        }
    }

}
