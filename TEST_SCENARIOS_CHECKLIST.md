# 📋 Test Scenarios Checklist

## ✅ Hướng dẫn sử dụng

### 1. Import vào Google Sheets hoặc Excel
- Mở file `TEST_CASES.csv`
- Import vào Google Sheets: File → Import → Upload
- Hoặc mở trực tiếp bằng Excel

### 2. Cấu trúc Test Cases

File `TEST_CASES.csv` chứa **97 test cases** được tổ chức theo:

#### 📊 Các cột chính:
1. **Test Case ID**: Mã định danh duy nhất (TC_MODULE_XXX)
2. **Module**: Phân loại module (Authentication, User Management, etc.)
3. **Feature**: Chức năng cụ thể
4. **Test Scenario**: Kịch bản test
5. **Test Case Description**: Mô tả chi tiết
6. **Preconditions**: Điều kiện tiên quyết
7. **Test Steps**: Các bước thực hiện (chi tiết)
8. **Test Data**: Dữ liệu test cụ thể
9. **Expected Result**: Kết quả mong đợi
10. **Actual Result**: Kết quả thực tế (điền khi test)
11. **Status**: Trạng thái (Not Tested/Passed/Failed/Blocked)
12. **Priority**: Độ ưu tiên (Critical/High/Medium/Low)
13. **Tested By**: Người test
14. **Test Date**: Ngày test
15. **Notes**: Ghi chú

---

## 📦 14 Test Modules

### 1️⃣ Authentication (TC_AUTH_001 → TC_AUTH_009)
- ✅ Valid/Invalid login
- ✅ Token validation
- ✅ Password reset flow (OTP)
- ✅ Change password
- ✅ Logout

**Trọng tâm**: Security, token management, password recovery

### 2️⃣ User Management (TC_USER_001 → TC_USER_007)
- ✅ Registration (valid/invalid/duplicate)
- ✅ Get users (admin/unauthorized)
- ✅ Update profile
- ✅ Pagination

**Trọng tâm**: CRUD operations, role-based access

### 3️⃣ Brand Management (TC_BRAND_001 → TC_BRAND_006)
- ✅ Create/Update/Delete brands
- ✅ Upload logo
- ✅ Duplicate validation
- ✅ Public access

**Trọng tâm**: File upload, validation

### 4️⃣ Shoe Management (TC_SHOE_001 → TC_SHOE_012)
- ✅ CRUD operations
- ✅ Validation (price, required fields)
- ✅ Filters (gender, brand, category)
- ✅ Multi-filter
- ✅ Pagination & sorting

**Trọng tâm**: Complex filtering, data validation

### 5️⃣ Shoe Variants (TC_VARIANT_001 → TC_VARIANT_006)
- ✅ Create/Update/Delete variants
- ✅ Stock management
- ✅ Size chart initialization
- ✅ Negative stock validation

**Trọng tâm**: Inventory management

### 6️⃣ Shoe Images (TC_IMAGE_001 → TC_IMAGE_004)
- ✅ Add/Delete images
- ✅ File upload
- ✅ Get images by shoe

**Trọng tâm**: File handling

### 7️⃣ Shopping Cart (TC_CART_001 → TC_CART_007)
- ✅ Add/Update/Remove items
- ✅ Stock validation
- ✅ Quantity limits
- ✅ Clear cart

**Trọng tâm**: Business logic, stock validation

### 8️⃣ Orders (TC_ORDER_001 → TC_ORDER_015)
- ✅ Create order
- ✅ Apply discount
- ✅ Cancel order
- ✅ Update status (workflow)
- ✅ Status transitions validation
- ✅ Role-based actions

**Trọng tâm**: Complex workflow, state management

### 9️⃣ Payment (TC_PAYMENT_001 → TC_PAYMENT_003)
- ✅ Generate payment URL
- ✅ Payment callback (success/fail)
- ✅ Order status update

**Trọng tâm**: VNPay integration

### 🔟 Discounts (TC_DISCOUNT_001 → TC_DISCOUNT_007)
- ✅ Create (percentage/fixed)
- ✅ Validation (dates, duplicates)
- ✅ Apply discount
- ✅ Filter active discounts

**Trọng tâm**: Business rules, date validation

### 1️⃣1️⃣ Reports (TC_REPORT_001 → TC_REPORT_005)
- ✅ Daily/Monthly revenue
- ✅ Top products/customers
- ✅ Inventory status

**Trọng tâm**: Data aggregation, performance

### 1️⃣2️⃣ AI Chat (TC_CHAT_001 → TC_CHAT_002)
- ✅ Query shoe data
- ✅ Query discount data

**Trọng tâm**: AI integration

### 1️⃣3️⃣ Security (TC_SEC_001 → TC_SEC_004)
- ✅ Role-based access control
- ✅ Token expiration
- ✅ Unauthorized access

**Trọng tâng**: Authentication & Authorization

### 1️⃣4️⃣ Performance & Integration (TC_PERF_001 → TC_INT_002)
- ✅ Concurrent users
- ✅ Large dataset pagination
- ✅ End-to-end purchase flow
- ✅ Order fulfillment flow

**Trọng tâm**: System integration, performance

---

## 🎯 Priority Matrix

| Priority | Count | Description |
|----------|-------|-------------|
| **Critical** | 4 | Must pass before release |
| **High** | 45 | Core functionality |
| **Medium** | 38 | Important features |
| **Low** | 10 | Nice to have |

---

## 📝 Cách ghi chép kết quả test

### Bước 1: Chọn Test Case
Chọn test case từ file CSV theo thứ tự hoặc theo module

### Bước 2: Thực hiện test
1. Đọc **Preconditions** - đảm bảo điều kiện đủ
2. Đọc **Test Steps** - thực hiện từng bước
3. Sử dụng **Test Data** được gợi ý
4. So sánh **Expected Result** với kết quả thực tế

### Bước 3: Ghi kết quả

#### ✅ Test PASSED
```
Actual Result: Kết quả đúng như mong đợi
Status: Passed
Tested By: Tên bạn
Test Date: 10/12/2025
Notes: (để trống hoặc ghi chú thêm)
```

#### ❌ Test FAILED
```
Actual Result: Ghi rõ lỗi xảy ra (VD: Status 500, Error message...)
Status: Failed
Tested By: Tên bạn
Test Date: 10/12/2025
Notes: Tham chiếu Bug ID (BUG_001)
```

#### 🚫 Test BLOCKED
```
Status: Blocked
Notes: Lý do block (VD: Backend not running, dependency failed)
```

### Bước 4: Báo cáo Bug (nếu có)
Mở file `BUG_REPORT_TEMPLATE.csv` và điền:
- Bug ID: BUG_XXX
- Module, Feature
- Severity: Critical/High/Medium/Low
- Priority: P1/P2/P3/P4
- Steps to Reproduce
- Related Test Case

---

## 📈 Theo dõi tiến độ

Sử dụng file `TEST_EXECUTION_SUMMARY.csv`:

### Cách tính:
```
Pass Rate = (Passed / Total) × 100%
```

### Cập nhật sau mỗi test cycle:
1. Tổng số test cases
2. Số Passed/Failed/Blocked
3. Pass Rate
4. Notes về vấn đề chính

---

## 🔄 Test Flow đề xuất

### Phase 1: Smoke Test (15-30 phút)
Test các chức năng cơ bản:
```
TC_AUTH_001 (Login)
TC_USER_001 (Register)
TC_SHOE_004 (Get Shoes)
TC_CART_001 (Add to Cart)
TC_ORDER_001 (Create Order)
```

### Phase 2: Functional Test (2-3 giờ)
Test toàn bộ 97 test cases theo thứ tự module

### Phase 3: Integration Test (1 giờ)
```
TC_INT_001 (Complete Purchase Flow)
TC_INT_002 (Order Fulfillment Flow)
```

### Phase 4: Security & Performance (1 giờ)
```
TC_SEC_001 → TC_SEC_004
TC_PERF_001 → TC_PERF_002
```

---

## 💡 Tips cho việc test

### 1. Chuẩn bị môi trường
```bash
# Đảm bảo backend đang chạy
curl http://localhost:8080/api/v1/actuator/health

# Đảm bảo database có dữ liệu
# Check: brands, shoes, users
```

### 2. Test Data mẫu
```json
// Admin account
Username: admin
Password: admin123

// Customer account
Username: customer01
Password: password123

// Manager account
Username: manager
Password: manager123

// Valid discount code
SUMMER2024
```

### 3. Sử dụng Postman Collection
- Import `postman_collection.json`
- Login trước để lấy token
- Token tự động lưu vào {{access_token}}

### 4. Ghi chú quan trọng
- Screenshot khi có lỗi
- Ghi rõ Response Body khi failed
- Check logs backend nếu 500 error
- Verify database state sau mỗi operation

### 5. Test theo nhóm chức năng
```
Ngày 1: Authentication + User Management
Ngày 2: Products (Brands + Shoes + Variants + Images)
Ngày 3: Shopping (Cart + Orders + Payment)
Ngày 4: Discounts + Reports + Security
Ngày 5: Integration + Performance + Regression
```

---

## 📊 Báo cáo cuối cùng

### Template báo cáo:
```
Test Execution Report
Date: [Ngày]
Tester: [Tên]

1. Summary
   - Total Test Cases: 97
   - Passed: X
   - Failed: Y
   - Blocked: Z
   - Pass Rate: XX%

2. Test Coverage
   - Authentication: X/9
   - User Management: X/7
   - (...)

3. Critical Issues Found
   - BUG_001: [Description]
   - BUG_002: [Description]

4. Recommendations
   - [Các vấn đề cần sửa trước khi release]

5. Sign-off
   - Tested by: [Tên]
   - Date: [Ngày]
   - Status: [Ready for Release / Not Ready]
```

---

## 🎓 Thuật ngữ kiểm thử

| Thuật ngữ | Giải thích |
|-----------|-----------|
| **Test Case** | Một trường hợp test cụ thể |
| **Test Scenario** | Kịch bản test (có thể chứa nhiều test cases) |
| **Test Suite** | Tập hợp các test cases |
| **Smoke Test** | Test nhanh các chức năng cơ bản |
| **Regression Test** | Test lại sau khi sửa bug |
| **Integration Test** | Test tích hợp giữa các module |
| **End-to-End Test** | Test toàn bộ luồng từ đầu đến cuối |
| **Pass Rate** | Tỷ lệ test passed |
| **Bug Severity** | Mức độ nghiêm trọng của bug |
| **Bug Priority** | Độ ưu tiên sửa bug |

---

## 📞 Hỗ trợ

Nếu cần hỗ trợ:
1. Check POSTMAN_GUIDE.md cho hướng dẫn API
2. Review code trong `back-end/src/main/java`
3. Check logs trong terminal backend
4. Xem database để verify data

**Happy Testing! 🚀**
