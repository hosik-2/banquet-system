package com.convention.event_system.repository;

import com.convention.event_system.domain.Venue;

public interface VenueRepository {

    Long findIdForUpdate(Venue venue);

}
