CREATE TABLE auth
(
    token VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL ,
    valid_timestamp DATE NOT NULL
);

ALTER TABLE auth ADD CONSTRAINT auth_constrain UNIQUE (token);
