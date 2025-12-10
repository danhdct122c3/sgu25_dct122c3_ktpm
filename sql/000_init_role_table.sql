-- Ensure `role` table exists before other seed scripts run
-- This file must be alphabetically first so it runs before other init scripts

CREATE DATABASE IF NOT EXISTS shop_shoe_superteam CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shop_shoe_superteam;

-- Create `role` table expected by other SQL seed files
CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `roles` VARCHAR(100) NOT NULL UNIQUE,
  `description` TEXT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Also create legacy `_role_` table as alias if your app expects it
CREATE TABLE IF NOT EXISTS `_role_` (
  `id` VARCHAR(36) NOT NULL PRIMARY KEY,
  `name` VARCHAR(50) NOT NULL UNIQUE,
  `description` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- If `_role_` is empty but `role` has data, keep them in sync later via seed scripts

-- Insert the four default roles into `role` only if not exists (other scripts may insert too)
INSERT INTO `role` (`roles`, `description`)
SELECT 'STAFF', 'Staff users with limited privileges' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `role` WHERE `roles` = 'STAFF');

INSERT INTO `role` (`roles`, `description`)
SELECT 'MANAGER', 'Manager users with elevated privileges' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `role` WHERE `roles` = 'MANAGER');

INSERT INTO `role` (`roles`, `description`)
SELECT 'CUSTOMER', 'End customers' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `role` WHERE `roles` = 'CUSTOMER');

INSERT INTO `role` (`roles`, `description`)
SELECT 'ADMIN', 'Administrators with full privileges' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `role` WHERE `roles` = 'ADMIN');

-- Optionally sync to `_role_` for compatibility if that table is referenced by code
INSERT INTO `_role_` (`id`, `name`, `description`)
SELECT UUID(), r.roles, r.description FROM `role` r
WHERE NOT EXISTS (SELECT 1 FROM `_role_` rr WHERE rr.name = r.roles);

