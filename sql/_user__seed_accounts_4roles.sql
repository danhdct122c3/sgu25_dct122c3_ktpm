-- Seed user accounts for 4 roles: CUSTOMER, STAFF, MANAGER, ADMIN (MySQL)
-- Notes:
-- - Password hashes below are BCrypt (cost=10) for test passwords.
-- - 'admin' -> password 'admin123'
-- - 'manager' -> password 'manager123'
-- - 'staff' and all customers -> password 'user123'
-- - Safe inserts: each block only runs if username does not exist.

-- Admin account
INSERT INTO `user` (
  `id`, `username`, `password`, `email`, `address`, `phone`, `fullName`, `isActive`, `createdAt`, `updatedAt`, `role_id`, `otpCode`, `otpExpiryDate`
)
SELECT UUID(), 'admin', '$2a$10$FT6WerTL672r5eW4ZedgGuggLPZnu7ERUAzoWLQqXmrFURllIKZw2',
       'admin@example.com', 'Admin Address', '0000000000', 'Administrator', 1, NOW(), NULL,
       (SELECT r.`id` FROM `role` r WHERE r.`roles` = 'ADMIN' ORDER BY r.`id` ASC LIMIT 1), NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `user` u WHERE u.`username` = 'admin');

-- Manager account
INSERT INTO `user` (
  `id`, `username`, `password`, `email`, `address`, `phone`, `fullName`, `isActive`, `createdAt`, `updatedAt`, `role_id`, `otpCode`, `otpExpiryDate`
)
SELECT UUID(), 'manager', '$2a$10$GEuZqe3sbw1A8t8FNUqWTO2TKu6Bjz32yfQZ6y9etopLc8eM2CSOa',
       'manager@example.com', '789 Main St, Springfield', '0987654321', 'Manager Example', 1, NOW(), NULL,
       (SELECT r.`id` FROM `role` r WHERE r.`roles` = 'MANAGER' ORDER BY r.`id` ASC LIMIT 1), NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `user` u WHERE u.`username` = 'manager');

-- Staff account (uses password 'user123' for convenience)
INSERT INTO `user` (
  `id`, `username`, `password`, `email`, `address`, `phone`, `fullName`, `isActive`, `createdAt`, `updatedAt`, `role_id`, `otpCode`, `otpExpiryDate`
)
SELECT UUID(), 'staff', '$2a$10$peGeWJcde.Q8BFxDY0YNNejNMGGbg7DPUI7cdLSgE6KBSIUdjoWLW',
       'staff@example.com', '456 Market Ave', '0777777777', 'Staff Example', 1, NOW(), NULL,
       (SELECT r.`id` FROM `role` r WHERE r.`roles` = 'STAFF' ORDER BY r.`id` ASC LIMIT 1), NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `user` u WHERE u.`username` = 'staff');

-- Customer (default) account
INSERT INTO `user` (
  `id`, `username`, `password`, `email`, `address`, `phone`, `fullName`, `isActive`, `createdAt`, `updatedAt`, `role_id`, `otpCode`, `otpExpiryDate`
)
SELECT UUID(), 'user', '$2a$10$peGeWJcde.Q8BFxDY0YNNejNMGGbg7DPUI7cdLSgE6KBSIUdjoWLW',
       'user@example.com', '123 Example St', '0123456789', 'User Example', 1, NOW(), NULL,
       (SELECT r.`id` FROM `role` r WHERE r.`roles` = 'CUSTOMER' ORDER BY r.`id` ASC LIMIT 1), NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `user` u WHERE u.`username` = 'user');

-- Additional customers for testing
INSERT INTO `user` (
  `id`, `username`, `password`, `email`, `address`, `phone`, `fullName`, `isActive`, `createdAt`, `updatedAt`, `role_id`, `otpCode`, `otpExpiryDate`
)
SELECT UUID(), 'customer01', '$2a$10$peGeWJcde.Q8BFxDY0YNNejNMGGbg7DPUI7cdLSgE6KBSIUdjoWLW',
       'customer01@example.com', '12 Sample Rd', '0900000001', 'Customer 01', 1, NOW(), NULL,
       (SELECT r.`id` FROM `role` r WHERE r.`roles` = 'CUSTOMER' ORDER BY r.`id` ASC LIMIT 1), NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `user` u WHERE u.`username` = 'customer01');

INSERT INTO `user` (
  `id`, `username`, `password`, `email`, `address`, `phone`, `fullName`, `isActive`, `createdAt`, `updatedAt`, `role_id`, `otpCode`, `otpExpiryDate`
)
SELECT UUID(), 'customer02', '$2a$10$peGeWJcde.Q8BFxDY0YNNejNMGGbg7DPUI7cdLSgE6KBSIUdjoWLW',
       'customer02@example.com', '34 Sample Rd', '0900000002', 'Customer 02', 1, NOW(), NULL,
       (SELECT r.`id` FROM `role` r WHERE r.`roles` = 'CUSTOMER' ORDER BY r.`id` ASC LIMIT 1), NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `user` u WHERE u.`username` = 'customer02');

INSERT INTO `user` (
  `id`, `username`, `password`, `email`, `address`, `phone`, `fullName`, `isActive`, `createdAt`, `updatedAt`, `role_id`, `otpCode`, `otpExpiryDate`
)
SELECT UUID(), 'customer03', '$2a$10$peGeWJcde.Q8BFxDY0YNNejNMGGbg7DPUI7cdLSgE6KBSIUdjoWLW',
       'customer03@example.com', '56 Sample Rd', '0900000003', 'Customer 03', 1, NOW(), NULL,
       (SELECT r.`id` FROM `role` r WHERE r.`roles` = 'CUSTOMER' ORDER BY r.`id` ASC LIMIT 1), NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `user` u WHERE u.`username` = 'customer03');

INSERT INTO `user` (
  `id`, `username`, `password`, `email`, `address`, `phone`, `fullName`, `isActive`, `createdAt`, `updatedAt`, `role_id`, `otpCode`, `otpExpiryDate`
)
SELECT UUID(), 'customer04', '$2a$10$peGeWJcde.Q8BFxDY0YNNejNMGGbg7DPUI7cdLSgE6KBSIUdjoWLW',
       'customer04@example.com', '78 Sample Rd', '0900000004', 'Customer 04', 1, NOW(), NULL,
       (SELECT r.`id` FROM `role` r WHERE r.`roles` = 'CUSTOMER' ORDER BY r.`id` ASC LIMIT 1), NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `user` u WHERE u.`username` = 'customer04');

INSERT INTO `user` (
  `id`, `username`, `password`, `email`, `address`, `phone`, `fullName`, `isActive`, `createdAt`, `updatedAt`, `role_id`, `otpCode`, `otpExpiryDate`
)
SELECT UUID(), 'customer05', '$2a$10$peGeWJcde.Q8BFxDY0YNNejNMGGbg7DPUI7cdLSgE6KBSIUdjoWLW',
       'customer05@example.com', '90 Sample Rd', '0900000005', 'Customer 05', 1, NOW(), NULL,
       (SELECT r.`id` FROM `role` r WHERE r.`roles` = 'CUSTOMER' ORDER BY r.`id` ASC LIMIT 1), NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM `user` u WHERE u.`username` = 'customer05');