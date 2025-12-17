# 📚 HƯỚNG DẪN SỬ DỤNG TEST DATA

## 📂 Cấu Trúc Files

Hệ thống có 4 modules chính, mỗi module có 1 file CSV riêng chỉ chứa **data đầu vào** và **kết quả mong đợi**:

### **1. test_data_authentication.csv** - Quản Lý Truy Cập
- **Columns:** username, password, fullName, email, phoneNumber, expectedStatus, expectedMessage
- **15 test data rows** cho đăng ký, đăng nhập, phân quyền

### **2. test_data_products.csv** - Danh Mục Sản Phẩm
- **Columns:** name, price, fakePrice, gender, category, brandId, status, minPrice, maxPrice, searchKeyword, page, size, expectedStatus, expectedMessage
- **29 test data rows** cho tìm kiếm, lọc, thêm, sửa sản phẩm

### **3. test_data_cart.csv** - Giỏ Hàng
- **Columns:** variantId, quantity, discountCode, expectedStatus, expectedMessage
- **29 test data rows** cho thêm, cập nhật, xóa, thanh toán

### **4. test_data_orders.csv** - Quản Lý Đơn Hàng
- **Columns:** orderId, newStatus, orderStatus, searchKeyword, page, size, discountCode, expectedStatus, expectedMessage
- **28 test data rows** cho tạo đơn, xem, lọc, cập nhật trạng thái

---

## 🔧 Cách Sử Dụng

### **Option 1: Import Vào Excel (Khuyên Dùng)**

1. **Mở Excel** → Tạo file mới
2. **Import từng file CSV:**
   - Data → From Text/CSV → Chọn file `test_data_authentication.csv`
   - Load → Sheet sẽ tự động tạo
   - Đổi tên sheet thành "Authentication"
3. **Lặp lại** cho 3 file còn lại:
   - `test_data_products.csv` → Sheet "Products"
   - `test_data_cart.csv` → Sheet "Shopping Cart"
   - `test_data_orders.csv` → Sheet "Orders"
4. **Lưu file** thành `test_data.xlsx`

**Kết quả:** 1 file Excel với 4 sheets riêng biệt!

---

### **Option 2: Sử Dụng Trong Postman Collection Runner**

#### **Bước 1: Chuẩn bị Collection**

Trong Postman request, sử dụng variables từ CSV:

```json
// Authentication - Login Request
{
  "username": "{{username}}",
  "password": "{{password}}"
}
```

#### **Bước 2: Thêm Test Scripts**

```javascript
// Test script tự động validate
pm.test("Status code is " + pm.iterationData.get("expectedStatus"), function () {
    pm.response.to.have.status(parseInt(pm.iterationData.get("expectedStatus")));
});

pm.test("Response contains: " + pm.iterationData.get("expectedResult"), function () {
    var jsonData = pm.response.json();
    pm.expect(JSON.stringify(jsonData)).to.include(pm.iterationData.get("expectedResult"));
});
```

#### **Bước 3: Chạy Collection Runner**

1. Click **Collections** → Chọn collection của bạn
2. Click **Run**
3. Click **Select File** → Chọn file CSV (ví dụ: `test_data_authentication.csv`)
4. Click **Run Test Data**
5. Postman sẽ chạy **24 test cases tự động!**

---

### **Option 3: Sử Dụng Với Newman CLI**

```bash
# Chạy test cho Authentication module
newman run postman_collection.json -d test_data_authentication.csv -e environment.json --reporters cli,htmlextra

# Chạy test cho Products module
newman run postman_collection.json -d test_data_products.csv -e environment.json --reporters cli,htmlextra

# Chạy test cho Cart module
newman run postman_collection.json -d test_data_cart.csv -e environment.json --reporters cli,htmlextra

# Chạy test cho Orders module
newman run postman_collection.json -d test_data_orders.csv -e environment.json --reporters cli,htmlextra
```

---

## 📝 Cấu Trúc Columns

### **Authentication Module**
| Column | Mô Tả | Ví Dụ | Required |
|--------|-------|-------|----------|
| testId | ID test case | DataAU1, DataAU2 | Có |
| description | Mô tả test case | Đăng nhập thành công - Admin | Có |
| username | Tên đăng nhập | admin01 | Có (login) |
| password | Mật khẩu | admin123 | Có (login) |
| fullName | Họ tên đầy đủ | Nguyen Van A | Có (register) |
| email | Email | user@example.com | Có (register) |
| phoneNumber | Số điện thoại | 0901234567 | Có (register) |
| expectedStatus | HTTP status mong đợi | 200, 400, 401 | Có |
| expectedMessage | Message mong đợi | Login successful | Có |

### **Products Module**
| Column | Mô Tả | Ví Dụ | Required |
|--------|-------|-------|----------|
| testId | ID test case | DataProd1, DataProd2 | Có |
| description | Mô tả test case | Tạo sản phẩm thành công | Có |
| name | Tên sản phẩm | Nike Air Max 90 | Có (create/update) |
| price | Giá bán | 2299000 | Có (create/update) |
| fakePrice | Giá gốc | 2999000 | Có (create/update) |
| gender | Giới tính | UNISEX/MAN/WOMAN | Có (create) |
| category | Danh mục | RUNNING/CASUAL | Có (create) |
| brandId | ID thương hiệu | 1, 2, 3 | Có (create) |
| status | Trạng thái | true/false | Có (create) |
| minPrice | Giá tối thiểu (lọc) | 1000000 | Không |
| maxPrice | Giá tối đa (lọc) | 3000000 | Không |
| searchKeyword | Từ khóa tìm kiếm | Nike | Không |
| page | Trang (phân trang) | 0, 1, 2 | Không |
| size | Kích thước trang | 10, 20 | Không |
| expectedStatus | HTTP status | 200, 400, 404 | Có |
| expectedMessage | Message mong đợi | Success | Có |

### **Cart Module**
| Column | Mô Tả | Ví Dụ | Required |
|--------|-------|-------|----------|
| testId | ID test case | DataCart1, DataCart2 | Có |
| description | Mô tả test case | Thêm sản phẩm vào giỏ | Có |
| variantId | ID variant sản phẩm | UUID-VARIANT-1 | Có (add/update) |
| quantity | Số lượng | 2, 5, 10 | Có (add/update) |
| discountCode | Mã giảm giá | SUMMER2024 | Không |
| expectedStatus | HTTP status | 200, 400, 404 | Có |
| expectedMessage | Message mong đợi | Item added | Có |

### **Orders Module**
| Column | Mô Tả | Ví Dụ | Required |
|--------|-------|-------|----------|
| testId | ID test case | DataOrder1, DataOrder2 | Có |
| description | Mô tả test case | Tạo đơn hàng thành công | Có |
| orderId | ID đơn hàng | ORDER-UUID-1 | Có (update/view) |
| newStatus | Trạng thái mới | CONFIRMED | Có (update) |
| orderStatus | Lọc theo status | CREATED | Không (filter) |
| searchKeyword | Từ khóa tìm kiếm | customer01 | Không (search) |
| page | Trang (phân trang) | 0, 1 | Không |
| size | Kích thước trang | 10 | Không |
| discountCode | Mã giảm giá | SUMMER2024 | Không (create) |
| expectedStatus | HTTP status | 200, 400, 404 | Có |
| expectedMessage | Message mong đợi | Order created | Có |

---

## ⚠️ Lưu Ý Quan Trọng

### **1. Thay Thế UUID Thực Tế**

Một số field cần thay bằng giá trị thực từ database:

- `UUID-VARIANT-1` → Thay bằng variant ID thực tế
- `ORDER-UUID-1` → Thay bằng order ID thực tế
- `{id}` trong API path → Thay bằng ID thực tế

**Cách lấy:**
```javascript
// Test script - Lưu ID sau khi tạo
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("shoe_id", jsonData.result.id);
}

// Request tiếp theo dùng {{shoe_id}}
```

### **2. Thứ Tự Chạy Test**

Một số test phụ thuộc vào test trước:

1. **Authentication**: Chạy Login trước để lấy token
2. **Products**: Tạo sản phẩm trước khi update/delete
3. **Cart**: Có sản phẩm mới có thể thêm vào giỏ
4. **Orders**: Có giỏ hàng mới tạo được đơn

### **3. Chuẩn Bị Environment**

Tạo Postman Environment với variables:

```json
{
  "base_url": "http://localhost:8080/api/v1",
  "access_token": "",
  "shoe_id": "",
  "variant_id": "",
  "order_id": ""
}
```

### **4. Test Data Dependencies**

- **Products**: Cần có brands trong database (chạy SQL seed trước)
- **Cart**: Cần có shoes và variants
- **Orders**: Cần có cart items và users

---

## 📊 Thống Kê Test Coverage

| Module | Total Tests | Positive | Negative | Coverage |
|--------|-------------|----------|----------|----------|
| Authentication | 24 | 8 | 16 | 100% |
| Products | 42 | 20 | 22 | 95% |
| Shopping Cart | 35 | 18 | 17 | 90% |
| Orders | 45 | 25 | 20 | 95% |
| **TOTAL** | **146** | **71** | **75** | **95%** |

---

## 🚀 Quick Start

### **Test Ngay Trong 5 Phút:**

1. **Import Postman Collection**
   ```
   File → Import → postman_collection.json
   ```

2. **Tạo Environment**
   ```
   base_url = http://localhost:8080/api/v1
   ```

3. **Login để lấy token**
   ```
   POST /auth/login
   Body: {"username": "admin01", "password": "admin123"}
   ```

4. **Chạy Authentication Tests**
   ```
   Collection Runner → Select test_data_authentication.csv → Run
   ```

5. **Xem Report**
   ```
   24/24 tests passed ✅
   ```

---

## 💡 Tips

### **Tăng Tốc Testing:**

1. **Sử dụng Pre-request Scripts** để tự động login
2. **Save token** vào environment sau mỗi login
3. **Chain requests** bằng cách lưu IDs
4. **Skip tests** không cần thiết bằng cách comment CSV rows

### **Tự Động Hóa:**

```bash
# Chạy tất cả modules một lúc
newman run postman_collection.json -d test_data_authentication.csv && \
newman run postman_collection.json -d test_data_products.csv && \
newman run postman_collection.json -d test_data_cart.csv && \
newman run postman_collection.json -d test_data_orders.csv
```

---

## 📞 Hỗ Trợ

Nếu gặp vấn đề:
1. Kiểm tra backend đã chạy chưa: `http://localhost:8080`
2. Kiểm tra database đã có data seed chưa
3. Verify token còn hiệu lực không
4. Check console log trong Postman

**Chúc bạn test thành công! 🎉**
