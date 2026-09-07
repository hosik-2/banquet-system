package com.convention.event_system.service;

import com.convention.event_system.auth.LoginMember;
import com.convention.event_system.dto.BanquetCreateRequest;

public interface BanquetService {

    void registerBanquet(BanquetCreateRequest request, LoginMember actor);
    // 추후에 다른 기능 추가

}
