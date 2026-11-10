package com.convention.banquet_system.dto;

import com.convention.banquet_system.domain.BanquetStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChangeStatusRequest {

    @NotNull
    public Long version;

    @NotNull
    public BanquetStatus status;


}
