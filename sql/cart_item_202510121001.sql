-- Cart Item table
CREATE TABLE IF NOT EXISTS cart_item (
    id VARCHAR(36) PRIMARY KEY,
    cart_id VARCHAR(36) NOT NULL,
    variant_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES shoe_variant(id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_variant (cart_id, variant_id),
    CHECK (quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes for performance
CREATE INDEX idx_cart_item_cart_id ON cart_item(cart_id);
CREATE INDEX idx_cart_item_variant_id ON cart_item(variant_id);

-- Sample data (optional)
-- INSERT INTO cart_item (id, cart_id, variant_id, quantity) VALUES 
-- (UUID(), 'cart-id-1', 'variant-id-1', 2),
-- (UUID(), 'cart-id-1', 'variant-id-2', 1);
