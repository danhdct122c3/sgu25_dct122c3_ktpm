-- Script cập nhật: Chỉ sử dụng 2 role chính - USER và ADMIN
-- Thay thế MEMBER -> USER, xóa GUEST và MANAGER

-- Xóa dữ liệu cũ nếu có
DELETE FROM shop_shoe_superteam.`role`;

-- Thêm 2 role mới
INSERT INTO shop_shoe_superteam.`role` (roles) VALUES
	 ('USER'),
	 ('ADMIN');

-- Lưu ý: Sau khi chạy script này, cần cập nhật lại role_id trong bảng user
-- USER role sẽ có id = 1
-- ADMIN role sẽ có id = 2
