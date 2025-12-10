# Tài Liệu Đặc Tả Yêu Cầu Phần Mềm (SRS) - Hệ Thống ShoeShop E-Commerce

## 1. Giới Thiệu

### 1.1 Mục Đích
Tài liệu Đặc tả Yêu cầu Phần mềm (SRS) này được tạo ra với mục đích mô tả chi tiết các yêu cầu chức năng và phi chức năng của hệ thống ShoeShop E-commerce. Tài liệu này phục vụ như một tài liệu tham khảo chính thức cho các nhà phát triển, kiểm thử viên và các bên liên quan để đảm bảo rằng hệ thống được phát triển đúng theo yêu cầu đã định nghĩa.

### 1.2 Phạm Vi
Phạm vi của hệ thống ShoeShop bao gồm việc cung cấp một nền tảng thương mại điện tử chuyên biệt cho ngành hàng giày dép, hỗ trợ các chức năng chính như quản lý danh mục sản phẩm, giỏ hàng và thanh toán, quản lý đơn hàng, quản lý người dùng và kiểm soát truy cập. Hệ thống được thiết kế để phục vụ bốn vai trò người dùng: Khách hàng (Customer), Nhân viên (Staff), Quản lý (Manager) và Quản trị viên (Admin). Dự án không bao gồm các hệ thống bên thứ ba ngoài cổng thanh toán VNPay và các quy trình logistics vật lý bên ngoài.

### 1.3 Định Nghĩa, Viết Tắt và Thuật Ngữ
Trong tài liệu này, một số thuật ngữ và viết tắt được sử dụng bao gồm: E-commerce (Thương mại điện tử), SPA (Single Page Application), API (Application Programming Interface), CI/CD (Continuous Integration/Continuous Deployment), UAC (User Acceptance Criteria), FR (Functional Requirements), NFR (Non-Functional Requirements), MVC (Model-View-Controller), ACID (Atomicity, Consistency, Isolation, Durability).

### 1.4 Tài Liệu Tham Khảo
Tài liệu này tham khảo các nguồn sau: Báo cáo Kiểm thử Dự án ShoeShop E-commerce, Tài liệu Thiết kế Hệ thống, Danh sách Use Case, User Story và Acceptance Criteria, Tài liệu Kỹ thuật về ReactJS, Spring Boot và MySQL.

### 1.5 Tổng Quan Tài Liệu
Tài liệu SRS được chia thành bốn chương chính. Chương 1 giới thiệu tổng quan về dự án. Chương 2 mô tả tổng quan hệ thống. Chương 3 chi tiết các tính năng hệ thống. Chương 4 trình bày yêu cầu giao tiếp bên ngoài.

## 2. Mô Tả Tổng Quan

### 2.1 Quan Điểm Sản Phẩm
Hệ thống ShoeShop là một nền tảng thương mại điện tử dựa trên web, được thiết kế để cung cấp trải nghiệm mua sắm trực tuyến toàn diện cho khách hàng và các công cụ quản lý hiệu quả cho nhân viên. Hệ thống tích hợp với cổng thanh toán VNPay để xử lý các giao dịch an toàn, và sử dụng kiến trúc full-stack bao gồm Frontend được phát triển bằng ReactJS, Backend bằng Spring Boot và cơ sở dữ liệu MySQL. Hệ thống được triển khai trên các nền tảng đám mây như Vercel cho Frontend và Railway cho Backend, đảm bảo tính sẵn sàng và khả năng mở rộng.

### 2.2 Chức Năng Sản Phẩm
Các chức năng chính của hệ thống bao gồm quản lý danh mục sản phẩm cho phép xem, tìm kiếm và lọc sản phẩm theo các tiêu chí như thương hiệu, kích cỡ và giá cả; giỏ hàng và thanh toán hỗ trợ thêm, sửa, xóa sản phẩm, áp dụng mã giảm giá và thanh toán qua COD hoặc VNPay; quản lý đơn hàng cho phép theo dõi và cập nhật trạng thái đơn hàng từ lúc tạo đến khi giao; quản lý người dùng bao gồm đăng ký, đăng nhập và cập nhật hồ sơ; và kiểm soát truy cập với phân quyền chi tiết cho bốn vai trò người dùng.

### 2.3 Đặc Điểm Người Dùng
Hệ thống phục vụ bốn lớp người dùng chính. Khách hàng là người mua hàng cuối cùng, cần giao diện thân thiện, dễ sử dụng và trải nghiệm mua sắm mượt mà. Nhân viên là người xử lý đơn hàng tại cửa hàng, yêu cầu công cụ hiệu quả để xác nhận và cập nhật trạng thái đơn hàng. Quản lý chịu trách nhiệm điều hành kinh doanh, cần các công cụ để tạo mã giảm giá, giám sát đơn hàng và xem báo cáo doanh thu. Quản trị viên quản lý tổng thể hệ thống, có quyền cao nhất để quản lý người dùng, phân quyền và giám sát hoạt động.

### 2.4 Môi Trường Vận Hành
Hệ thống vận hành trong môi trường web, hỗ trợ các trình duyệt hiện đại như Chrome, Firefox và Safari trên cả desktop và thiết bị di động. Backend chạy trên máy chủ Java với Spring Boot, kết nối đến cơ sở dữ liệu MySQL. Hệ thống sử dụng Docker Compose trong môi trường phát triển để đóng gói và quản lý các thành phần. Triển khai sản xuất trên Vercel cho Frontend và Railway cho Backend và MySQL.

### 2.5 Ràng Buộc Thiết Kế và Triển Khai
Các ràng buộc thiết kế bao gồm việc sử dụng kiến trúc MVC cho Backend, SPA cho Frontend, và đảm bảo tính toàn vẹn dữ liệu với ACID trong MySQL. Bảo mật được ưu tiên với mã hóa mật khẩu, phân quyền chi tiết và kiểm soát truy cập. Hiệu năng phải đáp ứng thời gian phản hồi dưới 2 giây cho các thao tác chính và hỗ trợ đồng thời 1000 người dùng.

### 2.6 Tài Liệu Hướng Dẫn Người Dùng
Tài liệu hướng dẫn sử dụng sẽ bao gồm hướng dẫn cho khách hàng về cách mua sắm, thanh toán và theo dõi đơn hàng; hướng dẫn cho nhân viên về quản lý đơn hàng; hướng dẫn cho quản lý về tạo khuyến mãi và xem báo cáo; và hướng dẫn cho quản trị viên về quản lý hệ thống. Tài liệu sẽ được cung cấp dưới dạng online help và PDF.

### 2.7 Giả Định và Phụ Thuộc
Dự án giả định rằng phát triển theo mô hình Agile Scrum với các Sprint ngắn. Hệ thống phụ thuộc vào API của VNPay cho thanh toán, và các bên thứ ba này phải ổn định. Ngoài ra, hệ thống giả định rằng người dùng có kết nối internet ổn định và sử dụng trình duyệt tương thích.

## 3. Tính Năng Hệ Thống

### 3.1 Tính Năng 1 – Quản Lý Danh Mục Sản Phẩm
#### 3.1.1 Mô Tả và Ưu Tiên
Tính năng này cho phép xem danh sách sản phẩm, lọc theo thương hiệu, kích cỡ, màu sắc, tìm kiếm theo từ khóa, xem chi tiết sản phẩm bao gồm mô tả, hình ảnh và tồn kho. Admin và Manager có thể thêm mới, chỉnh sửa, xóa sản phẩm. Ưu tiên cao vì là chức năng cốt lõi.

#### 3.1.2 Chuỗi Kích Thích – Phản Ứng
Khi người dùng nhập từ khóa tìm kiếm, hệ thống trả về danh sách sản phẩm phù hợp. Khi chọn bộ lọc, danh sách được cập nhật theo thời gian thực.

#### 3.1.3 Yêu Cầu Chức Năng
Hệ thống phải hiển thị danh sách sản phẩm với bộ lọc và sắp xếp. Hiển thị chi tiết sản phẩm gồm tên, giá, mô tả, size, brand, hình ảnh, tồn kho. Admin có quyền CRUD.

Tính năng con: Xem danh sách sản phẩm với phân trang. Lọc theo thương hiệu, kích cỡ, khoảng giá. Tìm kiếm theo từ khóa. Xem chi tiết sản phẩm với hình ảnh và mô tả. Thêm mới sản phẩm với upload hình ảnh. Chỉnh sửa thông tin sản phẩm. Xóa sản phẩm. Quản lý thương hiệu và kích cỡ.

### 3.2 Tính Năng 2 – Giỏ Hàng và Thanh Toán
#### 3.2.1 Mô Tả và Ưu Tiên
Cho phép thêm/sửa/xóa sản phẩm trong giỏ, áp dụng mã giảm giá, tính toán tổng tiền theo thời gian thực, nhập địa chỉ giao hàng, chọn thanh toán COD hoặc VNPay. Ưu tiên cao.

#### 3.2.2 Chuỗi Kích Thích – Phản Ứng
Khi thêm sản phẩm, giỏ hàng cập nhật số lượng. Khi áp dụng mã giảm giá, tổng tiền giảm tương ứng.

#### 3.2.3 Yêu Cầu Chức Năng
Tính toán tổng tiền, thuế, phí ship, giảm giá. Lưu trạng thái giỏ hàng cho người đăng nhập. Cho phép nhập địa chỉ và chọn phương thức thanh toán. Tạo bản ghi đơn hàng khi checkout.

Tính năng con: Thêm sản phẩm vào giỏ hàng. Cập nhật số lượng sản phẩm. Xóa sản phẩm khỏi giỏ. Áp dụng mã giảm giá hợp lệ. Tính toán tổng tiền tự động. Nhập thông tin giao hàng. Chọn phương thức thanh toán COD. Chọn thanh toán VNPay và chuyển hướng. Xử lý callback từ VNPay.

### 3.3 Tính Năng 3 – Quản Lý Đơn Hàng
#### 3.3.1 Mô Tả và Ưu Tiên
Theo dõi trạng thái đơn hàng, hủy đơn nếu chưa xác nhận, cập nhật trạng thái bởi Staff/Admin. Ưu tiên cao.

#### 3.3.2 Chuỗi Kích Thích – Phản Ứng
Khi tạo đơn, trạng thái là CREATED. Staff xác nhận chuyển sang CONFIRMED, etc.

#### 3.3.3 Yêu Cầu Chức Năng
Staff/Admin thay đổi trạng thái. Xem báo cáo doanh thu.

Tính năng con: Xem danh sách đơn hàng với lọc theo trạng thái. Xem chi tiết đơn hàng. Hủy đơn hàng nếu trạng thái CREATED hoặc CONFIRMED. Cập nhật trạng thái đơn hàng theo quy trình. Xem lịch sử đơn hàng. Báo cáo doanh thu theo thời gian.

### 3.4 Tính Năng 4 – Quản Lý Người Dùng
#### 3.4.1 Mô Tả và Ưu Tiên
Đăng ký, đăng nhập, cập nhật hồ sơ. Admin quản lý tài khoản. Ưu tiên cao.

#### 3.4.2 Chuỗi Kích Thích – Phản Ứng
Sau đăng ký, gửi xác nhận. Đăng nhập thành công chuyển đến trang chủ.

#### 3.4.3 Yêu Cầu Chức Năng
Hỗ trợ đăng ký/đăng nhập/đăng xuất. Admin kích hoạt/vô hiệu hóa tài khoản. Cập nhật thông tin cá nhân.

Tính năng con: Đăng ký tài khoản mới. Đăng nhập với email/mật khẩu. Đăng xuất. Cập nhật thông tin cá nhân. Thay đổi mật khẩu. Quản lý danh sách người dùng. Kích hoạt/khóa tài khoản.

### 3.5 Tính Năng 5 – Kiểm Soát Truy Cập
#### 3.5.1 Mô Tả và Ưu Tiên
Phân quyền cho 4 vai trò. Ưu tiên cao.

#### 3.5.2 Chuỗi Kích Thích – Phản Ứng
Người dùng đăng nhập, hệ thống kiểm tra vai trò và hiển thị menu phù hợp.

#### 3.5.3 Yêu Cầu Chức Năng
Thiết lập nhóm quyền Admin, Manager, Staff, Customer. Đảm bảo truy cập theo quyền.

Tính năng con: Phân quyền truy cập các trang. Hiển thị menu theo vai trò. Bảo vệ API endpoints. Kiểm tra quyền trước khi thực hiện hành động.

## 4. Yêu Cầu Giao Tiếp Bên Ngoài

### 4.1 Giao Diện Người Dùng
Giao diện người dùng được thiết kế dưới dạng ứng dụng trang đơn (SPA) sử dụng ReactJS, đảm bảo responsive trên cả desktop và mobile. Các trang chính bao gồm trang chủ hiển thị sản phẩm nổi bật, trang danh sách sản phẩm với bộ lọc, trang chi tiết sản phẩm, trang giỏ hàng, trang thanh toán, trang lịch sử đơn hàng, trang đăng nhập/đăng ký, và các trang quản trị cho Admin, Manager, Staff. Giao diện sử dụng Tailwind CSS để đảm bảo tính thẩm mỹ và nhất quán.

### 4.2 Giao Diện Phần Cứng
Hệ thống yêu cầu máy tính cá nhân hoặc thiết bị di động với trình duyệt web hiện đại hỗ trợ HTML5, CSS3 và JavaScript. Không yêu cầu phần cứng đặc biệt ngoài kết nối internet ổn định. Máy chủ backend cần ít nhất 2GB RAM và 20GB dung lượng lưu trữ.

### 4.3 Giao Diện Phần Mềm
Frontend sử dụng ReactJS để xây dựng giao diện. Backend sử dụng Spring Boot (Java) để cung cấp RESTful APIs. Cơ sở dữ liệu MySQL để lưu trữ dữ liệu. Tích hợp với VNPay API cho thanh toán. Sử dụng Docker Compose trong phát triển. Các API chính bao gồm ShoeController cho quản lý sản phẩm, CartController cho giỏ hàng, OrderController cho đơn hàng, AuthController cho xác thực, UserController cho người dùng, DiscountController cho giảm giá, PaymentController cho thanh toán.

### 4.4 Giao Diện Truyền Thông
Giao tiếp giữa Frontend và Backend thông qua giao thức HTTP/HTTPS với dữ liệu định dạng JSON. Giao tiếp với VNPay qua API RESTful. Cơ sở dữ liệu kết nối qua JDBC. Hệ thống sử dụng WebSocket nếu cần cho thông báo thời gian thực. Tất cả giao tiếp đều được mã hóa để đảm bảo bảo mật.