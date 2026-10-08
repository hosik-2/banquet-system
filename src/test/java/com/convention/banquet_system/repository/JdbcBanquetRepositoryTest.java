package com.convention.banquet_system.repository;

import com.convention.banquet_system.domain.Banquet;
import com.convention.banquet_system.domain.BanquetSchedule;
import com.convention.banquet_system.domain.Venue;
import com.convention.banquet_system.query.BanquetDetail;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class JdbcBanquetRepositoryTest {

    @Autowired
    BanquetRepository banquetRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void 단건_행사_조회_성공() {

        String sql = """
            INSERT INTO banquet (
                banquet_id, banquet_name, banquet_date, start_time, end_time, promoter_id, venue_id)
                VALUES (10, '테스트 행사10', '2026-10-10', '10:00:00', '16:00:00', 1, 1);
            """;

        jdbcTemplate.update(sql);

        BanquetDetail banquetDetail = banquetRepository.findById(10L);

        BanquetSchedule schedule = new BanquetSchedule(
                LocalDate.of(2026, 10, 10),
                LocalTime.of(10, 0, 0),
                LocalTime.of(16, 0,0));

        assertThat(banquetDetail.getBanquet().getBanquetId()).isEqualTo(10L);
        assertThat(banquetDetail.getBanquet().getBanquetName()).isEqualTo("테스트 행사10");
        assertThat(banquetDetail.getBanquet().getSchedule().getBanquetDate()).isEqualTo(schedule.getBanquetDate());
        assertThat(banquetDetail.getBanquet().getVenue()).isEqualTo(Venue.MAJESTIC_BALLROOM);

    }

    @Test
    void 행사_수정_성공() {
        String venueSelectSql = "SELECT venue_id FROM venue WHERE venue = ?";
        Long venueId = jdbcTemplate.queryForObject(venueSelectSql, Long.class, Venue.MAJESTIC_BALLROOM.name());

        String sql = """
            INSERT INTO banquet (
                banquet_id, banquet_name, banquet_date, start_time, end_time, promoter_id, venue_id)
                VALUES (10, '테스트 행사10', '2026-10-10', '10:00:00', '16:00:00', 1, ?);
            """;
        jdbcTemplate.update(sql, venueId);

        BanquetSchedule banquetSchedule = new BanquetSchedule(
                LocalDate.of(2026, 10, 10),
                LocalTime.of(10, 0),
                LocalTime.of(20, 0)
        );

        Banquet banquet = Banquet.register(
                "테스트 행사10",
                banquetSchedule,
                Venue.MAJESTIC_BALLROOM,
                null,
                1L
        );

        Integer updated = banquetRepository.update(10L, venueId, banquet, 0L);
        LocalTime endTime = jdbcTemplate.queryForObject("SELECT end_time FROM banquet WHERE banquet_id = 10", LocalTime.class);
        Long version = jdbcTemplate.queryForObject("SELECT version FROM banquet WHERE banquet_id = 10", Long.class);

        assertThat(updated).isEqualTo(1);
        assertThat(endTime).isEqualTo(LocalTime.of(20, 0));
        assertThat(version).isEqualTo(1L);
    }

    @Test
    void version_차이로_수정_거부() {
        String venueSelectSql = "SELECT venue_id FROM venue WHERE venue = ?";
        Long venueId = jdbcTemplate.queryForObject(venueSelectSql, Long.class, Venue.MAJESTIC_BALLROOM.name());
        String sql = """
            INSERT INTO banquet (
                banquet_id, banquet_name, banquet_date, start_time, end_time, promoter_id, venue_id)
                VALUES (10, '테스트 행사10', '2026-10-10', '10:00:00', '16:00:00', 1, ?);
            """;

        jdbcTemplate.update(sql, venueId);

        BanquetSchedule banquetSchedule = new BanquetSchedule(
                LocalDate.of(2026, 10, 10),
                LocalTime.of(10, 0),
                LocalTime.of(20, 0)
        );

        Banquet banquet = Banquet.register(
                "테스트 행사10",
                banquetSchedule,
                Venue.MAJESTIC_BALLROOM,
                null,
                1L
        );

        Integer updated = banquetRepository.update(10L, venueId, banquet, 1L);
        LocalTime endTime = jdbcTemplate.queryForObject("SELECT end_time FROM banquet WHERE banquet_id = 10", LocalTime.class);
        Long version = jdbcTemplate.queryForObject("SELECT version FROM banquet WHERE banquet_id = 10", Long.class);

        assertThat(updated).isEqualTo(0);
        assertThat(endTime).isEqualTo(LocalTime.of(16, 0));
        assertThat(version).isEqualTo(0L);
    }

}