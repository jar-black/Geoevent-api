CREATE TABLE geo_stamps
(
    id          VARCHAR(36) PRIMARY KEY,
    user_id     VARCHAR(36)              NOT NULL,
    latitude    Decimal(8, 6),
    longitude   Decimal(9, 6),
    timestamp   TIMESTAMP WITH TIME ZONE NOT NULL,
    geoevent_id VARCHAR(36)              NULL
);

ALTER TABLE geo_stamps
    ADD CONSTRAINT geo_stamps_id_constrain UNIQUE (id);
