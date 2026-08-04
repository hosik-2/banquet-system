package com.convention.event_system.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class Member {

    private Long memberId; // 래퍼클래스 쓰기(PK)

    private String memberName;

    private Department department;

}
