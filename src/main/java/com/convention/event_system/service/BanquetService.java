package com.convention.event_system.service;

import com.convention.event_system.auth.LoginMember;
import com.convention.event_system.dto.BanquetCreateRequest;
import com.convention.event_system.dto.BanquetUpdateRequest;
import com.convention.event_system.query.BanquetDetail;
import com.convention.event_system.service.result.BanquetUpdateResult;

public interface BanquetService {

    Long registerBanquet(BanquetCreateRequest request, LoginMember actor);
    // 추후에 다른 기능 추가

    BanquetDetail getBanquetDetail(Long banquetId);

    BanquetUpdateResult updateBanquet(Long banquetId, BanquetUpdateRequest request, LoginMember actor);

}

