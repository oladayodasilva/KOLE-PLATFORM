ALTER TABLE identity.user_roles
    ALTER COLUMN assigned_at
    SET DEFAULT CURRENT_TIMESTAMP;