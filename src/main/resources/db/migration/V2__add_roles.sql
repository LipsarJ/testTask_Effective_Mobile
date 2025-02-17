CREATE TABLE roles (
                       id bigint PRIMARY KEY,
                       role_name TEXT
);
INSERT INTO roles (id, role_name) VALUES (1, 'ADMIN');
INSERT INTO roles (id, role_name) VALUES (2, 'USER');

CREATE TABLE user_roles (
                            user_id BIGSERIAL,
                            role_id BIGSERIAL NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id),
                            FOREIGN KEY (role_id) REFERENCES roles(id)
);
