CREATE TABLE identity.users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(40) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uq_identity_users_email
        UNIQUE (email),

    CONSTRAINT uq_identity_users_phone
        UNIQUE (phone_number),

    CONSTRAINT ck_identity_users_status
        CHECK (
            status IN (
                'PENDING_VERIFICATION',
                'ACTIVE',
                'SUSPENDED',
                'DEACTIVATED'
            )
        )
);

CREATE TABLE identity.roles (
    id UUID PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uq_identity_roles_name
        UNIQUE (name)
);

CREATE TABLE identity.permissions (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uq_identity_permissions_name
        UNIQUE (name)
);

CREATE TABLE identity.user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_identity_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES identity.users (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_identity_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES identity.roles (id)
        ON DELETE RESTRICT
);

CREATE TABLE identity.role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,

    PRIMARY KEY (role_id, permission_id),

    CONSTRAINT fk_identity_role_permissions_role
        FOREIGN KEY (role_id)
        REFERENCES identity.roles (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_identity_role_permissions_permission
        FOREIGN KEY (permission_id)
        REFERENCES identity.permissions (id)
        ON DELETE CASCADE
);

CREATE TABLE identity.refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    family_id UUID NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    replaced_by_token_id UUID,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uq_identity_refresh_token_hash
        UNIQUE (token_hash),

    CONSTRAINT fk_identity_refresh_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES identity.users (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_identity_refresh_tokens_replacement
        FOREIGN KEY (replaced_by_token_id)
        REFERENCES identity.refresh_tokens (id)
        ON DELETE SET NULL
);

CREATE INDEX idx_identity_users_status
    ON identity.users (status);

CREATE INDEX idx_identity_refresh_tokens_user
    ON identity.refresh_tokens (user_id);

CREATE INDEX idx_identity_refresh_tokens_family
    ON identity.refresh_tokens (family_id);

CREATE INDEX idx_identity_refresh_tokens_expires
    ON identity.refresh_tokens (expires_at);

INSERT INTO identity.roles (
    id,
    name,
    description,
    created_at,
    updated_at
)
VALUES
(
    '10000000-0000-0000-0000-000000000001',
    'HOUSEHOLD',
    'Individual or household waste supplier',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '10000000-0000-0000-0000-000000000002',
    'ESTATE_MANAGER',
    'Manager of an estate or residential community',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '10000000-0000-0000-0000-000000000003',
    'BUSINESS',
    'Business or institutional waste supplier',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '10000000-0000-0000-0000-000000000004',
    'COLLECTOR',
    'Approved waste collection field operator',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '10000000-0000-0000-0000-000000000005',
    'OPERATIONS_MANAGER',
    'Internal operations manager',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '10000000-0000-0000-0000-000000000006',
    'ADMIN',
    'KÓLÉ platform administrator',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '10000000-0000-0000-0000-000000000007',
    'EXECUTIVE',
    'Executive platform access',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);