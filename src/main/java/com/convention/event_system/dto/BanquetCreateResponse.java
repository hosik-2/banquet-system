package com.convention.event_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BanquetCreateResponse {

    private LocalDateTime timestamp;

    private Long banquetId;

    private String message;
}
