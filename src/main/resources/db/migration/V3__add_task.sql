CREATE TABLE tasks
(
    id          BIGSERIAL PRIMARY KEY               NOT NULL,
    title       TEXT                                NOT NULL,
    description TEXT                                NOT NULL,
    status      TEXT                                NOT NULL,
    priority    TEXT                                NOT NULL,
    comments    TEXT,
    creator_id  BIGINT,
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (creator_id) REFERENCES users (id)
);

CREATE TABLE tasks_users
(
    task_id BIGSERIAL NOT NULL,
    user_id BIGSERIAL NOT NULL,
    PRIMARY KEY (task_id, user_id),
    FOREIGN KEY (task_id) REFERENCES tasks (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);