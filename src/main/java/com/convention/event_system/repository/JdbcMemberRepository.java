package com.convention.event_system.repository;

import com.convention.event_system.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcMemberRepository implements MemberRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Member> findById(Long memberId) {

        String sql = "SELECT * FROM MEMBER WHERE member_id = ?";
        List<Member> members = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Member.class), memberId);
        return members.stream().findFirst();

    }
}
