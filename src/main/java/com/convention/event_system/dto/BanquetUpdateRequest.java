package com.convention.event_system.dto;

import com.convention.event_system.domain.BanquetStatus;
import com.convention.event_system.domain.Venue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BanquetUpdateRequest {

    @NotBlank
    private String banquetName;

    @NotNull
    private LocalDate banquetDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @Positive
    private Long inChargeId;

    @NotNull
    private Venue venue;

    @Positive
    private Integer guarantee;

    @NotNull
    private Long version;

}
