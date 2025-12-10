# Template Checklist Xem Test Case cho Báo Cáo Kiểm Thử

## Thông Tin Chung
- **Tên Dự Án**: ShoeShop E-Commerce
- **Phiên Bản**: 1.0
- **Ngày Kiểm Tra**: [Ngày]
- **Người Kiểm Tra**: [Tên QC]
- **Người Phát Triển**: [Tên Dev]

## Danh Sách Test Case

| STT | Test Case ID | Mô Tả Test Case | Điều Kiện Tiên Quyết | Các Bước Thực Hiện | Kết Quả Mong Đợi | Kết Quả Thực Tế | Trạng Thái (Pass/Fail) | Ghi Chú |
|-----|--------------|------------------|----------------------|---------------------|-------------------|------------------|---------------------------|---------|
| 1   | TC-001      | Đăng nhập với tài khoản hợp lệ | Người dùng chưa đăng nhập | 1. Mở trang đăng nhập<br>2. Nhập email và mật khẩu hợp lệ<br>3. Nhấn nút Đăng nhập | Đăng nhập thành công, chuyển đến trang chủ | [Kết quả] | [Pass/Fail] | [Ghi chú] |
| 2   | TC-002      | Thêm sản phẩm vào giỏ hàng | Người dùng đã đăng nhập | 1. Chọn sản phẩm<br>2. Nhấn "Thêm vào giỏ"<br>3. Kiểm tra giỏ hàng | Sản phẩm được thêm, số lượng cập nhật | [Kết quả] | [Pass/Fail] | [Ghi chú] |
| ... | ...         | ...              | ...                  | ...                 | ...               | ...             | ...                       | ...     |

## Tóm Tắt
- **Tổng Số Test Case**: [Số]
- **Pass**: [Số]
- **Fail**: [Số]
- **Tỷ Lệ Pass**: [Phần trăm]%

## Q&A Checklist: QC (Non-Tech) Hỏi - Dev Trả Lời

### 1. Câu Hỏi: Hệ thống có xử lý đúng khi khách hàng thêm sản phẩm vào giỏ hàng không?
**Trả Lời (Dev)**: Có, khi khách hàng nhấn "Thêm vào giỏ", hệ thống kiểm tra tồn kho của sản phẩm, nếu đủ thì cập nhật số lượng trong bảng cart_item của database, và trả về phản hồi thành công cho frontend.

### 2. Câu Hỏi: Khi thanh toán qua VNPay, hệ thống có đảm bảo an toàn không?
**Trả Lời (Dev)**: Có, hệ thống sử dụng API của VNPay với mã hóa dữ liệu, không lưu thông tin thẻ tín dụng trên server của chúng ta, chỉ lưu mã giao dịch để theo dõi.

### 3. Câu Hỏi: Nếu khách hàng hủy đơn hàng, hệ thống có hoàn lại tiền không?
**Trả Lời (Dev)**: Tùy thuộc vào trạng thái đơn hàng. Nếu chưa thanh toán, không cần hoàn tiền. Nếu đã thanh toán qua VNPay, cần tích hợp API hoàn tiền của VNPay, nhưng hiện tại hệ thống chỉ cho phép hủy ở trạng thái CREATED hoặc CONFIRMED, và chưa thanh toán.

### 4. Câu Hỏi: Hệ thống có gửi thông báo khi đơn hàng được cập nhật trạng thái không?
**Trả Lời (Dev)**: Hiện tại chưa có tính năng gửi email hoặc thông báo, nhưng có thể mở rộng bằng cách tích hợp dịch vụ email như SendGrid.

### 5. Câu Hỏi: Khi admin thêm sản phẩm mới, hình ảnh được lưu ở đâu?
**Trả Lời (Dev)**: Hình ảnh được upload lên thư mục uploads/shoes trên server backend, và đường dẫn được lưu trong database bảng shoe_image.

### 6. Câu Hỏi: Hệ thống có hỗ trợ nhiều ngôn ngữ không?
**Trả Lời (Dev)**: Frontend hiện tại chỉ hỗ trợ tiếng Việt, nhưng có thể thêm đa ngôn ngữ bằng thư viện i18n trong React.

### 7. Câu Hỏi: Nếu database bị lỗi, hệ thống có tiếp tục hoạt động không?
**Trả Lời (Dev)**: Không, hệ thống phụ thuộc vào database để lưu trữ dữ liệu, nếu database down thì hầu hết chức năng sẽ không hoạt động, cần có cơ chế failover hoặc caching.

### 8. Câu Hỏi: Khách hàng có thể xem lịch sử đơn hàng không?
**Trả Lời (Dev)**: Có, khách hàng đăng nhập có thể xem danh sách đơn hàng của mình qua API /orders, với thông tin trạng thái và chi tiết.

### 9. Câu Hỏi: Hệ thống có kiểm tra tồn kho khi đặt hàng không?
**Trả Lời (Dev)**: Có, khi tạo đơn hàng, hệ thống kiểm tra tồn kho của từng variant sản phẩm, nếu không đủ thì không cho phép đặt hàng.

### 10. Câu Hỏi: Khi đăng ký tài khoản, mật khẩu có được mã hóa không?
**Trả Lời (Dev)**: Có, mật khẩu được mã hóa bằng BCrypt trước khi lưu vào database.