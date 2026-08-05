package com.convention.event_system.controller;

import com.convention.event_system.dto.BanquetCreateRequest;
import com.convention.event_system.service.BanquetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/banquets")
public class BanquetApiController {

    private final BanquetService banquetService;

    @PostMapping
    public ResponseEntity<String> registerBanquet(@RequestBody BanquetCreateRequest dto) /*throws IllegalArgumentException*/ {
        //@RequestBody -> 없으면 URL쿼리 파라미터에서 값을 가져오려고 함 이게 있어야 JSON에서 가져온다 기억해라
        //참고로 form데이터는 @ModelAttribute다
        //ResponseEntity -> 이게 있어야 응답 정보에 데이터를 넣어 응답 메시지 작성이 가능하다 기억해라
        //throw IllegalArgumentException -> 이건 언체크예외라 컨트롤러 레벨에서 던져주지 않아도 괜찮음

        banquetService.registerBanquet(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("행사 등록 완료.");
        //201 상태코드랑 같이 메시지 반환하는 코드인데 이렇게 대충 쓰면 된다 . 찍어보자 모르겠으면

    }

}
