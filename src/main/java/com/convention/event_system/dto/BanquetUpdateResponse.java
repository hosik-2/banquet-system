package com.convention.event_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BanquetUpdateResponse {

    private Long banquetId;

    private Long version;

    private String message;

}
