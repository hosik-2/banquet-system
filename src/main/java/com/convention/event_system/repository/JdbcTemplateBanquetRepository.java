package com.convention.event_system.repository;

import com.convention.event_system.domain.Banquet;
import com.convention.event_system.domain.BanquetSchedule;
import com.convention.event_system.domain.Member;
import com.convention.event_system.domain.Venue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class JdbcTemplateBanquetRepository implements BanquetRepository {

    private final JdbcTemplate jdbcTemplate; // DB조작 도구임 주입 받고 쓰면 됌

    @Override
    public Banquet save(Banquet banquet) {
//        커넥션 취득이나 나머지 자원 반환도 jdbcTemplate에서 알아서 해줌 그래서 템플릿을 주입 받아서 update(), query()
//        같은 메서드로 커넥션 풀이나 다른 거 고민할 필요 없이 알아서 다 해주니 우리는 람다식에 커넥션이랑 자동증가값이 있다면
//        그 인자만 잘 넘겨주고 preparedStatement 세팅만 잘 해줘서 람다식 안에서 그걸 리턴해주고 객체를 잘 넘기면 됌 ㅇㅋ?

        KeyHolder keyHolder = new GeneratedKeyHolder(); //DB에서 자동으로 만든 id값을 받아올 변수

        String sql = """
                INSERT INTO banquet (banquet_name, banquet_date, start_time, end_time, promoter_id, in_charge_id,
                venue, guarantee) VALUES (?, ?, ?, ?, ?, ?, ?, ?)""";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //Statement.RETURN_GENERATED_KEYS 이게 DB에서 만들어주는 자동 증가 키값을 받아오겠다는 표시임 안그럼 에러남

            ps.setString(1, banquet.getBanquetName());
            ps.setObject(2, banquet.getSchedule().getBanquetDate());
            ps.setObject(3, banquet.getSchedule().getStartTime());
            ps.setObject(4, banquet.getSchedule().getEndTime());
            ps.setLong(5, banquet.getPromoterId());
            ps.setObject(6, banquet.getInChargeId() != null ?
                    banquet.getInChargeId() : null); // 위처럼 하면 널값이 들어오면 NPE터짐
            ps.setString(7, banquet.getVenue().name());
            ps.setObject(8, banquet.getGuarantee() != null ?
                    banquet.getGuarantee() : null);


            return ps;
        }, keyHolder); // DB에서 넘겨줄 키값을 받을 키홀더도 update() 안에 선언해줘야 함

        long generatedId = keyHolder.getKey().longValue();
        banquet.assignId(generatedId);
        // DB에는 자동으로 id값이 선언되어 다른 쿼리문이랑 같이 한 번에 저장이 되기 때문에 이 작업은 update() 안에서 할 필요가 없음
        // 이 작업은 우리가 도메인에(자바에) 저장할 id값만 불러오는 작업이니까

        return banquet;

    }

    @Override //행사 중복 여부를 위해 중복 날짜객체 반환
    public List<BanquetSchedule> findSchedulesByDateAndVenue(LocalDate banquetDate, Venue venue) {
        String sql = """
                SELECT * FROM BANQUET WHERE banquet_date = ? AND venue = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        new BanquetSchedule(
                                rs.getDate("banquet_date").toLocalDate(),
                                rs.getTime("start_time").toLocalTime(),
                                rs.getTime("end_time").toLocalTime()
                        ),
                banquetDate, venue);

    }


//    @Override
//    public boolean existsByBanquetDateAndVenue(LocalDate banquetDate, Venue venue) {
//        //중복 검사를 위한 조회 메서드
//        String sql = """
//                SELECT COUNT(*) FROM BANQUET WHERE banquet_date = ? AND venue = ?
//                """;
//
//        Integer countForQuery = jdbcTemplate.queryForObject(sql, Integer.class, banquetDate, venue);
//        boolean result;
//        if (countForQuery > 0) result = true;
//        else result = false;
//
//        return result;
//    }

    @Override
    public Member findMemberById(Long memberId) {
        String sql = "SELECT * FROM MEMBER WHERE member_id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Member.class), memberId);
    }


}
