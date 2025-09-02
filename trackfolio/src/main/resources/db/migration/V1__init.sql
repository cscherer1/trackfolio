-- Create users table for TrackFolio
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster lookups
CREATE INDEX idx_users_email ON users(email);
