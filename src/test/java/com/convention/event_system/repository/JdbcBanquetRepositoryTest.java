package com.convention.event_system.repository;

import com.convention.event_system.domain.BanquetSchedule;
import com.convention.event_system.domain.Venue;
import com.convention.event_system.query.BanquetDetail;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

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

        Assertions.assertThat(banquetDetail.getBanquet().getBanquetId()).isEqualTo(10L);
        Assertions.assertThat(banquetDetail.getBanquet().getBanquetName()).isEqualTo("테스트 행사10");
        Assertions.assertThat(banquetDetail.getBanquet().getSchedule().getBanquetDate()).isEqualTo(schedule.getBanquetDate());
        Assertions.assertThat(banquetDetail.getBanquet().getVenue()).isEqualTo(Venue.MAJESTIC_BALLROOM);

    }

}