package com.convention.event_system.controller;

import com.convention.event_system.auth.CurrentMember;
import com.convention.event_system.auth.LoginMember;
import com.convention.event_system.dto.BanquetCreateRequest;
import com.convention.event_system.dto.BanquetCreateResponse;
import com.convention.event_system.service.BanquetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/banquets")
public class BanquetApiController {

    private final BanquetService banquetService;

    @PostMapping
    public ResponseEntity<BanquetCreateResponse> registerBanquet(@RequestBody @Valid BanquetCreateRequest dto,
                                                  @CurrentMember LoginMember actor) {


        Long banquetId = banquetService.registerBanquet(dto, actor);
        BanquetCreateResponse response = new BanquetCreateResponse(LocalDateTime.now(), banquetId, "행사 생성 완료.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
        //201 상태코드랑 같이 메시지 반환하는 코드인데 이렇게 대충 쓰면 된다 . 찍어보자 모르겠으면

    }

}
