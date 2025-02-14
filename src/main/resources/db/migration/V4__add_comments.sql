ALTER TABLE tasks DROP COLUMN comments;

CREATE TABLE comments
(
    id             BIGSERIAL PRIMARY KEY               NOT NULL,
    text           TEXT                                NOT NULL,
    commentator_id bigint,
    create_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (commentator_id) references users(id)
);

CREATE TABLE tasks_comments
(
    task_id BIGSERIAL,
    comment_id BIGSERIAL,
    PRIMARY KEY (task_id, comment_id),
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (comment_id) REFERENCES comments(id)
);