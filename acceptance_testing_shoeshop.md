# KIỂM THỬ CHẤP NHẬN (ACCEPTANCE TESTING)

## 3.5. Case 4 – Acceptance Testing cho ShoeShop

### 3.5.1. Tổng quan

Kiểm thử chấp nhận nhằm xác nhận hệ thống đáp ứng yêu cầu nghiệp vụ từ phía khách hàng.

### 3.5.2. Đánh giá của khách hàng

- Dựa trên Acceptance Criteria
- Dựa trên trải nghiệm thực tế

### 3.5.3. Phân theo role

| Role | Yêu cầu chấp nhận |
|------|-------------------|
| Customer | Đặt hàng dễ dàng, quản lý hồ sơ cá nhân |
| Manager | Quản lý nghiệp vụ cửa hàng (đơn hàng, sản phẩm, nhân viên, giảm giá) |
| Staff | Xử lý đơn hàng giới hạn |
| Admin | Quản lý người dùng |

### 3.5.4. Prompt sinh Acceptance Criteria

Bạn hãy đóng vai trò là khách hàng của hệ thống ShoeShop.

Dựa trên nghiệp vụ mua giày trực tuyến, hãy đề xuất các tiêu chí chấp nhận cho từng vai trò:
- Customer ( liên quan đặt hàng quản lý hồ sơ , đơn hàng cá nhân)
- Manager ( quản lý đơn hàng của cửa hàng, nhân viên, phiếu giảm giá, sản phẩm)
- Nhân viên bán hàng (chỉ quản lý đơn hàng , giới hạn so với manager
- Admin ( chỉ quản lý nhân viên ,..)

Tham khảo đưa ra các danh sách yêu cầu chức năng từ khách hàng ở chấp nhận.

## CÁC YÊU CẦU CHỨC NĂNG

### BR1 Cấu trúc Website ShoeShop

| BR ID | Role | Tiêu chí chấp nhận | KQ | Nhận xét | Xác nhận |
|-------|------|---------------------|----|----------|----------|
| BR1.1 | Customer, Manager, Staff, Admin | Trang chủ (/) chứa liên kết đầy đủ đến các trang: Trang chủ, Sản phẩm (/products), Giỏ hàng (/cart), Tài khoản (/account), Liên hệ (/contact), Chính sách (/policies). | PASS | Đúng yêu cầu | PO |
| BR1.2 | Customer | Trang sản phẩm (/products) hiển thị danh sách giày với bộ lọc theo thương hiệu, kích cỡ, giá và thanh tìm kiếm. | PASS | Đúng yêu cầu | PO |
| BR1.3 | Customer | Trang chi tiết sản phẩm (/product/:id) hiển thị hình ảnh, mô tả, giá, tồn kho, và nút thêm vào giỏ. | PASS | Đúng yêu cầu | PO |
| BR1.4 | Customer | Trang giỏ hàng (/cart) cho phép xem, chỉnh sửa số lượng, áp dụng mã giảm giá, và tiến hành thanh toán. | FAIL | Mã hết hạn vẫn áp dụng | PO thiết kế lại |
| BR1.5 | Customer | Trang thanh toán (/checkout) hỗ trợ COD và VNPay với xác nhận đơn hàng an toàn. | PASS | Đúng yêu cầu | PO |
| BR1.6 | Customer | Trang tài khoản (/account) cho phép xem/chỉnh sửa hồ sơ và lịch sử đơn hàng. | PASS | Đúng yêu cầu | PO |
| BR1.7 | Customer | Trang liên hệ (/contact) với biểu mẫu phản hồi gửi đến quản trị viên. | PASS | Đúng yêu cầu | PO |

### BR2 Các chức năng Hệ thống

| BR ID | Role | Tiêu chí chấp nhận | KQ | Nhận xét | Xác nhận |
|-------|------|---------------------|----|----------|----------|
| BR2.1 | Customer | Tìm kiếm sản phẩm trên trang sản phẩm (/products) theo từ khóa chính xác, hiển thị kết quả khớp. | PASS | Đúng yêu cầu | PO |
| BR2.2 | Customer | Lọc sản phẩm trên trang sản phẩm (/products) theo giá, kích cỡ, màu sắc và sắp xếp theo phổ biến, giá tăng/giảm. | PASS | Đúng yêu cầu | PO |
| BR2.3 | Customer | Đăng ký tài khoản trên trang đăng ký (/register) và đăng nhập trên trang đăng nhập (/login) với bảo mật. | PASS | Đúng yêu cầu | PO |
| BR2.4 | Customer | Giỏ hàng được lưu tạm thời và đồng bộ khi đăng xuất/đăng nhập lại trên trang giỏ hàng (/cart). | PASS | Đúng yêu cầu | PO |
| BR2.5 | Customer | Thanh toán an toàn qua COD hoặc VNPay trên trang thanh toán (/checkout) với xác nhận. | PASS | Đúng yêu cầu | PO |
| BR2.6 | Customer | Xem trạng thái đơn hàng trên trang tài khoản (/account/orders). | PASS | Đúng yêu cầu | PO |

### BR3 Quản lý Tài khoản Customer

| BR ID | Role | Tiêu chí chấp nhận | KQ | Nhận xét | Xác nhận |
|-------|------|---------------------|----|----------|----------|
| BR3.1 | Customer | Đăng ký tài khoản mới trên trang đăng ký (/register) với thông tin hợp lệ như email, mật khẩu, tên, địa chỉ, và nhận email xác nhận. | PASS | Đúng yêu cầu | PO |
| BR3.2 | Customer | Đăng nhập an toàn trên trang đăng nhập (/login) với email và mật khẩu, tự động lưu phiên và chuyển hướng đến trang chủ. | PASS | Đúng yêu cầu | PO |
| BR3.3 | Customer | Chỉnh sửa thông tin cá nhân trên trang hồ sơ (/account/profile) bao gồm tên, địa chỉ, số điện thoại, và lưu thay đổi. | PASS | Đúng yêu cầu | PO |
| BR3.4 | Customer | Xem chi tiết đơn hàng trên trang lịch sử đơn hàng (/account/orders), bao gồm trạng thái, sản phẩm, tổng tiền. | PASS | Đúng yêu cầu | PO |
| BR3.5 | Customer | Đánh giá sản phẩm sau khi nhận hàng trên trang chi tiết đơn hàng (/account/orders/:id), chọn sao và viết nhận xét. | PASS | Đúng yêu cầu | PO |

### BR4 Quản lý Nghiệp vụ Cửa hàng cho Manager

| BR ID | Role | Tiêu chí chấp nhận | KQ | Nhận xét | Xác nhận |
|-------|------|---------------------|----|----------|----------|
| BR4.1 | Manager | Xem danh sách tất cả đơn hàng của cửa hàng trên trang quản lý đơn hàng (/admin/orders), lọc theo trạng thái, ngày, khách hàng. | PASS | Đúng yêu cầu | PO |
| BR4.2 | Manager | Cập nhật trạng thái đơn hàng từ Đặt hàng sang Xử lý, Đóng gói, Giao hàng, Hoàn thành trên trang chi tiết đơn (/admin/orders/:id). | PASS | Đúng yêu cầu | PO |
| BR4.3 | Manager | Phân quyền cho nhân viên trên trang quản lý nhân viên (/admin/staff), thêm/chỉnh sửa vai trò Staff hoặc Manager. | PASS | Đúng yêu cầu | PO |
| BR4.4 | Manager | Tạo và chỉnh sửa mã giảm giá trên trang quản lý giảm giá (/admin/discounts), thiết lập tỷ lệ, thời hạn, điều kiện áp dụng. | PASS | Đúng yêu cầu | PO |
| BR4.5 | Manager | Thêm/sửa/xóa sản phẩm trên trang quản lý sản phẩm (/admin/products), cập nhật hình ảnh, tồn kho, giá. | PASS | Đúng yêu cầu | PO |
| BR4.6 | Manager | Xem báo cáo doanh thu trên trang thống kê (/admin/reports), lọc theo ngày/tháng, xuất dữ liệu Excel. | PASS | Đúng yêu cầu | PO |

### BR5 Quản lý Đơn hàng cho Staff

| BR ID | Role | Tiêu chí chấp nhận | KQ | Nhận xét | Xác nhận |
|-------|------|---------------------|----|----------|----------|
| BR5.1 | Staff | Xem danh sách đơn hàng được phân công xử lý trên trang đơn hàng của nhân viên (/staff/orders), lọc theo trạng thái. | PASS | Đúng yêu cầu | PO |
| BR5.2 | Staff | Cập nhật trạng thái đơn hàng từ Xử lý sang Đóng gói, Giao hàng trên trang chi tiết đơn (/staff/orders/:id). | PASS | Đúng yêu cầu | PO |
| BR5.3 | Staff | Xem chi tiết thông tin đơn hàng bao gồm khách hàng, sản phẩm, địa chỉ giao trên trang chi tiết (/staff/orders/:id). | PASS | Đúng yêu cầu | PO |
| BR5.4 | Staff | Thêm ghi chú nội bộ cho đơn hàng trên trang chi tiết đơn (/staff/orders/:id) để ghi nhận tiến độ. | PASS | Đúng yêu cầu | PO |

### BR6 Quản lý Người dùng cho Admin

| BR ID | Role | Tiêu chí chấp nhận | KQ | Nhận xét | Xác nhận |
|-------|------|---------------------|----|----------|----------|
| BR6.1 | Admin | Tạo tài khoản người dùng mới trên trang quản lý người dùng (/admin/users), chọn vai trò Customer, Staff, Manager với thông tin đầy đủ. | PASS | Đúng yêu cầu | PO |
| BR6.2 | Admin | Cập nhật thông tin tài khoản người dùng trên trang chi tiết người dùng (/admin/users/:id), thay đổi email, tên, vai trò. | PASS | Đúng yêu cầu | PO |
| BR6.3 | Admin | Vô hiệu hóa tài khoản người dùng trên trang quản lý người dùng (/admin/users), đặt trạng thái inactive để ngăn truy cập. | PASS | Đúng yêu cầu | PO |
| BR6.4 | Admin | Đặt quyền truy cập cho từng vai trò trên trang phân quyền (/admin/roles), xác định chức năng được phép cho Customer, Staff, Manager. | PASS | Đúng yêu cầu | PO |
| BR6.5 | Admin | Theo dõi nhật ký hoạt động của người dùng trên trang logs (/admin/logs), xem lịch sử đăng nhập, thay đổi. | PASS | Đúng yêu cầu | PO |