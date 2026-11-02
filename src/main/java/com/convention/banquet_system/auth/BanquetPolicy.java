package com.convention.banquet_system.auth;

import com.convention.banquet_system.domain.Role;
import com.convention.banquet_system.exception.BusinessException;
import com.convention.banquet_system.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class BanquetPolicy {

    public void ensureCanRegister(LoginMember actor) {
        if (actor.getRole() != Role.PROMOTER) {
            throw new BusinessException(ErrorCode.BANQUET_REGISTER_FORBIDDEN);
        }
    }

    public void ensureCanModifyBanquet(LoginMember actor, Long promoterId) {
        if (actor.getRole() != Role.PROMOTER) {
            throw new BusinessException(ErrorCode.BANQUET_MODIFY_FORBIDDEN);
        }

        if (!actor.getId().equals(promoterId)) {
            throw new BusinessException(ErrorCode.BANQUET_MODIFY_FORBIDDEN);
        }
    }

}
