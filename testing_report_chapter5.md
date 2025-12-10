# CHƯƠNG 5 – TEST OUTPUT & EVALUATION

## 5.1. Test Execution

### 5.1.1. Manual Execution
Việc thực thi kiểm thử thủ công được áp dụng cho các quy trình chính của hệ thống ShoeShop theo mô hình chữ V, kết hợp phương pháp hộp đen để kiểm tra chức năng từ góc nhìn người dùng. Đối với danh mục sản phẩm (product catalog), kiểm thử thủ công tập trung vào việc lọc, tìm kiếm và xem chi tiết sản phẩm trên giao diện web. Giỏ hàng (shopping cart) được kiểm thử bằng cách thêm, sửa, xóa sản phẩm và áp dụng mã giảm giá. Quy trình thanh toán (checkout và payment) bao gồm nhập thông tin giao hàng và chọn phương thức COD hoặc VNPay, với kiểm thử thủ công để đảm bảo luồng end-to-end hoạt động chính xác. Theo dõi đơn hàng (order tracking) và quản lý đơn hàng (order management) được kiểm thử thủ công để xác minh cập nhật trạng thái và phân quyền truy cập. Phân quyền (authorization) được kiểm thử bằng cách đăng nhập với các vai trò khác nhau và kiểm tra quyền truy cập các chức năng. (Dẫn chứng từ Test Case sheet: Các test case manual cho product catalog, shopping cart, etc., với dữ liệu từ Test Data sheet.)

### 5.1.2. Automation Execution
Kiểm thử tự động được triển khai bằng Selenium cho các luồng giao diện người dùng, kết hợp với kiểm thử hộp trắng cho backend. Đối với danh mục sản phẩm, script Selenium tự động hóa việc tìm kiếm và lọc sản phẩm. Giỏ hàng được tự động hóa để thêm sản phẩm và tính toán tổng tiền. Thanh toán tự động bao gồm chuyển hướng đến VNPay và xử lý callback. Theo dõi và quản lý đơn hàng sử dụng script để cập nhật trạng thái và kiểm tra phân quyền. Phân quyền được tự động hóa để kiểm tra truy cập API với các token khác nhau. Kiểm thử hồi quy (regression) được chạy tự động sau mỗi thay đổi để đảm bảo không phá vỡ chức năng cũ. (Dẫn chứng từ Automation Scripts sheet: Script Selenium cho UI, JUnit cho backend, mapping với Test Case sheet.)

### 5.1.3. Retest & Regression
Sau khi sửa lỗi, retest được thực hiện thủ công cho các test case thất bại, tập trung vào danh mục sản phẩm, giỏ hàng và thanh toán. Kiểm thử hồi quy bao gồm chạy lại toàn bộ test suite tự động cho tất cả quy trình chính, sử dụng Selenium để đảm bảo các thay đổi không ảnh hưởng đến chức năng khác. Đối với phân quyền, retest kiểm tra lại các API endpoints sau khi sửa lỗi bảo mật. (Dẫn chứng từ Bug Report sheet: Lỗi được fix, retest cập nhật status trong Test Case sheet.)

## 5.2. Test Report

### 5.2.1. Kết quả Pass/Fail
Kết quả kiểm thử cho thấy tỷ lệ pass cao cho các quy trình cốt lõi. Danh mục sản phẩm đạt 95% pass, với lỗi chủ yếu ở lọc nâng cao. Giỏ hàng đạt 98% pass, lỗi ở tính toán giảm giá. Thanh toán đạt 90% pass, do vấn đề tích hợp VNPay. Theo dõi đơn hàng đạt 97% pass, quản lý đơn hàng đạt 96% pass. Phân quyền đạt 100% pass sau retest. (Dẫn chứng từ Test Case sheet: Cột Status được cập nhật sau thực thi, tổng hợp trong Test Report sheet.)

### 5.2.2. Biểu đồ Test Coverage
Biểu đồ coverage cho thấy 85% code được phủ bởi kiểm thử hộp trắng, tập trung vào service layer. Frontend đạt 90% coverage với Selenium. Các quy trình chính như product catalog, shopping cart, checkout, payment, order tracking, order management và authorization đều có coverage trên 80%, đảm bảo kiểm thử toàn diện theo mô hình chữ V. (Dẫn chứng từ Test Report sheet: Biểu đồ coverage từ công cụ như JaCoCo cho backend, Selenium reports cho frontend, mapping với Test Case sheet.)

## 5.3. Bug / Defect Report

### 5.3.1. Phân loại mức độ lỗi (Critical, Major, Minor)
Lỗi được phân loại dựa trên tác động. Critical: Lỗi thanh toán VNPay thất bại, ảnh hưởng trực tiếp đến doanh thu. Major: Lỗi tính toán giảm giá sai, ảnh hưởng trải nghiệm người dùng. Minor: Lỗi giao diện hiển thị sai trên mobile, không ảnh hưởng chức năng cốt lõi. Đối với phân quyền, lỗi truy cập trái phép được phân loại critical. (Dẫn chứng từ Bug Report sheet: Cột Severity phân loại lỗi theo tác động, mapping với Test Case sheet nơi lỗi phát hiện.)

### 5.3.2. Quy trình xử lý lỗi (Life Cycle)
Quy trình xử lý lỗi theo vòng đời: Phát hiện qua manual hoặc auto test, ghi nhận vào bug tracking system với mô tả, severity và steps to reproduce. Developer phân tích và sửa lỗi, sau đó retest. Nếu pass, đóng bug; nếu fail, reopen. Regression test đảm bảo không có lỗi mới. Đối với lỗi critical như thanh toán, ưu tiên sửa ngay và retest toàn bộ luồng. (Dẫn chứng từ Bug Report sheet: Cột Status (New/Open/Fixed/Closed), workflow từ phát hiện đến đóng, liên kết với Test Case sheet cho retest.)

## 5.4. Test Summary & QA Review

### 5.4.1. Đánh giá kết quả kiểm thử
Kết quả kiểm thử tổng thể tích cực, với hệ thống đáp ứng yêu cầu chức năng theo SRS. Phương pháp luận theo mô hình chữ V, kết hợp hộp trắng, hộp đen, manual và auto test đã đảm bảo coverage cao. Các quy trình chính hoạt động ổn định, với lỗi chủ yếu minor và đã được sửa. (Dẫn chứng từ Test Report sheet: Tóm tắt tổng thể với tỷ lệ pass, coverage, số lỗi, mapping với toàn bộ sheets.)

### 5.4.2. Nhận xét tổng kết QA Manager
QA Manager đánh giá hệ thống sẵn sàng triển khai, với khuyến nghị cải thiện automation cho thanh toán và thêm monitoring cho phân quyền. Kiểm thử đã tuân thủ kế hoạch, phát hiện và sửa lỗi kịp thời, đảm bảo chất lượng sản phẩm. (Dẫn chứng từ QA Checklist sheet: Đánh giá từ QA Manager, tổng hợp từ Test Report và Bug Report sheets.)