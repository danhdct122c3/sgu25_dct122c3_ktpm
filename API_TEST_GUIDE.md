# 📋 Hướng Dẫn Sử Dụng API Test Template

## 📚 Tổng Quan

File **API_Test_Template.csv** là template để thực hiện manual testing cho tất cả API endpoints, tổng hợp data từ 4 modules:
- **Authentication** (15 test cases)
- **Products** (28 test cases)
- **Cart** (28 test cases)
- **Orders** (29 test cases)

**Tổng cộng: 100 test cases**

---

## 📊 Cấu Trúc File Excel

### **Columns Explanation**

| Column | Mô Tả | Ví Dụ |
|--------|-------|-------|
| **apiTestId** | ID duy nhất cho API test case | API-AU-001, API-Prod-005, API-Cart-010 |
| **dataTestId** | ID tham chiếu đến test data | DataAU1, DataProd5, DataCart10 |
| **module** | Module/chức năng | Authentication, Products, Cart, Orders |
| **description** | Mô tả chi tiết test case | Đăng nhập thành công - Admin |
| **api** | API endpoint | /auth/login, /shoes, /cart/items |
| **method** | HTTP method | GET, POST, PUT, DELETE |
| **requestData** | Dữ liệu request (parameters/body) | username=admin01, password=admin123 |
| **expectedStatus** | HTTP status code mong đợi | 200, 201, 400, 401, 404 |
| **expectedMessage** | Message mong đợi trong response | Login successful, Invalid credentials |
| **actualStatus** | Status code thực tế (điền khi test) | *Để trống, điền sau khi test* |
| **actualMessage** | Message thực tế (điền khi test) | *Để trống, điền sau khi test* |
| **result** | Kết quả test | PASS/FAIL (điền sau khi test) |
| **notes** | Ghi chú thêm | Requires authentication, Replace {id} with actual ID |

---

## 🎯 Cách Sử Dụng

### **Bước 1: Import File CSV vào Excel**

1. Mở Microsoft Excel
2. File → Open → Chọn `API_Test_Template.csv`
3. Hoặc: Data → From Text/CSV → Chọn file → Load

### **Bước 2: Format Excel**

1. **Freeze First Row** (đóng băng dòng tiêu đề):
   - View → Freeze Panes → Freeze Top Row

2. **Auto-fit Columns** (tự động điều chỉnh độ rộng cột):
   - Select All (Ctrl+A) → Home → Format → AutoFit Column Width

3. **Apply Filters** (thêm bộ lọc):
   - Select header row → Data → Filter

4. **Format as Table** (định dạng bảng):
   - Select all data → Home → Format as Table → Choose style

5. **Add Data Validation cho cột Result**:
   - Select cột `result` → Data → Data Validation
   - Allow: List
   - Source: `PASS,FAIL,SKIP`

### **Bước 3: Chuẩn Bị Test Environment**

#### **3.1. Lấy Access Token**

Trước khi test các API yêu cầu authentication, cần login để lấy token:

```bash
# Login với admin
POST /auth/login
Body: {
  "username": "admin01",
  "password": "admin123"
}

# Copy access_token từ response
Response: {
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Lưu token vào:**
- Postman: Environment variables → `access_token`
- Manual testing: Notepad để copy vào header

#### **3.2. Thay Thế UUID Placeholders**

Các test case có placeholders cần thay bằng ID thực:

| Placeholder | Cách lấy ID thực |
|-------------|------------------|
| `{id}` trong `/shoes/{id}` | GET /shoes → copy `id` của shoe muốn test |
| `UUID-VARIANT-1` | GET /shoes → lấy `variantId` từ variants array |
| `ORDER-UUID-1` | GET /orders → lấy `id` của order muốn test |
| `{variantId}` trong URL | Sử dụng variantId đã lấy ở trên |

**Ví dụ:**
```
Before: /shoes/{id}
After:  /shoes/123

Before: variantId=UUID-VARIANT-1
After:  variantId=a3f2e1d4-5b6c-7d8e-9f0a-1b2c3d4e5f6g
```

### **Bước 4: Thực Hiện Test**

#### **4.1. Sử Dụng Postman**

1. **Setup Request:**
   - Method: Copy từ cột `method`
   - URL: `{{base_url}}` + cột `api`
   - Headers: 
     ```
     Authorization: Bearer {{access_token}}
     Content-Type: application/json
     ```

2. **Request Body/Params:**
   - GET requests: Chuyển `requestData` thành Query Params
   - POST/PUT requests: Chuyển `requestData` thành JSON Body

   **Ví dụ:**
   ```
   requestData: username=admin01, password=admin123
   
   → JSON Body:
   {
     "username": "admin01",
     "password": "admin123"
   }
   ```

3. **Execute & Record:**
   - Send request
   - Ghi `actualStatus` vào Excel
   - Ghi `actualMessage` vào Excel
   - So sánh với expected → Ghi `PASS`/`FAIL` vào cột `result`

#### **4.2. Sử Dụng cURL**

```bash
# Example: DataAU8 - Login Admin
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin01",
    "password": "admin123"
  }'

# Example: DataProd1 - Get All Shoes
curl -X GET "http://localhost:8080/shoes?page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

#### **4.3. Manual Testing Checklist**

Cho mỗi test case:

- [ ] Đọc `description` để hiểu mục đích test
- [ ] Check `notes` để xem requirements đặc biệt
- [ ] Chuẩn bị `requestData` (thay UUID nếu cần)
- [ ] Thực hiện request với đúng `method` và `api`
- [ ] Ghi nhận `actualStatus` và `actualMessage`
- [ ] So sánh actual vs expected
- [ ] Đánh dấu `PASS`/`FAIL` trong cột `result`
- [ ] Ghi thêm notes nếu có lỗi

### **Bước 5: Phân Tích Kết Quả**

#### **5.1. Tính Tỷ Lệ Pass/Fail**

Thêm công thức Excel ở cuối file:

```excel
# Đếm số test PASS
=COUNTIF(K:K,"PASS")

# Đếm số test FAIL
=COUNTIF(K:K,"FAIL")

# Tỷ lệ PASS (%)
=COUNTIF(K:K,"PASS")/COUNTA(K:K)*100

# Tổng số test
=COUNTA(A:A)-1
```

#### **5.2. Filter Các Test Failed**

1. Click Filter icon ở cột `result`
2. Chọn chỉ hiển thị `FAIL`
3. Review và fix bugs

#### **5.3. Tạo Pivot Table**

Để phân tích theo module:

1. Select all data → Insert → PivotTable
2. **Rows:** module
3. **Values:** Count of result
4. **Columns:** result (PASS/FAIL)

---

## 📝 Test Execution Order

### **Thứ Tự Thực Hiện Recommended:**

#### **Phase 1: Authentication (Ưu tiên cao)**
```
DataAU8  → Login Admin (lấy token)
DataAU9  → Login Customer (lấy token)
DataAU10 → Login Staff (lấy token)
DataAU1-7, 11-15 → Test các trường hợp khác
```

#### **Phase 2: Products Management**
```
DataProd1-15  → Test search/filter (không cần auth)
DataProd16-28 → Test CRUD (cần Admin/Manager token)
```

#### **Phase 3: Shopping Cart**
```
DataCart1-3   → Thêm items vào giỏ
DataCart18-22 → Test discount codes
DataCart23-25 → Tạo order từ giỏ
DataCart4-17  → Test validation & edge cases
```

#### **Phase 4: Order Management**
```
DataOrder1-3  → Tạo orders
DataOrder4-5  → View order details
DataOrder6-15 → Test order status transitions
DataOrder16-29 → Test filtering & search
```

---

## ⚠️ Lưu Ý Quan Trọng

### **Authorization Requirements**

| Module | Endpoint | Required Role |
|--------|----------|---------------|
| Authentication | /auth/* | Public |
| Products GET | /shoes (GET) | Public |
| Products CRUD | /shoes (POST/PUT/DELETE) | Admin, Manager |
| Cart | /cart/* | Customer (authenticated) |
| Orders CREATE | /orders (POST) | Customer (authenticated) |
| Orders MANAGE | /orders (PUT/DELETE) | Admin, Manager, Staff |
| Orders VIEW ALL | /orders (GET all) | Admin, Manager, Staff |
| Orders VIEW OWN | /orders (GET own) | Customer (authenticated) |

### **Common Issues & Solutions**

| Issue | Giải Pháp |
|-------|-----------|
| **401 Unauthorized** | Kiểm tra token đã expired chưa, login lại để lấy token mới |
| **403 Forbidden** | User không có quyền, đổi sang account có role phù hợp |
| **404 Not Found** | Kiểm tra ID trong URL có tồn tại không, có thể đã bị xóa |
| **400 Bad Request** | Kiểm tra format request data, thiếu field bắt buộc |
| **500 Internal Error** | Lỗi server, check logs backend, có thể cần restart server |

### **UUID Management**

1. **Lấy Product IDs:**
   ```
   GET /shoes?page=0&size=10
   Response: [...shoes with id field...]
   ```

2. **Lấy Variant IDs:**
   ```
   GET /shoes/{shoeId}
   Response: {
     ...shoe details,
     "variants": [
       { "variantId": "uuid-here", ... }
     ]
   }
   ```

3. **Lấy Order IDs:**
   ```
   GET /orders/my-orders  (for customer)
   GET /orders            (for admin/staff)
   Response: [...orders with id field...]
   ```

---

## 📈 Reporting Template

### **Test Summary Report**

Sau khi hoàn thành testing, tạo summary:

```
=================================
API TESTING REPORT
=================================

Test Date: [Date]
Tester: [Your Name]
Environment: [Dev/Test/Prod]

---------------------------------
SUMMARY
---------------------------------
Total Test Cases: [Count]
Passed: [Count] ([%])
Failed: [Count] ([%])
Skipped: [Count] ([%])

---------------------------------
FAILED TEST CASES
---------------------------------
[List of failed testIds with description]

---------------------------------
BUGS FOUND
---------------------------------
1. [Bug description]
   - Test Case: [testId]
   - Expected: [expected result]
   - Actual: [actual result]
   - Severity: [High/Medium/Low]

---------------------------------
NOTES
---------------------------------
[Any additional observations]
```

---

## 🔄 Integration với Postman Collection Runner

Nếu muốn automate, có thể:

1. Export Excel → CSV
2. Import vào Postman Collection Runner
3. Sử dụng Postman Tests scripts để tự động verify

**Postman Test Script Example:**
```javascript
pm.test("Status code is correct", function () {
    pm.response.to.have.status(pm.iterationData.get("expectedStatus"));
});

pm.test("Response message is correct", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.message).to.include(pm.iterationData.get("expectedMessage"));
});
```

---

## 📞 Support

Nếu gặp vấn đề:
1. Check TEST_DATA_GUIDE.md cho thông tin chi tiết về test data
2. Review Postman collection documentation
3. Check backend API documentation
4. Contact team lead/developer

---

**Happy Testing! 🚀**
