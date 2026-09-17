CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    username VARCHAR(100) NOT NULL UNIQUE,

    email VARCHAR(150) NOT NULL UNIQUE,

    password VARCHAR(255) NOT NULL,

    manager_id BIGINT NULL,

    CONSTRAINT fk_users_manager
        FOREIGN KEY (manager_id)
        REFERENCES users(id)
);