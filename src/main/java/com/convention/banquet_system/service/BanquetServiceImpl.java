package com.convention.banquet_system.service;

import com.convention.banquet_system.auth.BanquetPolicy;
import com.convention.banquet_system.auth.LoginMember;
import com.convention.banquet_system.domain.Banquet;
import com.convention.banquet_system.domain.BanquetSchedule;
import com.convention.banquet_system.dto.BanquetCreateRequest;
import com.convention.banquet_system.dto.BanquetUpdateRequest;
import com.convention.banquet_system.exception.BusinessException;
import com.convention.banquet_system.exception.ErrorCode;
import com.convention.banquet_system.query.BanquetDetail;
import com.convention.banquet_system.repository.BanquetRepository;
import com.convention.banquet_system.repository.VenueRepository;
import com.convention.banquet_system.service.result.BanquetUpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BanquetServiceImpl implements BanquetService {

    private final BanquetRepository banquetRepository;
    private final BanquetPolicy banquetPolicy;
    private final VenueRepository venueRepository;


    @Override
    @Transactional
    public Long registerBanquet(BanquetCreateRequest request, LoginMember actor) {

        banquetPolicy.ensureCanRegister(actor);

        BanquetSchedule banquetSchedule = new BanquetSchedule(
                request.getBanquetDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        //venue
        Long venueId = venueRepository.findIdForUpdate(request.getVenue());

        if (banquetRepository.existsOverlappingForCreate(venueId, banquetSchedule)) {
            throw new BusinessException(ErrorCode.BANQUET_DUPLICATE);
        }


        Banquet banquet = banquetRepository.save(Banquet.register(
                request.getBanquetName(),
                banquetSchedule,
                request.getVenue(),
                request.getGuarantee(),
                actor.getId()
        ), venueId);

        return banquet.getBanquetId();

    }

    @Override
    @Transactional(readOnly = true)
    public BanquetDetail getBanquetDetail(Long banquetId) {

        BanquetDetail detail;
        try {
            detail = banquetRepository.findById(banquetId);
        } catch (EmptyResultDataAccessException e) {
            throw new BusinessException(ErrorCode.BANQUET_NOT_FOUND);
        }
        return detail;

    }

    @Override
    @Transactional
    public BanquetUpdateResult updateBanquet(Long banquetId, BanquetUpdateRequest request, LoginMember actor) {

        BanquetDetail banquetDetail;
        try {
            banquetDetail = banquetRepository.findById(banquetId);
        } catch (EmptyResultDataAccessException e) {
            throw new BusinessException(ErrorCode.BANQUET_NOT_FOUND);
        }

        Banquet banquet = banquetDetail.getBanquet();
        banquetPolicy.ensureCanModifyBanquet(actor, banquet.getPromoterId());

        BanquetSchedule banquetSchedule = new BanquetSchedule(
                request.getBanquetDate(),
                request.getStartTime(),
                request.getEndTime());

        Long venueId = venueRepository.findIdForUpdate(request.getVenue());

        if (banquetRepository.existsOverlappingForUpdate(banquetId, venueId, banquetSchedule)) {
            throw new BusinessException(ErrorCode.BANQUET_DUPLICATE);
        }

        banquet.rename(request.getBanquetName());
        banquet.reschedule(banquetSchedule);
        banquet.assignInCharge(request.getInChargeId());
        banquet.changeVenue(request.getVenue());
        banquet.changeGuarantee(request.getGuarantee());

        Integer updated = banquetRepository.update(banquetId, venueId, banquet, request.getVersion());
        if (updated == 0) {
            throw new BusinessException(ErrorCode.CONFLICT_MODIFIED);
        }
        return new BanquetUpdateResult(banquetId, request.getVersion() + 1);
    }
}
