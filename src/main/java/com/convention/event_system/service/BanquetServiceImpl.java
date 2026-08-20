package com.convention.event_system.service;

import com.convention.event_system.auth.BanquetPolicy;
import com.convention.event_system.auth.LoginMember;
import com.convention.event_system.domain.Banquet;
import com.convention.event_system.domain.BanquetSchedule;
import com.convention.event_system.domain.Venue;
import com.convention.event_system.dto.BanquetCreateRequest;
import com.convention.event_system.repository.BanquetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BanquetServiceImpl implements BanquetService {

    private final BanquetRepository banquetRepository;
    private final BanquetPolicy banquetPolicy;


    @Override
    public void registerBanquet(BanquetCreateRequest request, LoginMember actor) {

        //먼저 모든 로직 전에 NPE를 방지하기 위해서 먼저 널체크
        if (request == null) {
            throw new NullPointerException("request null");
        }


        // 비즈니스 규칙 검사
        if (banquetRepository.existsByBanquetDateAndVenue(request.getBanquetDate(), Venue.valueOf(request.getVenue()))) {
            //같은 날짜, 같은 베뉴 행사 존재 확인 및 검증
            throw new IllegalArgumentException("같은 날짜, 같은 베뉴에 행사가 있습니다.");
        }

        banquetPolicy.ensureCanRegister(actor);

        BanquetSchedule banquetSchedule = new BanquetSchedule(
                request.getBanquetDate(),
                request.getStartTime(),
                request.getEndTime()
        );


        banquetRepository.save(Banquet.register(
                request.getBanquetName(),
                banquetSchedule,
                Venue.valueOf(request.getVenue()),
                request.getGuarantee(),
                actor.getId()
        ));

    }
}
