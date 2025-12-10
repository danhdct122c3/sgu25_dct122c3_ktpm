# Gói Tài Liệu Kiểm Thử Hoàn Chỉnh - Hệ Thống ShoeShop Store

## 1. TEST CASE MASTER (Tài Liệu Gốc)

**Mục đích:** Tài liệu gốc chứa tất cả test case cho frontend và backend, là cơ sở cho các tài liệu kiểm thử khác.

**Trách nhiệm thực hiện:**
- Ai chạy: QA Team
- Công cụ:  Excel
- Trigger: Trong quá trình phát triển và trước release

| TC_ID | Level | Role | Flow | Title | Pre-condition | Steps | Expected Result | Test Data |
|-------|-------|------|------|--------|---------------|--------|-----------------|-----------|
| TC-001 | FE | Customer | Login/Logout | Đăng nhập thành công | User chưa đăng nhập | 1. Mở trang login<br>2. Nhập email/password hợp lệ<br>3. Click "Đăng nhập" | Chuyển đến trang chủ, hiển thị tên user | email: customer@example.com, password: password123 |
| TC-002 | FE | Customer | Login/Logout | Đăng nhập thất bại | User chưa đăng nhập | 1. Mở trang login<br>2. Nhập email sai<br>3. Click "Đăng nhập" | Hiển thị lỗi "Invalid credentials" | email: wrong@example.com, password: password123 |
| TC-003 | BE | Customer | Login/Logout | Token validation | User đã đăng nhập | 1. Gửi request với JWT<br>2. Verify token | Trả về user info | JWT: valid_token |
| TC-004 | FE | Customer | View Catalog | Xem danh sách sản phẩm | User đã đăng nhập | 1. Mở trang catalog<br>2. Kiểm tra danh sách | Hiển thị 20 sản phẩm đầu | N/A |
| TC-005 | FE | Customer | View Catalog | Lọc sản phẩm theo brand | User đã đăng nhập | 1. Chọn brand filter<br>2. Click "Áp dụng" | Chỉ hiển thị sản phẩm của brand đó | brand: Nike |
| TC-006 | BE | Customer | View Catalog | API lấy sản phẩm | Database có dữ liệu | 1. Gọi GET /shoes<br>2. Parse response | Trả về JSON array products | page: 0, size: 20 |
| TC-007 | FE | Customer | Order Checkout | Thêm sản phẩm vào giỏ | User đã đăng nhập | 1. Click "Add to Cart"<br>2. Kiểm tra giỏ hàng | Sản phẩm xuất hiện trong giỏ | productId: 1, quantity: 2 |
| TC-008 | FE | Customer | Order Checkout | Checkout với thông tin hợp lệ | Giỏ có sản phẩm | 1. Click "Checkout"<br>2. Nhập địa chỉ<br>3. Xác nhận | Tạo order thành công | address: "123 Main St", phone: "0123456789" |
| TC-009 | BE | Customer | Order Checkout | Tạo order API | User authenticated | 1. POST /orders với cart data<br>2. Verify response | Trả về orderId, status: CREATED | cart: [{productId:1, qty:1}], shipping: {...} |
| TC-010 | FE | Customer | Payment | Thanh toán COD | Order đã tạo | 1. Chọn COD<br>2. Xác nhận thanh toán | Hiển thị "Order placed successfully" | N/A |
| TC-011 | FE | Customer | Payment | Khởi tạo VNPay | Order đã tạo | 1. Chọn VNPay<br>2. Chọn ngân hàng<br>3. Click "Thanh toán" | Redirect đến VNPay | bankCode: VCB |
| TC-012 | BE | Customer | Payment | VNPay callback success | Payment initiated | 1. Nhận callback với vnp_ResponseCode=00<br>2. Update order status | Order status → CONFIRMED | vnp_ResponseCode: "00", orderId: "ORD001" |
| TC-013 | BE | Customer | Payment | VNPay callback failure | Payment initiated | 1. Nhận callback với vnp_ResponseCode=01<br>2. Maintain order status | Order status giữ nguyên | vnp_ResponseCode: "01", orderId: "ORD001" |
| TC-014 | FE | Customer | Order Tracking | Xem lịch sử đơn hàng | User đã đăng nhập | 1. Mở trang "Order History"<br>2. Kiểm tra danh sách | Hiển thị các đơn hàng của user | N/A |
| TC-015 | FE | Customer | Order Tracking | Xem chi tiết đơn hàng | User có đơn hàng | 1. Click vào đơn hàng<br>2. Kiểm tra thông tin | Hiển thị đầy đủ chi tiết | orderId: ORD001 |
| TC-016 | BE | Customer | Order Tracking | API lấy orders | User authenticated | 1. GET /orders/user<br>2. Verify response | Trả về array orders của user | userId: 1 |
| TC-017 | FE | Staff | Order Management | Xem danh sách đơn hàng | Staff đã đăng nhập | 1. Mở trang quản lý<br>2. Kiểm tra danh sách | Hiển thị tất cả đơn hàng | N/A |
| TC-018 | FE | Staff | Order Management | Cập nhật trạng thái đơn | Staff đã đăng nhập | 1. Chọn đơn hàng<br>2. Thay đổi status<br>3. Lưu | Status cập nhật thành công | orderId: ORD001, newStatus: CONFIRMED |
| TC-019 | BE | Staff | Order Management | Update order status API | Staff authenticated | 1. PUT /orders/{id}/status<br>2. Verify DB update | Status changed, audit logged | orderId: ORD001, status: CONFIRMED |
| TC-020 | FE | Manager | Order Management | Tạo mã giảm giá | Manager đã đăng nhập | 1. Mở trang discount<br>2. Nhập thông tin<br>3. Tạo | Mã giảm giá được tạo | code: SAVE10, percentage: 10 |
| TC-021 | FE | Admin | Order Management | Quản lý user | Admin đã đăng nhập | 1. Mở trang user management<br>2. Thay đổi role<br>3. Lưu | Role cập nhật thành công | userId: 2, newRole: MANAGER |
| TC-022 | BE | Admin | Order Management | Update user role API | Admin authenticated | 1. PUT /users/{id}/role<br>2. Verify DB update | Role changed, permissions updated | userId: 2, role: MANAGER |
| TC-023 | FE | Customer | Authentication & Authorization | Truy cập trang admin | Customer đã đăng nhập | 1. Nhập URL admin<br>2. Kiểm tra kết quả | Redirect về unauthorized | N/A |
| TC-024 | BE | Customer | Authentication & Authorization | API authorization check | Customer JWT | 1. Gọi admin API<br>2. Verify response | Return 403 Forbidden | JWT: customer_token |
| TC-025 | FE | Staff | Authentication & Authorization | Truy cập manager features | Staff đã đăng nhập | 1. Thử tạo discount<br>2. Kiểm tra kết quả | Hiển thị lỗi "Insufficient permissions" | N/A |

## 2. UNIT TEST DOCUMENT (Developer)

**Mục đích:** Tài liệu unit test cho developer, validate logic của từng component. Không kế thừa từ Test Case Master nhưng có mapping ngang để liên kết.

**Trách nhiệm thực hiện:**
- Ai chạy: Developer
- Công cụ: JUnit (Backend), Jest (Frontend)
- Trigger: Local development + GitHub Actions CI

| UT_ID | Unit Under Test (class/method) | Purpose | Test Logic | Expected Output | Mapping to TC |
|-------|--------------------------------|---------|------------|-----------------|---------------|
| UT-001 | AuthenticationService.authenticateUser() | Validate user login logic | Mock user repo, test password verification | Return User object or throw exception | TC-003 |
| UT-002 | ProductService.getProducts() | Test catalog filtering | Mock DB, test query building | Return filtered product list | TC-006 |
| UT-003 | CartService.calculateTotal() | Validate price calculation | Input cart items, test sum logic | Return correct total | TC-009 |
| UT-004 | PaymentService.validateVNPaySignature() | Test payment security | Mock callback data, test HMAC validation | Return true/false | TC-012 |
| UT-005 | OrderService.updateOrderStatus() | Test status transition rules | Mock order, test state machine | Update status or throw exception | TC-019 |
| UT-006 | LoginForm.validateEmail() | Test email validation | Input various email formats | Return boolean | TC-001 |
| UT-007 | CheckoutForm.validateAddress() | Test address validation | Input address data, test required fields | Return validation result | TC-008 |
| UT-008 | DiscountUtils.calculateDiscountedPrice() | Test discount calculation | Input price and discount %, test math | Return discounted price | TC-020 |

## 3. INTEGRATION TEST DOCUMENT (API)

**Mục đích:** Tài liệu kiểm thử tích hợp API, subset của Test Case Master, focus vào tương tác giữa components.

**Trách nhiệm thực hiện:**
- Ai chạy: Developer + QA
- Công cụ: Postman/Newman, SpringBootTest + Testcontainers
- Trigger: CI/CD pipeline sau unit tests

| INT_ID | TC_Parent | API Endpoint | Method | Request Body | Steps | Expected Response | DB Validation |
|--------|-----------|--------------|--------|--------------|--------|-------------------|--------------|
| INT-003 | TC-003 | /auth/token | POST | {"email":"customer@example.com","password":"password123"} | 1. Send request<br>2. Verify JWT in response | 200 OK, JWT token | User login logged |
| INT-006 | TC-006 | /shoes | GET | ?page=0&size=20 | 1. Send request<br>2. Parse JSON response | 200 OK, products array | Query executed on products table |
| INT-009 | TC-009 | /orders | POST | {"cart":[...],"shipping":{...}} | 1. Send with JWT<br>2. Verify order creation | 201 Created, orderId | Order inserted, inventory reduced |
| INT-012 | TC-012 | /payment/callback | POST | VNPay callback params | 1. Send callback<br>2. Verify signature | 200 OK | Order status updated to CONFIRMED |
| INT-016 | TC-016 | /orders/user | GET | Header: Bearer JWT | 1. Send authenticated request<br>2. Filter by userId | 200 OK, user's orders | Query on orders table with user_id |
| INT-019 | TC-019 | /orders/{id}/status | PUT | {"status":"CONFIRMED"} | 1. Send with staff JWT<br>2. Update status | 200 OK | Order status changed, audit logged |
| INT-022 | TC-022 | /users/{id}/role | PUT | {"role":"MANAGER"} | 1. Send with admin JWT<br>2. Update role | 200 OK | User role updated in database |

## 4. SYSTEM TEST DOCUMENT (UI + E2E)

**Mục đích:** Tài liệu kiểm thử hệ thống end-to-end, subset của Test Case Master, focus vào luồng hoàn chỉnh từ UI đến backend.

**Trách nhiệm thực hiện:**
- Ai chạy: QA Team
- Công cụ: Manual testing + Selenium
- Trigger: Sau khi deploy lên Staging environment

| ST_ID | TC_Parent | Role | Scenario | Steps | Expected UI Behavior | Test Data | Result |
|-------|-----------|------|----------|--------|----------------------|-----------|--------|
| ST-001 | TC-001 | Customer | Complete login flow | 1. Open login page<br>2. Enter credentials<br>3. Submit<br>4. Verify dashboard | Redirect to home, show user menu | email: customer@example.com | PASS |
| ST-008 | TC-008 | Customer | End-to-end checkout | 1. Add to cart<br>2. Go to checkout<br>3. Enter shipping<br>4. Confirm order<br>5. Verify success | Order confirmation page, email sent | product: Nike Air, qty: 1 | PASS |
| ST-011 | TC-011 | Customer | VNPay payment flow | 1. Create order<br>2. Select VNPay<br>3. Choose bank<br>4. Redirect to VNPay<br>5. Complete payment<br>6. Return to success page | VNPay gateway opens, return with success | order: ORD001, bank: VCB | PASS |
| ST-014 | TC-014 | Customer | Order tracking | 1. Login<br>2. Go to order history<br>3. Click order<br>4. Check status<br>5. Verify details | Order list shows, details accurate | existing order | PASS |
| ST-018 | TC-018 | Staff | Order management | 1. Login as staff<br>2. View orders<br>3. Update status<br>4. Verify change | Status updated, notification sent | order: ORD001, status: CONFIRMED | PASS |
| ST-023 | TC-023 | Customer | Authorization check | 1. Login as customer<br>2. Try access admin page<br>3. Verify block | Redirect to unauthorized page | customer login | PASS |

## 5. UAT DOCUMENT (Business Acceptance)

**Mục đích:** Tài liệu kiểm thử chấp nhận người dùng, business-friendly, focus vào giá trị nghiệp vụ.

**Trách nhiệm thực hiện:**
- Ai chạy: PO / Business Users / Lecturers
- Công cụ: Manual testing trên UAT environment
- Trigger: Trước khi chấp nhận production

| UAT_ID | TC_Parent | Business Scenario | Acceptance Criteria | Steps | Expected Outcome |
|--------|-----------|-------------------|---------------------|--------|------------------|
| UAT-001 | TC-001 | Khách hàng đăng nhập thành công | Hệ thống cho phép truy cập cá nhân | 1. Mở trang đăng nhập<br>2. Nhập thông tin<br>3. Đăng nhập | Truy cập được tài khoản cá nhân |
| UAT-008 | TC-008 | Hoàn tất đặt hàng | Đơn hàng được tạo và xác nhận | 1. Thêm sản phẩm<br>2. Điền thông tin giao hàng<br>3. Đặt hàng | Nhận xác nhận đơn hàng |
| UAT-010 | TC-010 | Thanh toán khi nhận hàng | Không mất phí giao dịch | 1. Chọn COD<br>2. Xác nhận | Đặt hàng thành công |
| UAT-011 | TC-011 | Thanh toán online | Thanh toán an toàn qua VNPay | 1. Chọn VNPay<br>2. Thanh toán<br>3. Xác nhận | Đơn hàng được xử lý |
| UAT-014 | TC-014 | Theo dõi đơn hàng | Xem trạng thái đơn hàng | 1. Đăng nhập<br>2. Xem lịch sử<br>3. Kiểm tra trạng thái | Biết được tình trạng đơn hàng |
| UAT-018 | TC-018 | Nhân viên xử lý đơn | Cập nhật trạng thái kịp thời | 1. Xem đơn hàng<br>2. Cập nhật trạng thái<br>3. Lưu thay đổi | Trạng thái cập nhật chính xác |
| UAT-023 | TC-023 | Bảo mật theo vai trò | Chỉ truy cập được chức năng phù hợp | 1. Đăng nhập<br>2. Thử truy cập không được phép | Bị chặn truy cập không hợp lệ |