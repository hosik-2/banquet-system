package com.convention.event_system.controller;

import com.convention.event_system.auth.CurrentMember;
import com.convention.event_system.auth.LoginMember;
import com.convention.event_system.domain.Banquet;
import com.convention.event_system.dto.*;
import com.convention.event_system.query.BanquetDetail;
import com.convention.event_system.service.BanquetService;
import com.convention.event_system.service.result.BanquetUpdateResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/banquets")
public class BanquetApiController {

    private final BanquetService banquetService;

    @PostMapping
    public ResponseEntity<BanquetCreateResponse> registerBanquet(@Valid @RequestBody BanquetCreateRequest dto,
                                                                 @CurrentMember LoginMember actor) {

        Long banquetId = banquetService.registerBanquet(dto, actor);
        BanquetCreateResponse response = new BanquetCreateResponse(LocalDateTime.now(), banquetId, "행사 생성 완료.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
        //201 상태코드랑 같이 메시지 반환하는 코드인데 이렇게 대충 쓰면 된다 . 찍어보자 모르겠으면

    }

    @GetMapping("/{banquetId}")
    public ResponseEntity<BanquetDetailResponse> getBanquetDetail(@PathVariable Long banquetId) {
        BanquetDetail banquetDetail = banquetService.getBanquetDetail(banquetId);
        BanquetDetailResponse response = BanquetDetailResponse.from(banquetDetail, "행사 조회 완료");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{banquetId}")
    public ResponseEntity<BanquetUpdateResponse> modifyBanquet(@PathVariable Long banquetId,
                                                               @Valid @RequestBody BanquetUpdateRequest dto,
                                                               @CurrentMember LoginMember actor) {
        BanquetUpdateResult updateBanquet = banquetService.updateBanquet(banquetId, dto, actor);
        BanquetUpdateResponse response = new BanquetUpdateResponse(
                updateBanquet.getBanquetId(), updateBanquet.getVersion(), "행사 수정 완료.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}


