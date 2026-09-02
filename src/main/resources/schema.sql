CREATE TABLE banquet(
    banquet_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    banquet_name VARCHAR(255) NOT NULL,
    banquet_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    promoter_id BIGINT NOT NULL,
    in_charge_id BIGINT,
    venue VARCHAR(255) NOT NULL,
    guarantee INT

);

CREATE TABLE member(
    member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_name VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL
);

INSERT INTO member(member_name, department)
VALUES ('testPromoter', 'Convention');
INSERT INTO member(member_name, department)
VALUES ('testInCharge', 'SVC');
