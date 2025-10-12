-- Cart table
CREATE TABLE IF NOT EXISTS cart (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES _user_(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_cart (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample data (optional)
-- INSERT INTO cart (id, user_id) VALUES 
-- (UUID(), 'existing-user-id-1'),
-- (UUID(), 'existing-user-id-2');
