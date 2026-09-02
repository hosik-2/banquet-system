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

import java.util.List;

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

        banquetPolicy.ensureCanRegister(actor);

        BanquetSchedule banquetSchedule = new BanquetSchedule(
                request.getBanquetDate(),
                request.getStartTime(),
                request.getEndTime()
        );


        Venue venue = Venue.valueOf(request.getVenue());

        List<BanquetSchedule> existingSchedules =
                banquetRepository.findSchedulesByDateAndVenue(banquetSchedule.getBanquetDate(), venue);

        // 비즈니스 규칙 검사
        for (BanquetSchedule existing : existingSchedules) {
            if (existing.overlaps(banquetSchedule)) {
                throw new IllegalArgumentException("같은 날짜, 같은 베뉴, 같은 시간에 행사가 있습니다.");
            }
        }

        banquetRepository.save(Banquet.register(
                request.getBanquetName(),
                banquetSchedule,
                venue,
                request.getGuarantee(),
                actor.getId()
        ));

    }
}
