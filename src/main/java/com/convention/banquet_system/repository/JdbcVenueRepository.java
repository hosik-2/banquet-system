package com.convention.banquet_system.repository;

import com.convention.banquet_system.domain.Venue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class JdbcVenueRepository implements VenueRepository{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long findIdForUpdate(Venue venue) {
        String sql = """
                SELECT venue_id FROM venue WHERE venue = ? FOR UPDATE
                """;

        return jdbcTemplate.queryForObject(sql, Long.class, venue.name());
    }
}
