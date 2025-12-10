-- Script to initialize database for integration tests
-- Run this in MySQL Workbench or command line before running tests

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS shop_shoe_superteam;
USE shop_shoe_superteam;

-- Insert roles if not exists (required for tests)
INSERT IGNORE INTO role (id, roles) VALUES
(1, 'ADMIN'),
(2, 'CUSTOMER'),
(3, 'MANAGER'),
(4, 'STAFF');

-- Verify
SELECT * FROM role;

