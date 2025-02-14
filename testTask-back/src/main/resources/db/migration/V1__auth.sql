CREATE TABLE users
(
    id          BIGSERIAL PRIMARY KEY               NOT NULL,
    username    TEXT                                NOT NULL UNIQUE,
    email       TEXT                                NOT NULL UNIQUE,
    password    TEXT                                NOT NULL,
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE refresh_token
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGSERIAL,
    token       TEXT      NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);