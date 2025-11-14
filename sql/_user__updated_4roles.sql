-- Update user->role mapping to align with 4 roles (MySQL)
-- Assumes users exist: admin, manager, staff, user
-- Only updates role_id; passwords remain as seeded by application

-- Map admin -> ADMIN
UPDATE `user` AS u
SET u.`role_id` = (
  SELECT r.`id` FROM `role` r WHERE r.`roles` = 'ADMIN' ORDER BY r.`id` ASC LIMIT 1
)
WHERE u.`username` = 'admin';

-- Map manager -> MANAGER
UPDATE `user` AS u
SET u.`role_id` = (
  SELECT r.`id` FROM `role` r WHERE r.`roles` = 'MANAGER' ORDER BY r.`id` ASC LIMIT 1
)
WHERE u.`username` = 'manager';

-- Map staff -> STAFF
UPDATE `user` AS u
SET u.`role_id` = (
  SELECT r.`id` FROM `role` r WHERE r.`roles` = 'STAFF' ORDER BY r.`id` ASC LIMIT 1
)
WHERE u.`username` = 'staff';

-- Map user -> CUSTOMER
UPDATE `user` AS u
SET u.`role_id` = (
  SELECT r.`id` FROM `role` r WHERE r.`roles` = 'CUSTOMER' ORDER BY r.`id` ASC LIMIT 1
)
WHERE u.`username` = 'user';

-- Optional: check results
-- SELECT u.username, u.role_id, r.roles FROM `user` u LEFT JOIN `role` r ON u.role_id = r.id WHERE u.username IN ('admin','manager','staff','user');