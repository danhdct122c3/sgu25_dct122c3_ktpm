# Shoe Store Web Application


## 1.3 Quy trình nghiệp vụ

### Danh mục sản phẩm
Khách hàng truy cập vào hệ thống có thể duyệt qua danh sách các sản phẩm giày đang được bày bán. Hệ thống hỗ trợ các công cụ tìm kiếm và bộ lọc nâng cao, cho phép người dùng lọc sản phẩm theo thương hiệu, kích cỡ, màu sắc hoặc khoảng giá mong muốn. Khi chọn một sản phẩm cụ thể, hệ thống hiển thị trang chi tiết bao gồm hình ảnh, mô tả, thông số kỹ thuật và tình trạng tồn kho thực tế, giúp khách hàng có đầy đủ thông tin trước khi quyết định mua hàng.

### Quy trình kiểm soát truy cập
Để đảm bảo an toàn và phân quyền chính xác, hệ thống yêu cầu người dùng (bao gồm Khách hàng, Nhân viên, Quản lý và Quản trị viên) phải đăng nhập để truy cập các chức năng tương ứng. Khách hàng mới có thể thực hiện đăng ký tài khoản bằng cách cung cấp thông tin cá nhân cơ bản. Hệ thống xác thực thông tin đăng nhập và cấp quyền truy cập dựa trên vai trò (Role): Khách hàng được truy cập các tính năng mua sắm; Nhân viên, Quản lý truy cập quản lý đơn hàng; Admin truy cập các chức năng quản trị cấp cao. Khi kết thúc phiên làm việc, người dùng thực hiện đăng xuất để bảo mật tài khoản.

### Quy trình xử lý giỏ hàng
Sau khi chọn được sản phẩm ưng ý, khách hàng thêm sản phẩm vào giỏ hàng. Tại đây, khách hàng có thể xem lại danh sách sản phẩm, điều chỉnh số lượng mua hoặc xóa bớt các mặt hàng không cần thiết. Hệ thống sẽ tự động tính toán lại tổng tiền hàng (tạm tính) mỗi khi có sự thay đổi về số lượng. Nếu khách hàng sở hữu mã giảm giá, họ có thể áp dụng ngay tại bước này để xem giá trị được chiết khấu trước khi tiến hành thanh toán.

### Quy trình quản lý đơn hàng
Quá trình đặt hàng bắt đầu khi khách hàng xác nhận giỏ hàng và cung cấp thông tin giao nhận. Hệ thống hỗ trợ hai phương thức thanh toán: thanh toán khi nhận hàng (COD) hoặc thanh toán trực tuyến qua VNPay.
- Sau khi đặt hàng thành công, đơn hàng được khởi tạo với trạng thái CREATED.
- Nhân viên hoặc Quản lý sẽ kiểm tra tồn kho và xác nhận đơn hàng (CONFIRMED).
- Quy trình xử lý tiếp tục qua các bước chuẩn bị hàng (PREPARING), sẵn sàng giao (READY_FOR_DELIVERY) và giao cho đơn vị vận chuyển (OUT_FOR_DELIVERY).
- Khi khách hàng nhận được hàng, trạng thái đơn hàng chuyển sang DELIVERED.
- Trong trường hợp hủy đơn hoặc trả hàng, hệ thống sẽ thực hiện quy trình cập nhật trạng thái và hoàn tiền (đối với đơn VNPay) theo quy định.

### Quy trình quản lý người dùng
Module này cho phép người dùng (Khách hàng) tự quản lý và cập nhật thông tin cá nhân như họ tên, địa chỉ giao hàng và mật khẩu để thuận tiện cho các lần mua sắm sau. Đối với Quản trị viên (Admin), hệ thống cung cấp quyền hạn cao nhất để quản lý toàn bộ danh sách người dùng trong hệ thống. Admin có thể xem chi tiết, tạo mới, cập nhật vai trò hoặc vô hiệu hóa (khóa) các tài khoản vi phạm chính sách hoặc các tài khoản nhân viên đã nghỉ việc.

### Quy trình quản lý mã giảm giá
Trang quản lý khuyến mãi dành riêng cho Quản lý (Manager) để thiết lập các chiến dịch kinh doanh. Người quản lý có thể tạo mới các mã giảm giá (Coupon) với các thông số chi tiết như: mã code, mức giảm (theo phần trăm hoặc số tiền cố định), ngày bắt đầu/kết thúc, số lượng giới hạn và giá trị đơn hàng tối thiểu. Khách hàng khi thanh toán sẽ nhập mã này, hệ thống tự động kiểm tra tính hợp lệ (thời hạn, điều kiện) và trừ trực tiếp vào tổng tiền thanh toán nếu mã hợp lệ.

## Phân quyền (Roles)

Hệ thống được chia thành 4 vai trò chính với các quyền hạn cụ thể:

1.  **Customer (Khách hàng)**:
    -   Đăng ký, đăng nhập.
    -   Xem danh sách sản phẩm, chi tiết sản phẩm.
    -   Thêm vào giỏ hàng, đặt hàng, thanh toán.
    -   Quản lý thông tin cá nhân, xem lịch sử đơn hàng.

2.  **Staff (Nhân viên)**:
    -   Đăng nhập vào trang quản trị.
    -   Xem danh sách đơn hàng.
    -   Xác nhận đơn hàng (CONFIRMED).
    -   Cập nhật trạng thái đơn hàng (PREPARING, READY_FOR_DELIVERY...).

3.  **Manager (Quản lý)**:
    -   Bao gồm các quyền của Staff.
    -   Quản lý mã giảm giá (Coupon): Tạo mới, chỉnh sửa, xóa.
    -   Xem báo cáo doanh thu (nếu có).

4.  **Admin (Quản trị viên)**:
    -   Quyền hạn cao nhất trong hệ thống.
    -   Quản lý người dùng: Xem, tạo mới, cập nhật vai trò, khóa tài khoản.
    -   Quản lý danh mục, thương hiệu, sản phẩm.
    -   Cấu hình hệ thống.

## Technology Stack

-   **Back-end**: Spring Boot, Spring Data JPA, Spring Security, JWT.
-   **Front-end**: ReactJS, Redux Toolkit, Axios, Tailwind CSS.
-   **Database**: MySQL.
-   **Deployment**: Docker, Docker Compose.

## Hướng dẫn chạy với Docker

Để chạy dự án bằng Docker, hãy đảm bảo bạn đã cài đặt Docker và Docker Compose trên máy của mình.

1.  **Clone repository**:
    ```bash
    git clone <repository-url>
    cd <project-directory>
    ```

2.  **Khởi chạy ứng dụng**:
    Mở terminal tại thư mục gốc của dự án và chạy lệnh sau:
    ```bash
    docker-compose up -d --build
    ```
    Lệnh này sẽ tải các image cần thiết, build source code và khởi chạy các container cho Database, Backend và Frontend.

3.  **Truy cập ứng dụng**:
    -   **Trang chủ (Frontend)**: Truy cập `http://localhost:3000`
    -   **API (Backend)**: Truy cập `http://localhost:8080`
    -   **Database (MySQL)**: Host `localhost`, Port `3307` (User: `app_user`, Password: `app_password`)

4.  **Các công cụ hỗ trợ (Optional)**:
    Nếu bạn muốn sử dụng PhpMyAdmin để quản lý database, hãy chạy lệnh:
    ```bash
    docker-compose --profile tools up -d
    ```
    -   **PhpMyAdmin**: Truy cập `http://localhost:8081`

5.  **Dừng ứng dụng**:
    ```bash
    docker-compose down
    ```

