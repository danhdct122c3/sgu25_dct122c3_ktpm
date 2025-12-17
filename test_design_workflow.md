# Test Design Workflow cho ShoeShop

## Ảnh Workflow
[Chèn ảnh mô tả workflow tổng quan cho các use case UC1, UC1.1, UC2, với các bước basic flow, alternate flows và exception paths.]

## 1. Flow 1: UC1 - Tra cứu và xem chi tiết sản phẩm

| Test case Id | Workflow Name | Action | Role | State | Involved TCs | Assign To | Test pass 1 result | Test pass 2 result | Test pass 3 result | Note |
|--------------|---------------|--------|------|-------|--------------|-----------|---------------------|---------------------|---------------------|------|
| WF_UC1_1 | Duyệt sản phẩm theo danh mục và lọc | 1 Truy cập trang Danh mục sản phẩm | Tester | Pending | TC_UC1_01 | Tester A |  |  |  |  |
| WF_UC1_1 | Duyệt sản phẩm theo danh mục và lọc | 1->2.1 Hiển thị danh sách mặc định | Tester | Open | TC_UC1_01 | Tester A |  |  |  |  |
| WF_UC1_1 | Duyệt sản phẩm theo danh mục và lọc | 1->2.1->3.1 Chọn danh mục | Tester | Open | TC_UC1_01 | Tester A |  |  |  |  |
| WF_UC1_1 | Duyệt sản phẩm theo danh mục và lọc | 1->2.1->3.1->4.1 Hiển thị danh sách danh mục | Tester | Open | TC_UC1_01 | Tester A |  |  |  |  |
| WF_UC1_1 | Duyệt sản phẩm theo danh mục và lọc | 1->2.1->3.1->4.1->5.1 Áp dụng lọc | Tester | Open | TC_UC1_01 | Tester A |  |  |  |  |
| WF_UC1_1 | Duyệt sản phẩm theo danh mục và lọc | 1->2.1->3.1->4.1->5.1->6.1 Hiển thị kết quả lọc | Tester | Open | TC_UC1_01 | Tester A |  |  |  |  |
| WF_UC1_1 | Duyệt sản phẩm theo danh mục và lọc | 1->2.1->3.1->4.1->5.1->6.1->7.1 Chọn sản phẩm | Tester | Open | TC_UC1_01 | Tester A |  |  |  |  |
| WF_UC1_1 | Duyệt sản phẩm theo danh mục và lọc | 1->2.1->3.1->4.1->5.1->6.1->7.1->8.1 Hiển thị chi tiết sản phẩm | Tester | Close | TC_UC1_01 | Tester A |  |  |  |  |
| WF_UC1_2 | Tìm kiếm sản phẩm | 1 Truy cập trang Danh mục | Tester | Pending | TC_UC1_02 | Tester B |  |  |  |  |
| WF_UC1_2 | Tìm kiếm sản phẩm | 1->A1.1 Nhập từ khóa tìm kiếm | Tester | Open | TC_UC1_02 | Tester B |  |  |  |  |
| WF_UC1_2 | Tìm kiếm sản phẩm | 1->A1.1->A1.2 Hiển thị kết quả tìm kiếm | Tester | Close | TC_UC1_02 | Tester B |  |  |  |  |
| WF_UC1_3 | Danh mục không có sản phẩm | 1 Truy cập trang Danh mục | Tester | Pending | TC_UC1_03 | Tester C |  |  |  |  |
| WF_UC1_3 | Danh mục không có sản phẩm | 1->2.1 Hiển thị danh sách mặc định | Tester | Open | TC_UC1_03 | Tester C |  |  |  |  |
| WF_UC1_3 | Danh mục không có sản phẩm | 1->2.1->3.1 Chọn danh mục rỗng | Tester | Open | TC_UC1_03 | Tester C |  |  |  |  |
| WF_UC1_3 | Danh mục không có sản phẩm | 1->2.1->3.1->E1.1 Hiển thị thông báo lỗi | Tester | Close | TC_UC1_03 | Tester C |  |  |  |  |

## 2. Flow 2: UC1.1 - Quản lý sản phẩm

| Test case Id | Workflow Name | Action | Role | State | Involved TCs | Assign To | Test pass 1 result | Test pass 2 result | Test pass 3 result | Note |
|--------------|---------------|--------|------|-------|--------------|-----------|---------------------|---------------------|---------------------|------|
| WF_UC1_1_1 | Admin chỉnh sửa sản phẩm | 1 Đăng nhập | Tester | Pending | TC_UC1_1_01 | Tester A |  |  |  |  |
| WF_UC1_1_1 | Admin chỉnh sửa sản phẩm | 1->2.1 Xác thực đăng nhập | Tester | Open | TC_UC1_1_01 | Tester A |  |  |  |  |
| WF_UC1_1_1 | Admin chỉnh sửa sản phẩm | 1->2.1->3.1 Chọn quản lý sản phẩm | Tester | Open | TC_UC1_1_01 | Tester A |  |  |  |  |
| WF_UC1_1_1 | Admin chỉnh sửa sản phẩm | 1->2.1->3.1->4.1 Hiển thị danh sách sản phẩm | Tester | Open | TC_UC1_1_01 | Tester A |  |  |  |  |
| WF_UC1_1_1 | Admin chỉnh sửa sản phẩm | 1->2.1->3.1->4.1->5.1 Tìm kiếm sản phẩm | Tester | Open | TC_UC1_1_01 | Tester A |  |  |  |  |
| WF_UC1_1_1 | Admin chỉnh sửa sản phẩm | 1->2.1->3.1->4.1->5.1->6.1 Hiển thị trang chỉnh sửa | Tester | Open | TC_UC1_1_01 | Tester A |  |  |  |  |
| WF_UC1_1_1 | Admin chỉnh sửa sản phẩm | 1->2.1->3.1->4.1->5.1->6.1->7.1 Chỉnh sửa và lưu | Tester | Open | TC_UC1_1_01 | Tester A |  |  |  |  |
| WF_UC1_1_1 | Admin chỉnh sửa sản phẩm | 1->2.1->3.1->4.1->5.1->6.1->7.1->8.1 Cập nhật sản phẩm | Tester | Close | TC_UC1_1_01 | Tester A |  |  |  |  |
| WF_UC1_1_2 | Nhân viên xem tồn kho | 1 Đăng nhập | Tester | Pending | TC_UC1_1_02 | Tester B |  |  |  |  |
| WF_UC1_1_2 | Nhân viên xem tồn kho | 1->2.1 Xác thực đăng nhập | Tester | Open | TC_UC1_1_02 | Tester B |  |  |  |  |
| WF_UC1_1_2 | Nhân viên xem tồn kho | 1->2.1->A1.1 Chọn quản lý sản phẩm | Tester | Open | TC_UC1_1_02 | Tester B |  |  |  |  |
| WF_UC1_1_2 | Nhân viên xem tồn kho | 1->2.1->A1.1->A1.2 Hiển thị danh sách | Tester | Open | TC_UC1_1_02 | Tester B |  |  |  |  |
| WF_UC1_1_2 | Nhân viên xem tồn kho | 1->2.1->A1.1->A1.2->A1.3 Chọn sản phẩm | Tester | Open | TC_UC1_1_02 | Tester B |  |  |  |  |
| WF_UC1_1_2 | Nhân viên xem tồn kho | 1->2.1->A1.1->A1.2->A1.3->A1.4 Hiển thị thông tin tồn kho | Tester | Close | TC_UC1_1_02 | Tester B |  |  |  |  |
| WF_UC1_1_3 | Lỗi dữ liệu chỉnh sửa | 1 Đăng nhập | Tester | Pending | TC_UC1_1_03 | Tester C |  |  |  |  |
| WF_UC1_1_3 | Lỗi dữ liệu chỉnh sửa | 1->2.1 Xác thực đăng nhập | Tester | Open | TC_UC1_1_03 | Tester C |  |  |  |  |
| WF_UC1_1_3 | Lỗi dữ liệu chỉnh sửa | 1->2.1->3.1 Chọn quản lý sản phẩm | Tester | Open | TC_UC1_1_03 | Tester C |  |  |  |  |
| WF_UC1_1_3 | Lỗi dữ liệu chỉnh sửa | 1->2.1->3.1->4.1 Hiển thị danh sách | Tester | Open | TC_UC1_1_03 | Tester C |  |  |  |  |
| WF_UC1_1_3 | Lỗi dữ liệu chỉnh sửa | 1->2.1->3.1->4.1->5.1 Tìm kiếm sản phẩm | Tester | Open | TC_UC1_1_03 | Tester C |  |  |  |  |
| WF_UC1_1_3 | Lỗi dữ liệu chỉnh sửa | 1->2.1->3.1->4.1->5.1->6.1 Hiển thị trang chỉnh sửa | Tester | Open | TC_UC1_1_03 | Tester C |  |  |  |  |
| WF_UC1_1_3 | Lỗi dữ liệu chỉnh sửa | 1->2.1->3.1->4.1->5.1->6.1->7.1 Nhập dữ liệu không hợp lệ | Tester | Open | TC_UC1_1_03 | Tester C |  |  |  |  |
| WF_UC1_1_3 | Lỗi dữ liệu chỉnh sửa | 1->2.1->3.1->4.1->5.1->6.1->7.1->E1.1 Hiển thị thông báo lỗi | Tester | Close | TC_UC1_1_03 | Tester C |  |  |  |  |

## 3. Flow 3: UC2 - Quản lý và Cập nhật Giỏ hàng

| Test case Id | Workflow Name | Action | Role | State | Involved TCs | Assign To | Test pass 1 result | Test pass 2 result | Test pass 3 result | Note |
|--------------|---------------|--------|------|-------|--------------|-----------|---------------------|---------------------|---------------------|------|
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1 Xem chi tiết sản phẩm | Tester | Pending | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1->2.1 Chọn thêm vào giỏ | Tester | Open | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1->2.1->3.1 Hiển thị thông báo thành công | Tester | Open | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1->2.1->3.1->4.1 Chọn xem giỏ hàng | Tester | Open | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1->2.1->3.1->4.1->5.1 Hiển thị giỏ hàng và tóm tắt | Tester | Open | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1->2.1->3.1->4.1->5.1->6.1 Thay đổi số lượng | Tester | Open | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1->2.1->3.1->4.1->5.1->6.1->7.1 Tái tính toán | Tester | Open | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1->2.1->3.1->4.1->5.1->6.1->7.1->8.1 Nhập mã giảm giá | Tester | Open | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1->2.1->3.1->4.1->5.1->6.1->7.1->8.1->9.1 Kiểm tra hợp lệ | Tester | Open | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1->2.1->3.1->4.1->5.1->6.1->7.1->8.1->9.1->10.1 Áp dụng ưu đãi | Tester | Open | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1->2.1->3.1->4.1->5.1->6.1->7.1->8.1->9.1->10.1->11.1 Xem xét thông tin | Tester | Open | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1->2.1->3.1->4.1->5.1->6.1->7.1->8.1->9.1->10.1->11.1->12.1 Chọn thanh toán | Tester | Open | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_1 | Thêm và cập nhật giỏ hàng | 1->2.1->3.1->4.1->5.1->6.1->7.1->8.1->9.1->10.1->11.1->12.1->13.1 Chuyển sang thanh toán | Tester | Close | TC_UC2_01 | Tester A |  |  |  |  |
| WF_UC2_2 | Lưu/đồng bộ giỏ hàng | 1 Xem chi tiết sản phẩm | Tester | Pending | TC_UC2_02 | Tester B |  |  |  |  |
| WF_UC2_2 | Lưu/đồng bộ giỏ hàng | 1->2.1 Chọn thêm vào giỏ | Tester | Open | TC_UC2_02 | Tester B |  |  |  |  |
| WF_UC2_2 | Lưu/đồng bộ giỏ hàng | 1->2.1->3.1 Hiển thị thông báo | Tester | Open | TC_UC2_02 | Tester B |  |  |  |  |
| WF_UC2_2 | Lưu/đồng bộ giỏ hàng | 1->2.1->3.1->A1.1 Đăng xuất | Tester | Open | TC_UC2_02 | Tester B |  |  |  |  |
| WF_UC2_2 | Lưu/đồng bộ giỏ hàng | 1->2.1->3.1->A1.1->A1.2 Đăng nhập lại | Tester | Open | TC_UC2_02 | Tester B |  |  |  |  |
| WF_UC2_2 | Lưu/đồng bộ giỏ hàng | 1->2.1->3.1->A1.1->A1.2->A1.3 Đồng bộ giỏ hàng | Tester | Close | TC_UC2_02 | Tester B |  |  |  |  |
| WF_UC2_3 | Sản phẩm hết hàng | 1 Xem chi tiết sản phẩm | Tester | Pending | TC_UC2_03 | Tester C |  |  |  |  |
| WF_UC2_3 | Sản phẩm hết hàng | 1->2.1 Chọn thêm vào giỏ | Tester | Open | TC_UC2_03 | Tester C |  |  |  |  |
| WF_UC2_3 | Sản phẩm hết hàng | 1->2.1->E1.1 Hiển thị thông báo lỗi | Tester | Close | TC_UC2_03 | Tester C |  |  |  |  |
| WF_UC2_4 | Mã giảm giá không hợp lệ | 1 Xem chi tiết sản phẩm | Tester | Pending | TC_UC2_04 | Tester D |  |  |  |  |
| WF_UC2_4 | Mã giảm giá không hợp lệ | 1->2.1 Chọn thêm vào giỏ | Tester | Open | TC_UC2_04 | Tester D |  |  |  |  |
| WF_UC2_4 | Mã giảm giá không hợp lệ | 1->2.1->3.1 Hiển thị thông báo | Tester | Open | TC_UC2_04 | Tester D |  |  |  |  |
| WF_UC2_4 | Mã giảm giá không hợp lệ | 1->2.1->3.1->4.1 Chọn xem giỏ hàng | Tester | Open | TC_UC2_04 | Tester D |  |  |  |  |
| WF_UC2_4 | Mã giảm giá không hợp lệ | 1->2.1->3.1->4.1->5.1 Hiển thị giỏ hàng | Tester | Open | TC_UC2_04 | Tester D |  |  |  |  |
| WF_UC2_4 | Mã giảm giá không hợp lệ | 1->2.1->3.1->4.1->5.1->8.1 Nhập mã giảm giá không hợp lệ | Tester | Open | TC_UC2_04 | Tester D |  |  |  |  |
| WF_UC2_4 | Mã giảm giá không hợp lệ | 1->2.1->3.1->4.1->5.1->8.1->E2.1 Hiển thị thông báo lỗi | Tester | Close | TC_UC2_04 | Tester D |  |  |  |  |