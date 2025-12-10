# Test Cases Template with Test Data References

## Test Data

### User Credentials

| ID | Content |
|----|---------|
| TD_USER_001 | username: customer |
| TD_USER_002 | password: password123 |
| TD_USER_003 | username: invalid |
| TD_USER_004 | password: wrongpass |
| TD_USER_005 | email: newcustomer@example.com |
| TD_USER_006 | email: invalidemail |
| TD_USER_007 | email: customer@example.com |
| TD_USER_008 | email: staff@example.com |
| TD_USER_009 | email: invalid@example.com |
| TD_USER_010 | email: manager@example.com |
| TD_USER_011 | email: admin@example.com |

### Product Data

| ID | Content |
|----|---------|
| TD_PROD_001 | brand: Nike |
| TD_PROD_002 | minPrice: 50 |
| TD_PROD_003 | maxPrice: 200 |
| TD_PROD_004 | productId: 1 |
| TD_PROD_005 | size: 10 |
| TD_PROD_006 | color: Black |
| TD_PROD_007 | size: 9 |

### Cart Data

| ID | Content |
|----|---------|
| TD_CART_001 | quantity: 2 |
| TD_CART_002 | quantity: 3 |
| TD_CART_003 | quantity: 15 |
| TD_CART_004 | item1: 50, item2: 30 qty 2 |
| TD_CART_005 | discountCode: SAVE10 |
| TD_CART_006 | discountCode: INVALID |

### Checkout Data

| ID | Content |
|----|---------|
| TD_CHECK_001 | address: 123 Main St |
| TD_CHECK_002 | phone: 1234567890 |

### Payment Data

| ID | Content |
|----|---------|
| TD_PAY_001 | bankCode: NCB |
| TD_PAY_002 | total: $150 |

### Order Data

| ID | Content |
|----|---------|
| TD_ORDER_001 | orderId: 123 |
| TD_ORDER_002 | status: Deliveried |
| TD_ORDER_003 | status: CONFIRMED |

### Discount Data

| ID | Content |
|----|---------|
| TD_DISC_001 | discountCode: test2 (20%) |
| TD_DISC_002 | discountCode: giamgia1 (10%) |
| TD_DISC_003 | discountCode: test5 (50%) |
| TD_DISC_004 | discountCode: test4 (min: 200.000đ) |
| TD_DISC_005 | discountCode: SAVE20 |
| TD_DISC_006 | discountCode: test3 |
| TD_DISC_007 | code: test |
| TD_DISC_008 | percentage: 10 |

## Auth Customer

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|-------|------|------|----------------|-------|-----------|-----------------|
| TC_AUTH_001 | Đăng nhập thành công với thông tin hợp lệ | Customer | Authentication & Access Control | Người dùng chưa đăng nhập | 1. Điều hướng đến trang /login<br>2. Nhập tên người dùng và mật khẩu<br>3. Nhấn nút Đăng nhập | TD_USER_001<br>TD_USER_002<br> | Người dùng đăng nhập thành công, được chuyển hướng đến trang chủ |
| TC_AUTH_002 | Đăng nhập thất bại với tên người dùng không hợp lệ | Customer | Authentication & Access Control | Người dùng chưa đăng nhập | 1. Điều hướng đến trang /login<br>2. Nhập tên người dùng không hợp lệ và mật khẩu hợp lệ<br>3. Nhấn nút Đăng nhập | TD_USER_003<br>TD_USER_002<br> | Hiển thị thông báo lỗi, người dùng không đăng nhập được |
| TC_AUTH_003 | Đăng nhập thất bại với mật khẩu không hợp lệ | Customer | Authentication & Access Control | Người dùng chưa đăng nhập | 1. Điều hướng đến trang /login<br>2. Nhập tên người dùng hợp lệ và mật khẩu không hợp lệ<br>3. Nhấn nút Đăng nhập | TD_USER_001<br>TD_USER_004<br> | Hiển thị thông báo lỗi, người dùng không đăng nhập được |
| TC_AUTH_004 | Đăng ký thành công với dữ liệu hợp lệ | Customer | Authentication & Access Control | Người dùng chưa đăng ký | 1. Điều hướng đến trang /register<br>2. Nhập mật khẩu<br>3. Nhấn nút Đăng ký | TD_USER_005<br>TD_USER_002<br> | Người dùng đăng ký thành công, đăng nhập và chuyển hướng đến trang chủ |
| TC_AUTH_005 | Đăng ký thất bại với email không hợp lệ | Customer | Authentication & Access Control | Người dùng chưa đăng ký | 1. Điều hướng đến trang /register<br>2. Nhập email không hợp lệ<br>3. Nhấn nút Đăng ký | TD_USER_006<br>TD_USER_002<br> | Hiển thị thông báo lỗi về email không hợp lệ |
| TC_AUTH_006 | Đăng ký thất bại với email đã tồn tại | Customer | Authentication & Access Control | Người dùng chưa đăng ký | 1. Điều hướng đến trang /register<br>2. Nhập email đã tồn tại<br>3. Nhấn nút Đăng ký | TD_USER_007<br>TD_USER_002<br> | Hiển thị thông báo lỗi về email đã tồn tại |
| TC_AUTH_007 | Đăng xuất | Customer | Authentication & Access Control | Người dùng đã đăng nhập | 1. Nhấn nút đăng xuất<br>2. Xác nhận | N/A<br>N/A<br> | Người dùng đăng xuất thành công |
| TC_AUTH_008 | Truy cập trang được bảo vệ mà không đăng nhập | Customer | Authentication & Access Control | Người dùng chưa đăng nhập | 1. Điều hướng đến /cart | N/A | Chuyển hướng đến /login |
| TC_AUTH_009 | Truy cập trang quản trị | Customer | Authentication & Access Control | Người dùng đã đăng nhập với vai trò khách hàng | 1. Điều hướng đến /admin | N/A | Chuyển hướng đến /unauthorized |
| TC_AUTH_010 | Truy cập trang nhân viên | Customer | Authentication & Access Control | Người dùng đã đăng nhập với vai trò khách hàng | 1. Điều hướng đến /staff | N/A | Chuyển hướng đến /unauthorized |
| TC_AUTH_011 | Truy cập trang quản lý | Customer | Authentication & Access Control | Người dùng đã đăng nhập với vai trò khách hàng | 1. Điều hướng đến /manager | N/A | Chuyển hướng đến /unauthorized |

## Auth Staff

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|-------|------|------|----------------|-------|-----------|-----------------|
| TC_AUTH_012 | Đăng nhập thành công với thông tin hợp lệ | Staff | Authentication & Access Control | Người dùng chưa đăng nhập | 1. Điều hướng đến /admin/login<br>2. Nhập email và mật khẩu<br>3. Nhấn nút Đăng nhập | TD_USER_008<br>TD_USER_002<br> | Người dùng đăng nhập thành công, chuyển hướng đến /staff |
| TC_AUTH_013 | Đăng nhập thất bại với thông tin không hợp lệ | Staff | Authentication & Access Control | Người dùng chưa đăng nhập | 1. Điều hướng đến /admin/login<br>2. Nhập email/mật khẩu không hợp lệ<br>3. Nhấn nút Đăng nhập | TD_USER_009<br>TD_USER_004<br> | Hiển thị thông báo lỗi, người dùng không đăng nhập được |
| TC_AUTH_014 | Đăng xuất | Staff | Authentication & Access Control | Người dùng đã đăng nhập | 1. Nhấn nút đăng xuất<br>2. Xác nhận | N/A<br>N/A<br> | Người dùng đăng xuất thành công, chuyển hướng đến /logout |
| TC_AUTH_015 | Truy cập trang nhân viên | Staff | Authentication & Access Control | Người dùng đã đăng nhập với vai trò nhân viên | 1. Điều hướng đến /staff | N/A | Hiển thị trang dashboard của nhân viên |
| TC_AUTH_016 | Truy cập trang quản lý | Staff | Authentication & Access Control | Người dùng đã đăng nhập với vai trò nhân viên | 1. Điều hướng đến /manager | N/A | Chuyển hướng đến /unauthorized |
| TC_AUTH_017 | Truy cập trang quản trị | Staff | Authentication & Access Control | Người dùng đã đăng nhập với vai trò nhân viên | 1. Điều hướng đến /admin | N/A | Chuyển hướng đến /unauthorized |

## Auth Manager

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|-------|------|------|----------------|-------|-----------|-----------------|
| TC_AUTH_018 | Đăng nhập thành công với thông tin hợp lệ | Manager | Authentication & Access Control | Người dùng chưa đăng nhập | 1. Điều hướng đến /admin/login<br>2. Nhập email và mật khẩu<br>3. Nhấn nút Đăng nhập | TD_USER_010<br>TD_USER_002<br> | Người dùng đăng nhập thành công, chuyển hướng đến /manager |
| TC_AUTH_019 | Đăng nhập thất bại với thông tin không hợp lệ | Manager | Authentication & Access Control | Người dùng chưa đăng nhập | 1. Điều hướng đến /admin/login<br>2. Nhập email/mật khẩu không hợp lệ<br>3. Nhấn nút Đăng nhập | TD_USER_009<br>TD_USER_004<br> | Hiển thị thông báo lỗi, người dùng không đăng nhập được |
| TC_AUTH_020 | Đăng xuất | Manager | Authentication & Access Control | Người dùng đã đăng nhập | 1. Nhấn nút đăng xuất<br>2. Xác nhận | N/A<br>N/A<br> | Người dùng đăng xuất thành công, chuyển hướng đến /logout |
| TC_AUTH_021 | Truy cập trang quản lý | Manager | Authentication & Access Control | Người dùng đã đăng nhập với vai trò quản lý | 1. Điều hướng đến /manager | N/A | Hiển thị trang dashboard của quản lý |
| TC_AUTH_022 | Truy cập trang quản trị | Manager | Authentication & Access Control | Người dùng đã đăng nhập với vai trò quản lý | 1. Điều hướng đến /admin | N/A | Chuyển hướng đến /unauthorized |
| TC_AUTH_023 | Truy cập trang nhân viên | Manager | Authentication & Access Control | Người dùng đã đăng nhập với vai trò quản lý | 1. Điều hướng đến /staff | N/A | Chuyển hướng đến /unauthorized |

## Auth Admin

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_AUTH_024 | Đăng nhập thành công với thông tin hợp lệ | Auth | Admin | Người dùng chưa đăng nhập | Đang xử lý | 1. Điều hướng đến /admin/login<br>2. Nhập email và mật khẩu<br>3. Nhấn nút Đăng nhập | TD_USER_011<br>TD_USER_002<br> | Người dùng đăng nhập thành công, chuyển hướng đến /admin | Danh |
| TC_AUTH_025 | Đăng nhập thất bại với thông tin không hợp lệ | Auth | Admin | Người dùng chưa đăng nhập | Đang xử lý | 1. Điều hướng đến /admin/login<br>2. Nhập email/mật khẩu không hợp lệ<br>3. Nhấn nút Đăng nhập | TD_USER_009<br>TD_USER_004<br> | Hiển thị thông báo lỗi, người dùng không đăng nhập được | Danh |
| TC_AUTH_026 | Đăng xuất | Auth | Admin | Người dùng đã đăng nhập | Đang xử lý | 1. Nhấn nút đăng xuất<br>2. Xác nhận | N/A<br>N/A<br> | Người dùng đăng xuất thành công, chuyển hướng đến /logout | Danh |
| TC_AUTH_027 | Truy cập trang quản trị | Auth | Admin | Người dùng đã đăng nhập với vai trò quản trị | Đang xử lý | 1. Điều hướng đến /admin | N/A | Hiển thị trang dashboard của quản trị | Danh |
| TC_AUTH_028 | Truy cập trang nhân viên | Auth | Admin | Người dùng đã đăng nhập với vai trò quản trị | Đang xử lý | 1. Điều hướng đến /staff | N/A | Chuyển hướng đến /unauthorized | Danh |
| TC_AUTH_029 | Truy cập trang quản lý | Auth | Admin | Người dùng đã đăng nhập với vai trò quản trị | Đang xử lý | 1. Điều hướng đến /manager | N/A | Chuyển hướng đến /unauthorized | Danh |

## ProductCatalog

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_PC_001 | Xem danh mục sản phẩm | ProductCatalog | Customer | Người dùng đã đăng nhập | Đang xử lý | 1. Điều hướng đến /shoes | N/A | Hiển thị lưới sản phẩm với hình ảnh và giá | Khang |
| TC_PC_002 | Lọc sản phẩm theo thương hiệu | ProductCatalog | Customer | Người dùng đã đăng nhập | Đang xử lý | 1. Điều hướng đến /shoes<br>2. Chọn bộ lọc thương hiệu<br>3. Nhấn Áp dụng | TD_PROD_001<br>TD_PROD_001<br>TD_PROD_001 | Sản phẩm được lọc để chỉ hiển thị thương hiệu đã chọn | Khang |
| TC_PC_003 | Lọc sản phẩm theo khoảng giá | ProductCatalog | Customer | Người dùng đã đăng nhập | Đang xử lý | 1. Điều hướng đến /shoes<br>2. Thiết lập khoảng giá<br>3. Nhấn Áp dụng | TD_PROD_002<br>TD_PROD_003<br> | Hiển thị sản phẩm trong khoảng giá | Khang |
| TC_PC_004 | Sắp xếp sản phẩm theo giá thấp đến cao | ProductCatalog | Customer | Người dùng đã đăng nhập | Đang xử lý | 1. Điều hướng đến /shoes<br>2. Chọn sắp xếp "Giá: Thấp đến Cao"<br>3. Nhấn Áp dụng | N/A<br>N/A<br>N/A | Sản phẩm được sắp xếp tăng dần theo giá | Khang |
| TC_PC_005 | Sắp xếp sản phẩm theo giá cao đến thấp | ProductCatalog | Customer | Người dùng đã đăng nhập | Đang xử lý | 1. Điều hướng đến /shoes<br>2. Chọn sắp xếp "Giá: Cao đến Thấp"<br>3. Nhấn Áp dụng | N/A<br>N/A<br>N/A | Sản phẩm được sắp xếp giảm dần theo giá | Khang |
| TC_PC_006 | Xem trang chi tiết sản phẩm | ProductCatalog | Customer | Người dùng đã đăng nhập | Đang xử lý | 1. Điều hướng đến /shoes<br>2. Nhấn vào hình ảnh/tiêu đề sản phẩm | TD_PROD_004<br>TD_PROD_004 | Trang chi tiết sản phẩm với hình ảnh, mô tả, biến thể | Khang |
| TC_PC_007 | Chi tiết sản phẩm với nhiều hình ảnh | ProductCatalog | Customer | Người dùng đã đăng nhập | Đang xử lý | 1. Điều hướng đến chi tiết sản phẩm<br>2. Nhấn vào hình thu nhỏ | N/A<br>N/A | Hình ảnh chính thay đổi khi nhấn vào hình thu nhỏ | Khang |
| TC_PC_008 | Chọn kích cỡ và màu sắc sản phẩm | ProductCatalog | Customer | Người dùng đã đăng nhập | Đang xử lý | 1. Điều hướng đến chi tiết sản phẩm<br>2. Chọn tùy chọn kích cỡ | TD_PROD_005<br>TD_PROD_006 | Lựa chọn kích cỡ và màu sắc được cập nhật trong UI | Khang |

## Shopping Cart

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_SC_001 | Thêm sản phẩm vào giỏ hàng | ShoppingCart | Customer | Người dùng đã đăng nhập | Đang xử lý | 1. Điều hướng đến chi tiết sản phẩm<br>2. Chọn kích cỡ/số lượng<br>3. Nhấn "Thêm vào Giỏ hàng" | TD_PROD_007<br>TD_CART_001<br> | Thông báo thành công, biểu tượng giỏ hàng hiển thị số lượng cập nhật | Khang |
| TC_SC_002 | Xem giỏ hàng | ShoppingCart | Customer | Người dùng đã đăng nhập, có sản phẩm trong giỏ | Đang xử lý | 1. Nhấn biểu tượng giỏ hàng | N/A | Thanh bên/slideout giỏ hàng hiển thị với sản phẩm, số lượng, giá | Khang |
| TC_SC_003 | Cập nhật số lượng sản phẩm trong giỏ | ShoppingCart | Customer | Người dùng đã đăng nhập, có sản phẩm trong giỏ | Đang xử lý | 1. Mở giỏ hàng<br>2. Thay đổi số lượng<br>3. Nhấn Cập nhật | TD_CART_002<br>TD_CART_002<br>TD_CART_002 | Số lượng được cập nhật, tổng giá được tính lại | Khang |
| TC_SC_004 | Xóa sản phẩm khỏi giỏ hàng | ShoppingCart | Customer | Người dùng đã đăng nhập, có sản phẩm trong giỏ | Đang xử lý | 1. Mở giỏ hàng<br>2. Nhấn xóa trên sản phẩm | N/A<br>N/A | Sản phẩm được xóa, tổng được cập nhật | Khang |
| TC_SC_005 | Xóa toàn bộ giỏ hàng | ShoppingCart | Customer | Người dùng đã đăng nhập, có sản phẩm trong giỏ | Đang xử lý | 1. Mở giỏ hàng<br>2. Nhấn "Xóa Giỏ hàng" | N/A<br>N/A | Tất cả sản phẩm được xóa, giỏ hàng trống | Khang |
| TC_SC_006 | Giỏ hàng duy trì qua các phiên | ShoppingCart | Customer | Người dùng đã đăng nhập, có sản phẩm trong giỏ | Đang xử lý | 1. Thêm sản phẩm vào giỏ<br>2. Đăng xuất<br>3. Đăng nhập lại<br>4. Kiểm tra giỏ hàng | N/A<br>N/A<br>N/A<br>N/A | Sản phẩm trong giỏ được bảo toàn sau khi đăng nhập lại | Khang |
| TC_SC_007 | Tính tổng giỏ hàng | ShoppingCart | Customer | Người dùng đã đăng nhập, nhiều sản phẩm | Đang xử lý | 1. Thêm nhiều sản phẩm<br>2. Xem tổng giỏ hàng | TD_CART_004<br>TD_CART_004 | Tổng hiển thị chính xác (50 + 60 = 110) | Khang |
| TC_SC_008 | Giới hạn số lượng tối đa trong giỏ | ShoppingCart | Customer | Người dùng đã đăng nhập | Đang xử lý | 1. Thử thêm số lượng > 10 | TD_CART_003 | Thông báo lỗi "Số lượng tối đa là 10" | Khang |
| TC_SC_009 | Giỏ hàng với mã giảm giá | ShoppingCart | Customer | Người dùng đã đăng nhập, có sản phẩm trong giỏ | Đang xử lý | 1. Mở giỏ hàng<br>2. Nhập mã giảm giá<br>3. Áp dụng | TD_CART_005<br>TD_CART_005<br>TD_CART_005 | Mã giảm giá được áp dụng, tổng giảm 10% | Khang |
| TC_SC_010 | Mã giảm giá không hợp lệ | ShoppingCart | Customer | Người dùng đã đăng nhập, có sản phẩm trong giỏ | Đang xử lý | 1. Mở giỏ hàng<br>2. Nhập mã không hợp lệ<br>3. Áp dụng | TD_CART_006<br>TD_CART_006<br>TD_CART_006 | Thông báo lỗi "Mã giảm giá không hợp lệ" | Khang |

## Checkout

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_CO_001 | Tiến hành thanh toán | Checkout | Customer | Người dùng đã đăng nhập, có sản phẩm trong giỏ | Đang xử lý | 1. Mở giỏ hàng<br>2. Nhấn "Thanh toán" | N/A<br>N/A | Chuyển hướng đến trang thanh toán với tóm tắt giỏ hàng | Thành |
| TC_CO_002 | Nhập thông tin giao hàng | Checkout | Customer | Ở trang thanh toán | Đang xử lý | 1. Điền địa chỉ giao hàng<br>2. Điền thông tin liên hệ<br>3. Nhấn Tiếp tục | TD_CHECK_001<br>TD_CHECK_002<br> | Xác thực biểu mẫu thành công, tiến hành thanh toán | Thành |
| TC_CO_003 | Chọn phương thức thanh toán COD | Checkout | Customer | Ở trang thanh toán | Đang xử lý | 1. Chọn "Thanh toán khi nhận hàng"<br>2. Nhấn Tiếp tục | N/A<br>N/A | Phương thức COD được chọn, tóm tắt thanh toán hiển thị | Thành |
| TC_CO_004 | Chọn phương thức thanh toán VNPay | Checkout | Customer | Ở trang thanh toán | Đang xử lý | 1. Chọn "VNPay"<br>2. Nhấn Tiếp tục | N/A<br>N/A | Phương thức VNPay được chọn, lựa chọn ngân hàng hiển thị | Thành |
| TC_CO_005 | Xem tóm tắt đơn hàng | Checkout | Customer | Ở trang thanh toán | Đang xử lý | 1. Xem lại sản phẩm, giá, tổng | N/A | Tất cả sản phẩm, số lượng, giá hiển thị chính xác | Thành |
| TC_CO_006 | Áp dụng mã giảm giá khi thanh toán | Checkout | Customer | Ở trang thanh toán | Đang xử lý | 1. Nhập mã giảm giá<br>2. Áp dụng | TD_CART_005<br>TD_CART_005 | Mã giảm giá được áp dụng, tổng được cập nhật | Thành |
| TC_CO_007 | Xác thực biểu mẫu thanh toán | Checkout | Customer | Ở trang thanh toán | Đang xử lý | 1. Để trống các trường bắt buộc<br>2. Nhấn Tiếp tục | N/A<br>N/A | Thông báo lỗi yêu cầu cập nhật thông tin địa chỉ cá nhân, chuyển sang trang nhập hồ sơ cá nhân | Thành |

## Payment

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_PAY_001 | Xác nhận đơn hàng COD | Payment | Customer | Đã chọn COD khi thanh toán | Đang xử lý | 1. Xem lại đơn hàng<br>2. Nhấn "Đặt hàng" | N/A<br>N/A | Trang xác nhận đơn hàng, số đơn hàng hiển thị | Thành |
| TC_PAY_002 | Chuyển hướng thanh toán VNPay | Payment | Customer | Đã chọn VNPay khi thanh toán | Đang xử lý | 1. Chọn ngân hàng<br>2. Nhấn "Thanh toán ngay" | TD_PAY_001<br>TD_PAY_001 | Chuyển hướng đến cổng VNPay | Thành |
| TC_PAY_003 | Trả về thành công VNPay | Payment | Customer | Đã khởi tạo thanh toán | Đang xử lý | 1. Hoàn thành thanh toán trên VNPay<br>2. Trả về trang | N/A<br>N/A | Trang thành công với xác nhận đơn hàng | Thành |
| TC_PAY_004 | Trả về thất bại VNPay | Payment | Customer | Đã khởi tạo thanh toán | Đang xử lý | 1. Thất bại thanh toán trên VNPay<br>2. Trả về trang | N/A<br>N/A | Trang thất bại với thông báo lỗi | Thành |
| TC_PAY_005 | Trả về hủy VNPay | Payment | Customer | Đã khởi tạo thanh toán | Đang xử lý | 1. Hủy trên VNPay<br>2. Trả về trang | N/A<br>N/A | Trang hủy, đơn hàng không hoàn thành | Thành |
| TC_PAY_008 | Hiển thị số tiền thanh toán | Payment | Customer | Ở bước thanh toán | Đang xử lý | 1. Kiểm tra số tiền thanh toán | TD_PAY_002 | Số tiền chính xác $150 hiển thị | Thành |
| TC_PAY_009 | Nhiều lần thử thanh toán | Payment | Customer | Thanh toán thất bại | Hoàn thành | 1. Thử lại thanh toán<br>2. Hoàn thành thành công | N/A<br>N/A | Lần thử thứ hai thành công | Thành |

## Order Tracking

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_OT_001 | Xem lịch sử đơn hàng | Order Tracking | Customer | Người dùng đã đăng nhập, có đơn hàng | Đang xử lý | 1. Điều hướng đến /order-history | N/A | Danh sách đơn hàng của người dùng với trạng thái, ngày | Thành |
| TC_OT_002 | Xem chi tiết đơn hàng | Order Tracking | Customer | Người dùng đã đăng nhập, có đơn hàng | Đang xử lý | 1. Nhấn vào đơn hàng trong lịch sử | TD_ORDER_001 | Trang chi tiết đơn hàng với sản phẩm, trạng thái, theo dõi | Thành |
| TC_OT_003 | Hiển thị trạng thái đơn hàng | Order Tracking | Customer | Người dùng đã đăng nhập, có đơn hàng | Đang xử lý | 1. Xem trạng thái đơn hàng | TD_ORDER_002 | Huy hiệu trạng thái hiển thị "Đã giao" | Thành |
| TC_OT_004 | Thông tin theo dõi đơn hàng | Order Tracking | Customer | Người dùng đã đăng nhập, đơn hàng đã giao | Đang xử lý | 1. Xem thông tin theo dõi | N/A | Số theo dõi, hãng vận chuyển, thời gian giao hàng ước tính | Thành |
| TC_OT_005 | Cập nhật trạng thái đơn hàng | Order Tracking | Customer | Người dùng đã đăng nhập, đơn hàng đang vận chuyển | Đang xử lý | 1. Làm mới trang | N/A | Trạng thái cập nhật tự động hoặc khi làm mới | Thành |

## OrderManagement

| ID | Module | Title | Role | Pre-condition | Steps | Status | Test Data | Expected Result | Người thực hiện |
|----|--------|-------|------|----------------|-------|--------|-----------|-----------------|-----------------|
| TC_OM_001 | OrderManagement | Nhân viên xem tất cả đơn hàng | Customer | Nhân viên đã đăng nhập | 1. Điều hướng đến /staff/member-order-history | Đang xử lý | N/A | Danh sách tất cả đơn hàng của khách hàng | Thành |
| TC_OM_002 | OrderManagement | Nhân viên xem chi tiết đơn hàng | Customer | Nhân viên đã đăng nhập | 1. Nhấn vào đơn hàng | Đang xử lý | TD_ORDER_001 | Chi tiết đơn hàng với thông tin khách hàng | Thành |
| TC_OM_003 | OrderManagement | Nhân viên cập nhật trạng thái đơn hàng | Customer | Nhân viên đã đăng nhập, đơn hàng đang chờ | 1. Thay đổi trạng thái thành CONFIRMED<br>2. Lưu | Đang xử lý | TD_ORDER_003<br>TD_ORDER_003 | Trạng thái được cập nhật thành công | Thành |
| TC_OM_004 | OrderManagement | Quản lý xem tất cả đơn hàng | Customer | Quản lý đã đăng nhập | 1. Điều hướng đến /manager/member-order-history | Đang xử lý | N/A | Danh sách tất cả đơn hàng với tùy chọn quản lý | Thành |
| TC_OM_005 | OrderManagement | Quản lý cập nhật trạng thái đơn hàng | Customer | Quản lý đã đăng nhập, đơn hàng đang chờ | 1. Thay đổi trạng thái thành CONFIRMED<br>2. Lưu | Đang xử lý | TD_ORDER_001<br>TD_ORDER_001 | Trạng thái được cập nhật thành công | Thành |

## DiscountManagement

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_DM_001 | Áp dụng mã giảm giá hợp lệ | Discount | Customer | Người dùng đã đăng nhập, có sản phẩm trong giỏ | Đang xử lý | 1. Mở giỏ hàng<br>2. Nhập mã<br>3. Áp dụng | TD_DISC_001<br>TD_DISC_001<br>TD_DISC_001 | Mã giảm giá được áp dụng, tổng giảm | Quân |
| TC_DM_002 | Áp dụng mã giảm giá đã hết hạn | Discount | Customer | Người dùng đã đăng nhập, có sản phẩm trong giỏ | Đang xử lý | 1. Mở giỏ hàng<br>2. Nhập mã đã hết hạn<br>3. Áp dụng | TD_DISC_002<br>TD_DISC_002<br>TD_DISC_002 | Lỗi "Mã giảm giá đã hết hạn" | Quân |
| TC_DM_003 | Áp dụng mã giảm giá đã sử dụng | Discount | Customer | Người dùng đã đăng nhập, có sản phẩm trong giỏ | Đang xử lý | 1. Mở giỏ hàng<br>2. Nhập mã đã sử dụng<br>3. Áp dụng | TD_DISC_003<br>TD_DISC_003<br>TD_DISC_003 | Lỗi "Mã giảm giá đã được sử dụng" | Quân |
| TC_DM_004 | Mua tối thiểu cho mã giảm giá | Discount | Customer | Người dùng đã đăng nhập, giỏ hàng dưới mức tối thiểu | Đang xử lý | 1. Nhập mã<br>2. Áp dụng | TD_DISC_004<br>TD_DISC_004 | Lỗi "Cần mua tối thiểu 200,000đ" | Quân |
| TC_DM_006 | Xóa mã giảm giá đã áp dụng | Discount | Customer | Người dùng đã đăng nhập, mã giảm giá đã áp dụng | Đang xử lý | 1. Nhấn Xóa Mã giảm giá | N/A | Mã giảm giá được xóa, tổng được cập nhật | Quân |
| TC_DM_007 | Nhiều lần thử mã giảm giá | Discount | Customer | Người dùng đã đăng nhập, mã giảm giá đã áp dụng | Đang xử lý | 1. Thử áp dụng mã khác | TD_DISC_005 | Lỗi "Chỉ một mã giảm giá mỗi đơn hàng" | Quân |
| TC_DM_008 | Mã giảm giá phân biệt chữ hoa chữ thường | Discount | Customer | Người dùng đã đăng nhập, có sản phẩm trong giỏ | Đang xử lý | 1. Nhập mã với chữ cái sai<br>2. Áp dụng | TD_DISC_006<br>TD_DISC_006 | Mã giảm giá được áp dụng (không phân biệt hoa thường) | Quân |
| TC_DM_009 | Quản lý tạo mã giảm giá | Discount | Manager | Quản lý đã đăng nhập | Đang xử lý | 1. Điều hướng đến quản lý giảm giá<br>2. Tạo mã giảm giá mới | TD_DISC_007<br>TD_DISC_008 | Mã giảm giá được tạo thành công | Quân |