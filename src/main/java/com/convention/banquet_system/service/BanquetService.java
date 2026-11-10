package com.convention.banquet_system.service;

import com.convention.banquet_system.auth.LoginMember;
import com.convention.banquet_system.domain.BanquetStatus;
import com.convention.banquet_system.dto.BanquetCreateRequest;
import com.convention.banquet_system.dto.BanquetUpdateRequest;
import com.convention.banquet_system.dto.ChangeStatusRequest;
import com.convention.banquet_system.query.BanquetDetail;
import com.convention.banquet_system.service.result.BanquetUpdateResult;

public interface BanquetService {

    Long registerBanquet(BanquetCreateRequest request, LoginMember actor);
    // 추후에 다른 기능 추가

    BanquetDetail getBanquetDetail(Long banquetId);

    BanquetUpdateResult updateBanquet(Long banquetId, BanquetUpdateRequest request, LoginMember actor);

    Long updateBanquetStatus(Long banquetId, ChangeStatusRequest request, LoginMember actor);

}

