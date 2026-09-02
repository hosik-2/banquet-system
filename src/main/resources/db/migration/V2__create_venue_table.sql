CREATE TABLE venue(
    venue VARCHAR(255) NOT NULL UNIQUE,
    venue_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY
);

INSERT INTO venue (venue)
VALUES
    ('MAJESTIC_BALLROOM'),
    ('BRISE_HALL'),
    ('GALLERY_HALL'),
    ('CAFE_TERRACE'),
    ('LEBEN_HALL'),
    ('CHAMBER_HALL');

ALTER TABLE banquet
ADD COLUMN venue_id BIGINT;

UPDATE banquet b
JOIN venue v
ON b.venue = v.venue
SET b.venue_id = v.venue_id;

ALTER TABLE banquet
MODIFY COLUMN venue_id BIGINT NOT NULL;

ALTER TABLE banquet
ADD CONSTRAINT fk_banquet_venue
FOREIGN KEY (venue_id)
REFERENCES venue(venue_id);

ALTER TABLE banquet
DROP COLUMN venue;