-- Script cập nhật users với role mới (USER và ADMIN)
-- Chạy sau khi đã chạy _role__updated_2roles.sql

-- Cập nhật user với role
-- role_id = 1 là USER
-- role_id = 2 là ADMIN

-- Xóa dữ liệu cũ
DELETE FROM shop_shoe_superteam.`user`;

-- Thêm admin mẫu (role_id = 2 = ADMIN)
INSERT INTO shop_shoe_superteam.`user` (id,address,created_at,email,full_name,is_active,otp_code,otp_expiry_date,password,phone,updated_at,username,role_id) VALUES
	 ('6698f5cf-6a8d-49a5-9c86-ac895ca15263','Admin Address','2025-09-20 15:24:07.439820','admin@example.com','Administrator',1,NULL,NULL,'$2a$10$FT6WerTL672r5eW4ZedgGuggLPZnu7ERUAzoWLQqXmrFURllIKZw2','0000000000',NULL,'admin',2);

-- Thêm user mẫu (role_id = 1 = USER)
INSERT INTO shop_shoe_superteam.`user` (id,address,created_at,email,full_name,is_active,otp_code,otp_expiry_date,password,phone,updated_at,username,role_id) VALUES
	 ('f9e9f47b-8bb9-42c1-b98f-d13907b0f0cb','Tỉnh Yên Bái, Huyện Trạm Tấu, Xã Bản Công','2025-09-20 13:58:32.120057','user@example.com','User Example',1,NULL,NULL,'$2a$10$peGeWJcde.Q8BFxDY0YNNejNMGGbg7DPUI7cdLSgE6KBSIUdjoWLW','0123456789','2025-09-20 16:51:58.160475','user',1);

-- Password cho admin: admin123
-- Password cho user: user123
