# Chương 4 – Thiết kế kiểm thử (Test Design)

Chương 4 trình bày thiết kế kiểm thử cho hệ thống ShoeShop E-commerce từ 4 góc nhìn (views) chính: tiếp cận theo pipeline, mô hình chữ V, phương pháp kiểm thử (hộp trắng/đen, Selenium, tự động), và thủ công vs tự động. Mỗi view thể hiện một khía cạnh quan trọng của hoạt động kiểm thử, đảm bảo coverage toàn diện từ verification đến validation.

## 4.1. View 1 – Tiếp cận theo Pipeline

### 4.1.1. Mô tả rõ pipeline tiếp cận

Pipeline kiểm thử ShoeShop được thiết kế theo mô hình end-to-end, tích hợp với CI/CD pipeline. Quy trình bắt đầu từ phân tích SRS/Use Case và kết thúc bằng báo cáo chất lượng.
``
**Luồng pipeline tổng thể:**
1. **Input:** SRS, Use Case, Acceptance Criteria (UAC)
2. **Verification:** Unit Test, Integration Test, System Test
3. **Validation:** UAT, User Acceptance Test
4. **Output:** Test Report (20% summary), Detailed Report Sheet

### 4.1.2. Mapping verification và validation

| Verification (Xác minh) | Validation (Xác nhận) |
|-------------------------|----------------------|
| Unit Test - Test từng component | UAT - Test theo yêu cầu người dùng |
| Integration Test - Test tương tác | System Test - Test end-to-end |
| Code Review - Review implementation | Acceptance Test - Test UAC |

### 4.1.3. Unit-test, integration test, UAC, System test

| Loại Test | Mục đích | Áp dụng cho 4 quy trình chính | Công cụ |
|-----------|----------|------------------------------|---------|
| Unit Test | Test individual functions/methods | - Tìm kiếm: validateSearchLogic()<br>- Cart: calculateTotal()<br>- Order: validatePayment()<br>- Authorization: checkRole() | JUnit, Mockito |
| Integration Test | Test component interactions | - Cart + Order API<br>- Payment + Order DB<br>- Auth + Role service<br>- Search + Product DB | Spring Test, TestContainers |
| System Test | Test complete workflows | - End-to-end purchase flow<br>- Login → Search → Cart → Checkout → Payment<br>- Order tracking full cycle<br>- Role-based access control | Selenium, Postman |
| UAC Test | Business requirement validation | - Customer can complete purchase<br>- Staff can update order status<br>- Payment methods work correctly<br>- Authorization enforced | Manual testing |

## 4.2. View 2 – Tiếp cận theo mô hình chữ V

### 4.2.1. Tổng quan mô hình chữ V

Mô hình chữ V (V-Model) là mô hình SDLC nhấn mạnh việc kiểm thử sớm và song song với các giai đoạn phát triển. Bên trái là Verification (xác minh – không chạy code), bên phải là Validation (xác nhận – thực thi code). Mỗi giai đoạn phát triển đều có một giai đoạn kiểm thử tương ứng.

### 4.2.2. Giai đoạn Verification (Xác minh – không dùng code)

| Giai đoạn phát triển | Mô tả | Kiểm thử tương ứng |
|---------------------|--------|-------------------|
| Phân tích yêu cầu kinh doanh | Làm rõ nhu cầu khách hàng, xây dựng kế hoạch kiểm thử chấp nhận (Acceptance Test) và bộ UAT ban đầu | Acceptance Test Plan |
| Thiết kế hệ thống | Xác định chức năng tổng thể của hệ thống và tạo ra các bài kiểm thử hệ thống (System Test) | System Test Plan |
| Thiết kế kiến trúc phần mềm | Xác định các thành phần, cách chúng tương tác; tạo kế hoạch kiểm thử tích hợp (Integration Test Plan) | Integration Test Plan |
| Thiết kế module | Thiết kế chi tiết từng module, database, I/O, xử lý lỗi; xây dựng kế hoạch kiểm thử đơn vị (UTP – Unit Test Plan) | Unit Test Plan |
| Triển khai viết code | Lập trình từng module theo thiết kế và tối ưu code khi cần | Code Review, Static Analysis |

### 4.2.3. Giai đoạn Validation (Xác nhận – thực thi code)

| Giai đoạn kiểm thử | Mô tả | Áp dụng cho ShoeShop |
|-------------------|--------|---------------------|
| Kiểm thử đơn vị (Unit Test) | Dùng UTP để kiểm tra từng module, phát hiện lỗi cục bộ và xác minh logic | Test CartService.calculateTotal(), OrderService.updateStatus() |
| Kiểm thử tích hợp (Integration Test) | Dựa trên kế hoạch kiểm thử tích hợp để đánh giá sự giao tiếp giữa các module | Test Cart + Order API, Payment + Database integration |
| Kiểm thử hệ thống (System Test) | Đánh giá toàn bộ hệ thống để kiểm tra xem có đáp ứng yêu cầu ban đầu hay không | End-to-end purchase flow, multi-role access control |
| Kiểm thử chấp nhận (UAT) | Kiểm tra trong môi trường thực tế để xác nhận hệ thống đáp ứng mong đợi của người dùng | Customer complete purchase, Staff manage orders |

### 4.2.3. Demo SRS/USECASE và sinh test-scenarios

**SRS Example - FR-SEARCH-01: Tìm kiếm sản phẩm**
- **Functional Requirement:** Hệ thống cho phép customer tìm kiếm sản phẩm theo từ khóa, thương hiệu, giá.
- **Acceptance Criteria (UAC):**
  - Tìm thấy sản phẩm nếu có từ khóa khớp
  - Hiển thị "Không tìm thấy" nếu không có kết quả
  - Thời gian response < 2 giây

**Use Case: UC-SEARCH-01 - Tìm kiếm sản phẩm**
- **Actor:** Customer
- **Pre-condition:** Customer đã login, ở trang catalog
- **Main Flow:**
  1. Customer nhập từ khóa vào search box
  2. System validate input (không rỗng, > 2 ký tự)
  3. System query database theo từ khóa
  4. System trả về danh sách sản phẩm
  5. Customer xem kết quả
- **Alternative Flow:**
  - A1: Từ khóa rỗng → Hiển thị lỗi
  - A2: Không tìm thấy → Hiển thị message

**Generated Test Scenarios từ Use Case:**

| Test Scenario ID | Description | Test Steps | Expected Result | Test Data |
|------------------|-------------|------------|-----------------|-----------|
| TS-SEARCH-01 | Valid search with results | 1. Login as customer<br>2. Go to catalog<br>3. Enter "Nike"<br>4. Click search | Display products containing "Nike" | keyword: "Nike" |
| TS-SEARCH-02 | Search with no results | 1. Enter "xyz123nonexistent"<br>2. Click search | Display "Không tìm thấy sản phẩm" | keyword: "xyz123" |
| TS-SEARCH-03 | Empty search | 1. Leave search box empty<br>2. Click search | Display validation error | keyword: "" |
| TS-SEARCH-04 | Search performance | 1. Enter valid keyword<br>2. Measure response time | Response < 2 seconds | keyword: "Adidas" |

## 4.3. View 3 – Phương pháp kiểm thử (White Box, Black Box, Selenium, Automated Testing)

### 4.3.1. Kĩ thuật test: Hộp trắng (White Box)

White box testing kiểm tra logic internal, code paths, và cấu trúc code.

| Kỹ thuật | Áp dụng cho ShoeShop | Ví dụ |
|----------|----------------------|-------|
| Statement Coverage | Test tất cả lines of code | Unit test cho search logic |
| Branch Coverage | Test tất cả decision points | If-else trong payment validation |
| Path Coverage | Test tất cả execution paths | Multiple paths trong order status update |

**Ví dụ White Box cho Authorization:**
```java
public boolean checkRole(User user, String requiredRole) {
    if (user == null) return false;  // Branch 1
    if (user.getRole() == null) return false;  // Branch 2
    return user.getRole().equals(requiredRole);  // Branch 3
}
```
- Test cases: user=null, user.role=null, user.role != required, user.role == required

### 4.3.2. Kĩ thuật test: Hộp đen (Black Box)

Black box testing kiểm tra external behavior, không quan tâm internal logic.

| Kỹ thuật | Áp dụng cho ShoeShop | Ví dụ |
|----------|----------------------|-------|
| Equivalence Partitioning | Chia input thành valid/invalid partitions | Search keywords: valid (3-50 chars), invalid (<3, >50, special chars) |
| Boundary Value Analysis | Test boundary values | Price range: 0, 1, 999999, 1000000 |
| Decision Table | Test combinations of conditions | Payment: COD + valid address, VNPay + invalid card |
| State Transition | Test state changes | Order status: CREATED → CONFIRMED → SHIPPED → DELIVERED |

### 4.3.3. Selenium - Kiểm thử tự động UI

Selenium được sử dụng cho automated UI testing của 4 quy trình chính.

**Ví dụ Selenium Script cho Search Process:**
```java
@Test
public void testProductSearch() {
    // Navigate to catalog page
    driver.get("http://localhost:3000/catalog");

    // Enter search keyword
    WebElement searchBox = driver.findElement(By.id("search-input"));
    searchBox.sendKeys("Nike");

    // Click search button
    WebElement searchBtn = driver.findElement(By.id("search-btn"));
    searchBtn.click();

    // Verify results
    WebElement results = driver.findElement(By.className("product-list"));
    Assert.assertTrue(results.getText().contains("Nike"));
}
```

**Selenium Test Cases cho 4 quy trình:**
- **Search:** Input keywords, verify results display
- **Cart:** Add products, verify cart updates, calculate total
- **Order:** Complete checkout, verify order creation
- **Authorization:** Login with different roles, verify access control

### 4.3.4. Kiểm thử tự động (Automated Testing)

Automated testing bao gồm unit, integration, UI, và API testing.

| Loại Automation | Công cụ | Áp dụng cho quy trình | Ví dụ |
|----------------|---------|----------------------|-------|
| Unit Automation | JUnit, Mockito | Tất cả services | Test CartService.calculateTotal() |
| API Automation | RestAssured, Postman | Order, Payment APIs | Test order creation endpoint |
| UI Automation | Selenium, Cypress | Search, Cart, Checkout | Test end-to-end purchase flow |
| Performance Automation | JMeter | Search, Order under load | Test 100 concurrent searches |

## 4.4. View 4 – Kiểm thử thủ công vs Tự động

### 4.4.1. Khi nào dùng Manual Testing

Manual testing phù hợp cho các trường hợp cần đánh giá chủ quan và khám phá.

| Tình huống | Lý do | Áp dụng cho quy trình |
|------------|--------|----------------------|
| UI/UX Evaluation | Cần đánh giá trải nghiệm người dùng | Search interface, cart usability |
| Exploratory Testing | Khám phá edge cases | Payment error scenarios |
| Usability Testing | Đánh giá tính dễ sử dụng | Order tracking workflow |
| UAT | Business validation | Complete customer journeys |
| Ad-hoc Testing | Test không có script | Unexpected error handling |

### 4.4.2. Khi nào dùng Automated Testing

Automation phù hợp cho repetitive, regression, và performance testing.

| Tình huống | Lý do | Áp dụng cho quy trình |
|------------|--------|----------------------|
| Regression Testing | Verify sau mỗi change | All 4 quy trình sau code changes |
| Load Testing | Simulate multiple users | Search, cart under high load |
| API Testing | Test backend services | Order, payment APIs |
| Cross-browser Testing | Verify compatibility | All UI flows trên multiple browsers |
| Smoke Testing | Quick validation | Basic functionality của 4 quy trình |

### 4.4.3. Design workflow tổng thể cho 4 quy trình

**Overall Design Workflow cho 4 quy trình chính:**

```
SRS/Use Case Analysis
    ↓
Identify Test Scenarios (20% coverage)
    ↓
Design Test Cases (Manual + Automated)
    ↓
Create Test Data Sheets
    ↓
Execute Manual Tests (Exploratory)
    ↓
Implement Automated Tests (Selenium, JUnit)
    ↓
Integration Testing (API + DB)
    ↓
System Testing (End-to-End)
    ↓
UAT Validation
    ↓
Generate Test Report (20% Summary)
    ↓
Detailed Report Sheet (Full Coverage)
```

**Workflow chi tiết cho từng quy trình:**

1. **Tìm kiếm sản phẩm (Product Search):**
   - Manual: UI testing, usability
   - Automated: API testing, performance
   - Data: Valid keywords, edge cases

2. **Cart (Giỏ hàng):**
   - Manual: Add/remove items, calculate total
   - Automated: Cart API, persistence
   - Data: Multiple items, discounts

3. **Order (Checkout, Payment, Tracking):**
   - Manual: Complete purchase flow, payment methods
   - Automated: Order creation, status updates
   - Data: Valid/invalid payments, order states

4. **Authorization (Phân quyền):**
   - Manual: Role-based access, permissions
   - Automated: Security testing, access control
   - Data: Different user roles, restricted actions

### 4.4.4. Báo cáo chỉ 20%, 1 sheet riêng cho báo cáo chi tiết

**Test Report Summary (20% coverage):**

| Quy trình | Test Cases | Pass | Fail | Coverage |
|-----------|------------|------|------|----------|
| Tìm kiếm | 15 | 14 | 1 | 93% |
| Cart | 20 | 19 | 1 | 95% |
| Order | 25 | 23 | 2 | 92% |
| Authorization | 10 | 10 | 0 | 100% |
| **Total** | **70** | **66** | **4** | **94%** |

**Detailed Report Sheet:** Chi tiết tất cả test cases, steps, results, defects - nằm trong file Excel riêng.

### 4.4.5. Liệt kê các sheet cần thiết

Các sheet cần thiết trong Test Management Excel file:

1. **Use Case List** - Danh sách use cases cho 4 quy trình
2. **Test Scenario** - Scenarios generated từ use cases
3. **Test Case** - Chi tiết test cases với steps và expected results
4. **Test Data** - Dữ liệu test cho từng scenario
5. **Test Execution** - Kết quả chạy test
6. **Defect Report** - Danh sách defects found
7. **Test Summary** - Báo cáo tổng hợp (20% coverage)
8. **Detailed Report** - Báo cáo chi tiết đầy đủ
9. **Traceability Matrix** - Mapping requirements ↔ test cases
10. **Test Checklist** - Checklist review cho QA