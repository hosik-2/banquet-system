ALTER TABLE banquet
ADD CONSTRAINT fk_banquet_promoter
FOREIGN KEY (promoter_id)
REFERENCES member(member_id);

ALTER TABLE banquet
ADD CONSTRAINT fk_banquet_in_charge
FOREIGN KEY (in_charge_id)
REFERENCES member(member_id);

CREATE INDEX idx_banquet_date
ON banquet(banquet_date);

CREATE INDEX idx_banquet_venue_date
ON banquet(venue_id, banquet_date);