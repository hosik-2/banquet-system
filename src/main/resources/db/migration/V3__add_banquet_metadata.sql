ALTER TABLE banquet
ADD COLUMN version BIGINT NOT NULL DEFAULT 0,
ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN created_by BIGINT,
ADD COLUMN updated_by BIGINT;

+--------------+--------------+------+-----+-------------------+-------------------+
| Field        | Type         | Null | Key | Default           | Extra             |
+--------------+--------------+------+-----+-------------------+-------------------+
| banquet_id   | bigint       | NO   | PRI | NULL              | auto_increment    |
| banquet_name | varchar(255) | NO   |     | NULL              |                   |
| banquet_date | date         | NO   | MUL | NULL              |                   |
| start_time   | time         | NO   |     | NULL              |                   |
| end_time     | time         | NO   |     | NULL              |                   |
| promoter_id  | bigint       | NO   | MUL | NULL              |                   |
| in_charge_id | bigint       | YES  | MUL | NULL              |                   |
| guarantee    | int          | YES  |     | NULL              |                   |
| venue_id     | bigint       | NO   | MUL | NULL              |                   |
| version      | bigint       | NO   |     | 0                 |                   |
| status       | varchar(50)  | NO   |     | PENDING           |                   |
| created_at   | datetime     | NO   |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
| updated_at   | datetime     | NO   |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
| created_by   | bigint       | YES  |     | NULL              |                   |
| updated_by   | bigint       | YES  |     | NULL              |                   |
+--------------+--------------+------+-----+-------------------+-------------------+