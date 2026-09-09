CREATE TABLE member(
                       member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       member_name VARCHAR(255) NOT NULL,
                       department VARCHAR(255) NOT NULL,
                       role VARCHAR(255) NOT NULL
);

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