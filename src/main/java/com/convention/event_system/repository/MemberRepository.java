package com.convention.event_system.repository;

import com.convention.event_system.domain.Member;

import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findById(Long memberId);
}
