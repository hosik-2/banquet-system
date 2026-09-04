# Database design

## venue  분리

- 기존에는 banquet 안에 venue를 문자열로 저장했다.
- venue 테이블 생성 및 기존 banquet.venue값을 기준으로 venue_id로 마이그레이션
- 이후 banquet.venue_id 가 venue.venue_id 를 참조하도록 FK를 설정했다.
- 이로써, 참조 무결성 보장, row lock 포인트로 활용 가능하게 했다.

## banquet 테이블 설계

- promoter_id, in_charge_id, venue_id 컬럼을 FK를 설정하여 존재하지 않는 데이터를 참조할 수 없게 설정했다.
- 추후 낙관적 락을 위한 version 컬럼을 생성했다.
- created_at, updated_at, created_by, updated_by을 추가하여 생성 및 수정 정보를 기록할 수 있도록 했다.
- status 컬럼을 추가하고, PENDING, CONFIRMED, CANCELLED 상태를 사용한다.
- 인덱스 추가 -> banquet_date: 월별 행사 조회, (venue_id, banquet_date): 행사 중복 검사용

## 행사 중복 정책

- UNIQUE (venue_id, banquet_date) 를 사용하게 되면 비즈니스 규칙인
같은 베뉴와 같은 날짜라도 시간이 겹치지 않으면 행사를 등록할 수 있다 라는 규칙에 어긋나게 된다.
- 시간 검사를 하기 전 DB에서 중복으로 처리하여 등록이 불가능하다.
- 따라서 UNIQUE 제약이 아닌 venue_id 에 먼저 FOR UPDATE를 걸어 (비관적 락)
venue -> banquet 순으로 직렬화 시켜 데드락을 방지한다.
