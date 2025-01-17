CREATE TABLE chat_messages
(
    id        VARCHAR(36) PRIMARY KEY,
    user_id   VARCHAR(36)              NOT NULL,
    event_id  VARCHAR(36)              NOT NULL,
    message   VARCHAR(255),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE chat_messages
    ADD CONSTRAINT message_id_constrain UNIQUE (id);
