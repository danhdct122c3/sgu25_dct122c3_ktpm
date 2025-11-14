-- Seed/update Role table to 4 roles: CUSTOMER, STAFF, MANAGER, ADMIN (MySQL)
-- Safe inserts: only create role rows if they don't already exist

INSERT INTO `role` (`roles`)
SELECT 'CUSTOMER'
WHERE NOT EXISTS (SELECT 1 FROM `role` WHERE `roles` = 'CUSTOMER');

INSERT INTO `role` (`roles`)
SELECT 'STAFF'
WHERE NOT EXISTS (SELECT 1 FROM `role` WHERE `roles` = 'STAFF');

INSERT INTO `role` (`roles`)
SELECT 'MANAGER'
WHERE NOT EXISTS (SELECT 1 FROM `role` WHERE `roles` = 'MANAGER');

INSERT INTO `role` (`roles`)
SELECT 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM `role` WHERE `roles` = 'ADMIN');

-- Optional: verify
-- SELECT * FROM `role`;