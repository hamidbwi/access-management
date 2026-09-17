CREATE TABLE approval_actions (
    id BIGSERIAL PRIMARY KEY,

    request_id BIGINT NOT NULL,

    approver_id BIGINT NOT NULL,

    action VARCHAR(20) NOT NULL,

    notes TEXT,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_approval_actions_request
        FOREIGN KEY (request_id)
        REFERENCES access_requests(id),

    CONSTRAINT fk_approval_actions_approver
        FOREIGN KEY (approver_id)
        REFERENCES users(id)
);

CREATE INDEX idx_approval_actions_request
ON approval_actions(request_id);