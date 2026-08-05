package com.convention.event_system.service;

import com.convention.event_system.domain.Banquet;
import com.convention.event_system.domain.Department;
import com.convention.event_system.domain.Member;
import com.convention.event_system.dto.BanquetCreateRequest;
import com.convention.event_system.repository.BanquetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BanquetServiceImpl implements BanquetService{

    private final BanquetRepository banquetRepository;


    @Override
    public void registerBanquet(BanquetCreateRequest request) {

        //먼저 모든 로직 전에 NPE를 방지하기 위해서 먼저 널체크
        if (request == null) {
            throw new NullPointerException("request null");
        }

        Member promoter;

        try {
            promoter = banquetRepository.findMemberById(request.getPromoterId());
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.", e);
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
        if (!promoter.getDepartment().equals(Department.Convention)) {
            //등록 사용자 권한 검증
            throw new IllegalArgumentException("판촉자가 아니면 행사를 등록할 수 없습니다.");
        }

        banquetRepository.save(new Banquet(request));

    }
}
