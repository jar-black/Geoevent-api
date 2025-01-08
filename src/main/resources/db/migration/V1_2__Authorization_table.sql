CREATE TABLE authorization
(
    token VARCHAR(36) PRIMARY KEY,
    userId VARCHAR(36),
    valid_timestamp DATE NOT NULL
);

ALTER TABLE authorization ADD CONSTRAINT constraint_name UNIQUE (token);
