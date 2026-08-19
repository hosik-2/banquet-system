package com.convention.event_system.service;

import com.convention.event_system.auth.BanquetPolicy;
import com.convention.event_system.auth.LoginMember;
import com.convention.event_system.domain.Banquet;
import com.convention.event_system.domain.Role;
import com.convention.event_system.dto.BanquetCreateRequest;
import com.convention.event_system.repository.BanquetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BanquetServiceImpl implements BanquetService{

    private final BanquetRepository banquetRepository;
    private final BanquetPolicy banquetPolicy;


    @Override
    public void registerBanquet(BanquetCreateRequest request, LoginMember actor) {

        //먼저 모든 로직 전에 NPE를 방지하기 위해서 먼저 널체크
        if (request == null) {
            throw new NullPointerException("request null");
        }


        // 비즈니스 규칙 검사
        if (banquetRepository.existsByBanquetDateAndVenue(request.getBanquetDate(), request.getVenue())) {
            //같은 날짜, 같은 베뉴 행사 존재 확인 및 검증
            throw new IllegalArgumentException("같은 날짜, 같은 베뉴에 행사가 있습니다.");
        }
        if (!(request.getStartTime().isBefore(request.getEndTime()))) {
            //행사 시작시간 < 종료시간 검증
            throw new IllegalArgumentException("시작시간이 종료시간보다 늦습니다.");
        }

        banquetPolicy.ensureCanRegister(actor);



        banquetRepository.save(new Banquet(request, actor.getId()));

    }
}
