# Phiên Q&A Làm Rõ Yêu Cầu Hệ Thống ShoeShop Store

## 1. Login / Logout

**Customer Questions:**
- Khi tôi đăng nhập thành công, tôi sẽ được chuyển đến trang nào?
- Nếu tôi quên mật khẩu thì làm thế nào để lấy lại?
- Tôi có thể đăng nhập đồng thời trên nhiều thiết bị không?
- Nếu tôi nhập sai mật khẩu nhiều lần thì có bị khóa tài khoản không?
- Sau khi đăng xuất, dữ liệu giỏ hàng của tôi có còn không?

**QA Follow-up Questions:**
- Tiêu chí chấp nhận cho đăng nhập thành công là gì - chỉ cần email/password đúng hay cần thêm xác thực?
- Khi đăng nhập thất bại, hệ thống hiển thị lỗi như thế nào - message cụ thể, lock account sau bao lần?
- JWT token có thời hạn bao lâu và được refresh như thế nào?
- Nếu user đã login mà cố tình truy cập admin page thì điều gì xảy ra?
- Có validation gì cho email format và password strength không?
- Khi logout, session được clear như thế nào - chỉ FE hay cả BE?
- Có audit log cho login attempts không?
- Xử lý concurrent login từ cùng account như thế nào?

**Developer Answers:**
Đối với Customer: Khi đăng nhập thành công, bạn sẽ được chuyển đến trang chủ (home page) với thông báo chào mừng. Quên mật khẩu thì dùng tính năng "Forgot Password" - nhập email và nhận OTP qua email để reset. Bạn có thể đăng nhập đồng thời trên nhiều thiết bị. Nhập sai mật khẩu 5 lần liên tiếp sẽ khóa tài khoản 15 phút. Dữ liệu giỏ hàng được lưu trong database nên vẫn còn sau logout.

Đối với QA: Tiêu chí đăng nhập thành công là email tồn tại, password đúng, account active. Lỗi hiển thị cụ thể: "Invalid credentials" cho sai password, "Account locked" cho quá nhiều lần thất bại. JWT token có thời hạn 24 giờ, refresh bằng API riêng. Truy cập admin page khi không phải role admin sẽ redirect về "/unauthorized" với HTTP 403. Email validation theo RFC 5322, password ít nhất 8 ký tự có chữ hoa/thường/số. Logout clear cả FE localStorage và BE invalidate token. Có audit log ghi login attempts với IP và timestamp. Concurrent login được phép, mỗi session có JWT riêng.

## 2. View Catalog (listing, filtering, sorting, product detail)

**Customer Questions:**
- Tôi có thể tìm sản phẩm theo tên không?
- Khi lọc theo giá, có hiển thị số lượng sản phẩm không?
- Sản phẩm hết hàng có hiển thị khác không?
- Tôi có thể xem ảnh sản phẩm phóng to không?
- Có giới hạn số sản phẩm hiển thị trên một trang không?

**QA Follow-up Questions:**
- Khi search không có kết quả, hệ thống hiển thị như thế nào?
- Filtering có support multiple criteria cùng lúc không (brand + price + category)?
- Sorting có maintain filter state không?
- Product detail có load lazy cho images không?
- Có pagination cho large catalog không, bao nhiêu items per page?
- Khi product hết stock, có disable add to cart không?
- Có validation cho invalid product IDs không?
- Performance requirements cho catalog load time?

**Developer Answers:**
Đối với Customer: Có thể search theo tên sản phẩm với autocomplete. Khi lọc giá, hiển thị số lượng kết quả. Sản phẩm hết hàng có badge "Out of Stock" và disable nút "Add to Cart". Ảnh sản phẩm có thể click để phóng to trong modal. Mỗi trang hiển thị 20 sản phẩm, có pagination.

Đối với QA: Search không có kết quả hiển thị "No products found" với gợi ý search khác. Filtering support multiple criteria (AND logic). Sorting maintain filter state. Product detail load images lazy với loading spinner. Pagination với 20 items/page, có next/prev/first/last. Hết stock disable add to cart và show message. Invalid product ID trả về 404. Catalog load time < 2 giây cho 1000 products.

## 3. Order Checkout (cart → shipping → confirm)

**Customer Questions:**
- Tôi có thể sửa số lượng sản phẩm trong giỏ hàng không?
- Nếu địa chỉ giao hàng sai thì có thể sửa sau khi đặt hàng không?
- Có áp dụng mã giảm giá không và như thế nào?
- Nếu sản phẩm hết hàng trong lúc checkout thì sao?
- Tôi có thể hủy đơn hàng ngay sau khi đặt không?

**QA Follow-up Questions:**
- Validation rules cho shipping address là gì?
- Khi nào thì discount code được áp dụng - trước hay sau tax?
- Cart persistence across sessions như thế nào?
- Race condition khi multiple users checkout cùng product?
- Maximum items per order là bao nhiêu?
- Checkout form có auto-save draft không?
- Error handling khi payment fails midway?
- Order number generation rules?

**Developer Answers:**
Đối với Customer: Có thể sửa số lượng trong giỏ hàng trước checkout. Địa chỉ giao hàng có thể sửa trong vòng 30 phút sau đặt hàng. Mã giảm giá nhập ở bước review order. Nếu hết hàng, hệ thống thông báo và remove khỏi cart. Có thể hủy đơn trong vòng 1 giờ nếu chưa được xử lý.

Đối với QA: Shipping address required: name, phone, address (min 10 chars), city, postal code. Discount áp dụng trước tax trên subtotal. Cart lưu trong DB với session ID và user ID. Race condition handled bằng optimistic locking. Max 50 items per order. Checkout form auto-save mỗi 30 giây. Payment failure rollback order và restore inventory. Order number format: ORD + timestamp + random 4 digits.

## 4. Payment (COD + VNPay)

**Customer Questions:**
- Thanh toán COD có mất phí không?
- VNPay hỗ trợ những ngân hàng nào?
- Nếu thanh toán VNPay thất bại thì tiền có bị trừ không?
- Thời gian xử lý thanh toán VNPay là bao lâu?
- Tôi có thể thay đổi phương thức thanh toán sau khi đặt hàng không?

**QA Follow-up Questions:**
- VNPay integration có handle callback security như thế nào?
- Idempotency cho duplicate callbacks được implement ra sao?
- Timeout handling cho VNPay transactions?
- COD status updates khi delivery completed?
- Payment amount validation với order total?
- Refund process cho failed VNPay payments?
- Currency support và exchange rates?
- PCI compliance cho payment data?

**Developer Answers:**
Đối với Customer: COD không mất phí giao dịch. VNPay hỗ trợ tất cả ngân hàng Việt Nam chính. Thanh toán thất bại không trừ tiền. Xử lý VNPay trong vòng 5-10 phút. Không thể thay đổi phương thức sau đặt hàng.

Đối với QA: VNPay callback validate signature bằng HMAC-SHA256 với secret key. Idempotency check transaction ID, ignore duplicates. Timeout 15 phút, order status → EXPIRED. COD status update khi delivery partner confirm. Payment amount must match order total exactly. Refund qua VNPay API trong 24h. Only VND supported. Payment data không lưu trong hệ thống, chỉ transaction references.

## 5. Order Tracking (Customer)

**Customer Questions:**
- Tôi có thể xem lịch sử đơn hàng của mình không?
- Trạng thái đơn hàng được cập nhật như thế nào?
- Nếu đơn hàng bị trễ thì tôi biết được không?
- Tôi có thể liên hệ hỗ trợ khi có vấn đề không?
- Có thông báo khi đơn hàng được giao không?

**QA Follow-up Questions:**
- Status transition rules và allowed changes?
- Real-time updates hay periodic refresh?
- Customer notification channels (email, SMS, in-app)?
- Order search và filtering capabilities?
- Historical status tracking retention period?
- Privacy controls cho order visibility?
- Integration với delivery tracking APIs?
- Escalation process cho delayed orders?

**Developer Answers:**
Đối với Customer: Có thể xem lịch sử đơn hàng với filter theo status/date. Status cập nhật real-time khi staff thay đổi. Có estimated delivery time, trễ thì có thông báo. Chat support và hotline. Email/SMS notification khi shipped và delivered.

Đối với QA: Status transitions: CREATED → CONFIRMED → PREPARING → SHIPPED → DELIVERED. Real-time via WebSocket. Notifications: email cho major status, SMS cho delivery. Search by order ID, filter by status/date. History retained 2 năm. Only customer's own orders visible. Integration với Viettel Post API. Escalation: auto-notify manager sau 48h delay.

## 6. Order Management (Staff/Manager/Admin)

**Customer Questions:**
- Nhân viên có thể xem đơn hàng của khách hàng khác không?
- Quản lý có thể tạo mã giảm giá không?
- Admin có thể xóa đơn hàng không?
- Khi đơn hàng được xác nhận thì khách hàng có biết không?
- Có thể thay đổi địa chỉ giao hàng sau khi xác nhận không?

**QA Follow-up Questions:**
- Role-based permissions matrix cho order operations?
- Audit logging cho order changes?
- Bulk operations support (multiple orders)?
- Order assignment cho staff members?
- Escalation workflow từ staff → manager → admin?
- Data validation cho order modifications?
- Conflict resolution cho concurrent updates?
- Reporting capabilities cho management?

**Developer Answers:**
Đối với Customer: Staff chỉ xem đơn hàng assigned, không xem private info. Manager có thể tạo discount codes. Admin có thể delete orders trong 24h. Khách hàng nhận email khi order confirmed. Địa chỉ có thể sửa trong 1h sau confirm.

Đối với QA: Permissions: Staff read/write assigned orders, Manager all orders + discounts, Admin full CRUD. Audit log ghi user, timestamp, old/new values. Bulk update cho status changes. Orders auto-assigned hoặc manual. Escalation: staff mark "needs attention", auto-escalate sau 4h. Validation: address format, phone regex. Optimistic locking prevent conflicts. Reports: daily metrics, performance KPIs.

## 7. Authentication & Authorization for all roles

**Customer Questions:**
- Tôi cần đăng ký tài khoản để mua hàng không?
- Có những vai trò nào trong hệ thống?
- Admin có thể thay đổi vai trò của người dùng không?
- Nếu tôi quên đăng nhập thì có thể tiếp tục mua hàng không?
- Bảo mật thông tin cá nhân như thế nào?

**QA Follow-up Questions:**
- JWT token structure và claims?
- Session management và timeout?
- Password policies và reset flow?
- Multi-factor authentication support?
- API authorization headers validation?
- Role hierarchy và inheritance?
- Account lockout và recovery?
- GDPR compliance cho user data?

**Developer Answers:**
Đối với Customer: Có, cần tài khoản để mua hàng và track orders. Vai trò: Customer, Staff, Manager, Admin. Admin có thể thay đổi roles. Phải login để checkout, guest checkout không support. Thông tin mã hóa và tuân thủ GDPR.

Đối với QA: JWT chứa userId, role, exp, iat claims. Session timeout 24h, sliding expiration. Password: 8+ chars, complexity required. Reset: email OTP, 10 min expiry. API validate Bearer token. Roles hierarchical: Admin > Manager > Staff > Customer. Lockout 5 attempts, unlock after 15 min. Data encrypted at rest, audit logs cho access.