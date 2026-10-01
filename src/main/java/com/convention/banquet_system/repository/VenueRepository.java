package com.convention.banquet_system.repository;

import com.convention.banquet_system.domain.Venue;

public interface VenueRepository {

    Long findIdForUpdate(Venue venue);

}
