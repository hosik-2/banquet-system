package com.convention.event_system.auth;

import com.convention.event_system.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginMember {

    private final Long id;

    private final Role role;
}
