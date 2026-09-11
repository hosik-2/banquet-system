package com.convention.event_system.auth;

import org.springframework.web.context.request.NativeWebRequest;

import javax.naming.AuthenticationException;

public interface LoginMemberProvider {
    LoginMember getLoginMember(NativeWebRequest request) throws AuthenticationException;

}
