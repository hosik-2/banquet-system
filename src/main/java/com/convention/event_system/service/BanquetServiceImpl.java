package com.convention.event_system.service;

import com.convention.event_system.auth.BanquetPolicy;
import com.convention.event_system.auth.LoginMember;
import com.convention.event_system.domain.Banquet;
import com.convention.event_system.domain.BanquetSchedule;
import com.convention.event_system.domain.Venue;
import com.convention.event_system.dto.BanquetCreateRequest;
import com.convention.event_system.exception.BusinessException;
import com.convention.event_system.exception.ErrorCode;
import com.convention.event_system.repository.BanquetRepository;
import com.convention.event_system.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


        Venue venue = Venue.valueOf(request.getVenue());

        //venue
        Long venueId = venueRepository.findIdForUpdate(venue);

        if (banquetRepository.existsOverlapping(venueId, banquetSchedule)) {
            throw new BusinessException(ErrorCode.BANQUET_DUPLICATE);
        }


        Banquet banquet = banquetRepository.save(Banquet.register(
                request.getBanquetName(),
                banquetSchedule,
                venue,
                request.getGuarantee(),
                actor.getId()
        ), venueId);

        return banquet.getBanquetId();

    }
}
