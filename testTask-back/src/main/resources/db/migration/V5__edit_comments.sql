DROP TABLE tasks_comments;

ALTER TABLE comments ADD COLUMN task_id BIGINT;

ALTER TABLE comments ADD CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES tasks(id);
