package com.convention.banquet_system.auth;

import com.convention.banquet_system.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginMember {

    private final Long id;

    private final Role role;
}
