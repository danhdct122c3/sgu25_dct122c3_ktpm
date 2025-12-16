# Báo Cáo Kiểm Thử Nghiệp Vụ: Use Case

Dựa trên tài liệu '[Usecase\ Test]\ gen_test_from_usecase.pdf', báo cáo này trình bày việc tạo test cases từ các use case trong 'focususecase\ (1).docx'. Phương pháp 3 bước: Tạo scenarios, Xác định test cases, Xác định data values.

## UC1: Quản Lý Truy Cập

### Scenarios
1. Basic Flow: Đăng nhập thành công Customer, logout.
2. Basic + A1: Đăng ký thành công, sau đó đăng nhập.
3. Basic + A2: Đăng nhập Admin/Staff/Manager.
4. Basic + E1: Đăng nhập thất bại.

### Test Cases

| Test Case ID | Scenario | Username | Password | Expected Result |
|--------------|----------|----------|----------|-----------------|
| UC1-TC1 | Scenario 1 | customer1 | pass123 | Redirect to home page |
| UC1-TC2 | Scenario 2 | newuser | newpass | Register success, then login success |
| UC1-TC3 | Scenario 3 | admin1 | adminpass | Redirect to admin page |
| UC1-TC4 | Scenario 4 | wronguser | wrongpass | Error: Sai username/password |

## UC2: Quản Lý Người Dùng

### Scenarios
1. Basic: Xem và cập nhật profile.
2. Basic + A1: Đổi mật khẩu.
3. Basic + A2: Admin quản lý tài khoản.
4. Basic + E1: Dữ liệu không hợp lệ.
5. Basic + E2: Mật khẩu sai.

### Test Cases

| Test Case ID | Scenario | Old Password | New Password | Profile Data | Expected Result |
|--------------|----------|--------------|--------------|--------------|-----------------|
| UC2-TC1 | Scenario 1 | N/A | N/A | Valid data | Update success |
| UC2-TC2 | Scenario 2 | oldpass | newpass | N/A | Password changed |
| UC2-TC3 | Scenario 3 | N/A | N/A | N/A | Account list displayed |
| UC2-TC4 | Scenario 4 | N/A | N/A | Invalid data | Error message |
| UC2-TC5 | Scenario 5 | wrongold | newpass | N/A | Error: Wrong old password |

## UC3: Quản Lý và Cập Nhật Giỏ Hàng

### Scenarios
1. Basic: Thêm vào giỏ, cập nhật, áp dụng giảm giá, checkout.
2. Basic + A1: Lưu giỏ khi logout.
3. Basic + E1: Sản phẩm hết hàng.
4. Basic + E2: Mã giảm giá không hợp lệ.

### Test Cases

| Test Case ID | Scenario | Product ID | Quantity | Discount Code | Expected Result |
|--------------|----------|------------|----------|---------------|-----------------|
| UC3-TC1 | Scenario 1 | P001 | 2 | VALID10 | Cart updated, discount applied, checkout success |
| UC3-TC2 | Scenario 2 | P001 | 1 | N/A | Cart saved on logout |
| UC3-TC3 | Scenario 3 | P002 (out of stock) | 1 | N/A | Error: Out of stock |
| UC3-TC4 | Scenario 4 | P001 | 1 | INVALID | Error: Invalid discount |

## UC4: Tra Cứu và Xem Chi Tiết Sản Phẩm

### Scenarios
1. Basic: Duyệt danh mục, lọc, xem chi tiết.
2. Basic + A1: Tìm kiếm thay cho lọc.
3. Basic + E1: Không tìm thấy sản phẩm.

### Test Cases

| Test Case ID | Scenario | Category | Filter | Search Keyword | Expected Result |
|--------------|----------|----------|--------|----------------|-----------------|
| UC4-TC1 | Scenario 1 | Shoes | Brand=A | N/A | Filtered list, details displayed |
| UC4-TC2 | Scenario 2 | N/A | N/A | Nike | Search results |
| UC4-TC3 | Scenario 3 | Nonexistent | N/A | N/A | No products message |

## UC5: Quản Lý Sản Phẩm

### Scenarios
1. Basic: Chỉnh sửa sản phẩm.
2. Basic + A1: Nhân viên xem tồn kho.
3. Basic + E1: Dữ liệu không hợp lệ.

### Test Cases

| Test Case ID | Scenario | Product Data | Expected Result |
|--------------|----------|--------------|-----------------|
| UC5-TC1 | Scenario 1 | Valid update | Product updated |
| UC5-TC2 | Scenario 2 | N/A | Inventory displayed |
| UC5-TC3 | Scenario 3 | Invalid data | Error message |

## UC6: Quản Lý và Theo Dõi Đơn Hàng

### Scenarios
1. Basic Customer: Tạo, hủy, xác nhận nhận hàng.
2. Basic Staff: Xác nhận, chuẩn bị, giao hàng.
3. Basic + E1: Hủy không hợp lệ.
4. Basic + E2: Cập nhật trạng thái sai.

### Test Cases

| Test Case ID | Scenario | Order Status | Action | Expected Result |
|--------------|----------|--------------|--------|-----------------|
| UC6-TC1 | Scenario 1 | CREATED | Cancel | Order cancelled |
| UC6-TC2 | Scenario 2 | CREATED | Confirm | Status CONFIRMED |
| UC6-TC3 | Scenario 3 | READY | Cancel | Error: Cannot cancel |
| UC6-TC4 | Scenario 4 | CREATED | Prepare | Error: Invalid transition |

## UC7: Quản Lý Mã Giảm Giá

### Scenarios
1. Basic: Tạo mã giảm giá.
2. Basic + A1: Cập nhật mã.
3. Basic + A2: Xem chi tiết.
4. Basic + E1: Dữ liệu không hợp lệ.
5. Basic + E2: Mã trùng.
6. Basic + E3: Ngày không hợp lệ.

### Test Cases

| Test Case ID | Scenario | Code | Value | Dates | Expected Result |
|--------------|----------|------|-------|-------|-----------------|
| UC7-TC1 | Scenario 1 | NEW10 | 10% | Valid | Code created |
| UC7-TC2 | Scenario 2 | EXIST10 | 15% | Valid | Code updated |
| UC7-TC3 | Scenario 3 | EXIST10 | N/A | N/A | Details displayed |
| UC7-TC4 | Scenario 4 | NEW10 | -5% | Valid | Error: Invalid value |
| UC7-TC5 | Scenario 5 | EXIST10 | 10% | Valid | Error: Code exists |
| UC7-TC6 | Scenario 6 | NEW10 | 10% | End < Start | Error: Invalid dates |

Báo cáo này cung cấp coverage cho các luồng chính, alternate, và exception của từng use case.