package com.convention.event_system.auth;

import com.convention.event_system.domain.Role;
import com.convention.event_system.exception.BusinessException;
import com.convention.event_system.exception.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BanquetPolicyTest {

    private BanquetPolicy banquetPolicy = new BanquetPolicy();

    @Test
    void 프로모터는_행사를_등록할_수_있다() {
        LoginMember loginMember = new LoginMember(1L, Role.PROMOTER);

        banquetPolicy.ensureCanRegister(loginMember);
    }

    @Test
    void 판촉자가_아닌_사용자가_요청시_예외() {
        LoginMember loginMember = new LoginMember(1L, Role.STAFF);

        BusinessException exception = assertThrows(
                BusinessException.class, () -> banquetPolicy.ensureCanRegister(loginMember)
        );

        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BANQUET_REGISTER_FORBIDDEN);

    }
}