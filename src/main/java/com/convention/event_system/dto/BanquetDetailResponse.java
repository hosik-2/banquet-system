package com.convention.event_system.dto;

import com.convention.event_system.domain.Banquet;
import com.convention.event_system.domain.BanquetSchedule;
import com.convention.event_system.domain.Venue;
import com.convention.event_system.query.BanquetDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BanquetDetailResponse {

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    private Long updatedBy;

    private Long banquetId;

    private String banquetName;

    private BanquetSchedule schedule;

    private Long promoterId;

    private Long inChargeId;

    private Venue venue;

    private Integer guarantee;

    private Long version;

    private String message;

    public static BanquetDetailResponse from(BanquetDetail detail, String message) {
        return new BanquetDetailResponse(
                detail.getAuditInfo().getCreatedAt(),
                detail.getAuditInfo().getUpdatedAt(),
                detail.getAuditInfo().getCreatedBy(),
                detail.getAuditInfo().getUpdatedBy(),
                detail.getBanquet().getBanquetId(),
                detail.getBanquet().getBanquetName(),
                detail.getBanquet().getSchedule(),
                detail.getBanquet().getPromoterId(),
                detail.getBanquet().getInChargeId(),
                detail.getBanquet().getVenue(),
                detail.getBanquet().getGuarantee(),
                detail.getBanquet().getVersion(),
                message
        );
    }

}
