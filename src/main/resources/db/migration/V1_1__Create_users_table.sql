CREATE TABLE users
(
    id            VARCHAR(36) PRIMARY KEY,
    name          VARCHAR(30) NOT NULL,
    phone         VARCHAR(30) NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    validated     BOOLEAN     NOT NULL DEFAULT FALSE
);

ALTER TABLE users
    ADD CONSTRAINT users_constrain UNIQUE (phone);
