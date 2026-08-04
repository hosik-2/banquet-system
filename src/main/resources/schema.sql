CREATE TABLE banquet(
    banquet_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    banquet_name VARCHAR(255) NOT NULL,
    banquet_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    promoter_id BIGINT NOT NULL,
    in_charge_id BIGINT,
    -- 판촉자랑 인차지는 Member 객체 전체를 넣을 수 없기 때문에 id만 넣어서 나중에 Member 엔티티에서 꺼내 오는 형식을 써야함
    venue VARCHAR(255) NOT NULL,
    guarantee INT

);

CREATE TABLE member(
    member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_name VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL
);

INSERT INTO member(member_id, member_name, department)
VALUES (1, "testPromoter", "Convention");
