package com.convention.banquet_system.repository;

import com.convention.banquet_system.domain.*;
import com.convention.banquet_system.query.BanquetDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JdbcBanquetRepositoryTest {

    @Autowired
    BanquetRepository banquetRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private Long promoterId;
    private Long venueId;
    private static Long BANQUET_ID = 10L;

    @BeforeEach
    void setUp() {
        String createMember = """
                INSERT INTO member (member_name, department, role)
                VALUES ('member1', ?, ?);
                """;
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(createMember, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, Department.Convention.name());
            ps.setString(2, Role.PROMOTER.name());

            return ps;
        }, keyHolder);

        promoterId = keyHolder.getKey().longValue();

        String venueSelectSql = "SELECT venue_id FROM venue WHERE venue = ?";
        venueId = jdbcTemplate.queryForObject(venueSelectSql, Long.class, Venue.MAJESTIC_BALLROOM.name());

        String sql = """
            INSERT INTO banquet (
                banquet_id, banquet_name, banquet_date, start_time, end_time, promoter_id, venue_id)
                VALUES (10, '테스트 행사10', '2026-10-10', '10:00:00', '16:00:00', ?, ?);
            """;
        jdbcTemplate.update(sql, promoterId, venueId);
    }

    @Test
    void 취소된_행사_중복검사_제외_확인() {
        jdbcTemplate.update("""
                UPDATE banquet SET status = ? WHERE banquet_id = ?
                """, BanquetStatus.CANCELLED.name(), BANQUET_ID);

        BanquetSchedule banquetSchedule = new BanquetSchedule(LocalDate.of(2026, 10, 10),
                LocalTime.of(10, 0),
                LocalTime.of(16, 0));

        Boolean existedCreate = banquetRepository.existsOverlappingForCreate(venueId, banquetSchedule);

        BanquetStatus status = jdbcTemplate.queryForObject("""
                SELECT status FROM banquet WHERE banquet_id = ?
                """, BanquetStatus.class, BANQUET_ID);

        assertThat(existedCreate).isFalse();
        assertThat(status).isEqualTo(BanquetStatus.CANCELLED);
    }

    @Test
    void 단건_행사_조회_성공() {
        BanquetDetail banquetDetail = banquetRepository.findById(BANQUET_ID);

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

        Integer updated = banquetRepository.update(BANQUET_ID, venueId, banquet, 0L);
        LocalTime endTime = jdbcTemplate.queryForObject("SELECT end_time FROM banquet WHERE banquet_id = 10", LocalTime.class);
        Long version = jdbcTemplate.queryForObject("SELECT version FROM banquet WHERE banquet_id = 10", Long.class);

        assertThat(updated).isEqualTo(1);
        assertThat(endTime).isEqualTo(LocalTime.of(20, 0));
        assertThat(version).isEqualTo(1L);
    }

    @Test
    void 취소된_행사는_수정시_중복검사_제외() {
        BanquetSchedule banquetSchedule = new BanquetSchedule(
                LocalDate.of(2026, 10, 10),
                LocalTime.of(10, 0),
                LocalTime.of(16, 0)
        );

        jdbcTemplate.update("""
                INSERT INTO banquet (
                banquet_name, banquet_date, start_time, end_time, promoter_id, venue_id)
                VALUES ('테스트 행사20', '2026-10-11', '10:00:00', '16:00:00', ?, ?);
                """, promoterId, venueId);
        jdbcTemplate.update("UPDATE banquet SET status = 'CANCELLED' WHERE banquet_id = ?", BANQUET_ID);
        Long test20Id = jdbcTemplate.queryForObject("SELECT banquet_id FROM banquet WHERE banquet_name = '테스트 행사20'", Long.class);

        Boolean existed = banquetRepository.existsOverlappingForUpdate(test20Id, venueId, banquetSchedule);

        assertThat(existed).isFalse();
    }

    @Test
    void version_차이로_수정_거부() {
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

        Integer updated = banquetRepository.update(BANQUET_ID, venueId, banquet, 1L);
        LocalTime endTime = jdbcTemplate.queryForObject("SELECT end_time FROM banquet WHERE banquet_id = 10", LocalTime.class);
        Long version = jdbcTemplate.queryForObject("SELECT version FROM banquet WHERE banquet_id = 10", Long.class);

        assertThat(updated).isEqualTo(0);
        assertThat(endTime).isEqualTo(LocalTime.of(16, 0));
        assertThat(version).isEqualTo(0L);
    }

    @Test
    void 행사_상태_변경_성공() {
        Long banquetVersion = jdbcTemplate.queryForObject("""
                SELECT version FROM banquet WHERE banquet_id = ?
                """, Long.class, BANQUET_ID);

        Integer changed = banquetRepository.changeStatus(BANQUET_ID, BanquetStatus.CONFIRMED, banquetVersion);
        BanquetStatus status = jdbcTemplate.queryForObject("""
                SELECT status FROM banquet WHERE banquet_id = ?
                """, BanquetStatus.class, BANQUET_ID);


        assertThat(changed).isEqualTo(1);
        assertThat(status).isEqualTo(BanquetStatus.CONFIRMED);

    }

}