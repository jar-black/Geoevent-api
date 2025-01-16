CREATE TABLE geo_events
(
    id                  VARCHAR(36) PRIMARY KEY,
    user_id             VARCHAR(36)              NOT NULL,
    latitude            Decimal(8, 6),
    longitude           Decimal(9, 6),
    description         VARCHAR(255),
    radius_meter        NUMERIC,
    time_before_minutes NUMERIC                  NOT NULL,
    time_after_minutes  NUMERIC                  NOT NULL,
    timestamp           TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE geo_events
    ADD CONSTRAINT geo_events_id_constrain UNIQUE (id);
