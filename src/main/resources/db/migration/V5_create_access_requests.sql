CREATE TABLE access_requests (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    access_id BIGINT NOT NULL,

    reason TEXT NOT NULL,

    status VARCHAR(30) NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_access_requests_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_access_requests_access
        FOREIGN KEY (access_id)
        REFERENCES accesses(id)
);

CREATE INDEX idx_access_requests_user
ON access_requests(user_id);

CREATE INDEX idx_access_requests_status
ON access_requests(status);

CREATE INDEX idx_access_requests_user_status
ON access_requests(user_id, status);