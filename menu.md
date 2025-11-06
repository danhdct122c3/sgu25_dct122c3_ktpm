```mermaid
sequenceDiagram
  participant S as Cửa hàng
  participant UI as Giao diện (UI)
  participant MS as MenuService (Backend)
  participant DB as CSDL

  S ->> UI: Nhập thông tin đăng nhập
  UI ->> MS: POST /auth/login
  MS ->> DB: SELECT user WHERE phone/email
  alt Tài khoản hợp lệ
    DB -->> MS: Thông tin hợp lệ
    MS -->> UI: 200 OK (token)
    UI -->> S: Hiển thị trang chủ
  else Sai mật khẩu hoặc tài khoản không tồn tại
    DB -->> MS: Không tìm thấy
    MS -->> UI: 401 Unauthorized
    UI -->> S: Thông báo "Sai thông tin đăng nhập"
    Note over S, UI: Người dùng có thể thử lại hoặc quên mật khẩu
  end
  S ->> UI: Chọn "Quản lý món ăn"
  UI ->> MS: GET /menus
  MS ->> DB: SELECT * FROM menu WHERE store_id=...
  alt CSDL phản hồi thành công
    DB -->> MS: Danh sách món
    MS -->> UI: 200 OK
    UI -->> S: Hiển thị danh sách món
  else Lỗi kết nối hoặc DB trống
    DB -->> MS: Error / Empty result
    MS -->> UI: 500 Internal Error
    UI -->> S: Hiển thị thông báo "Không thể tải danh sách món"
  end
  alt Thêm món
    S ->> UI: Nhấn "Thêm món"
    UI ->> MS: POST /menus {tên, giá, tồn kho, hình ảnh}
    alt Dữ liệu hợp lệ
      MS ->> DB: INSERT INTO menu (...)
      DB -->> MS: OK
      MS -->> UI: 201 Created
      UI -->> S: Thông báo "Thêm thành công"
    else Thiếu hoặc trùng tên món
      MS -->> UI: 400 Bad Request
      UI -->> S: Hiển thị "Dữ liệu món ăn không hợp lệ"
    end
  else Sửa món
    S ->> UI: Nhấn "Sửa món"
    UI ->> MS: PUT /menus/{id} {fields...}
    alt Món tồn tại
      MS ->> DB: UPDATE menu SET ... WHERE id=...
      DB -->> MS: OK
      MS -->> UI: 200 OK
      UI -->> S: Thông báo "Cập nhật thành công"
    else Món không tồn tại / lỗi DB
      MS -->> UI: 404 Not Found
      UI -->> S: Thông báo "Không tìm thấy món cần sửa"
    end
  else Xóa món
    S ->> UI: Nhấn "Xóa món"
    UI ->> MS: DELETE /menus/{id}
    alt Xóa thành công
      MS ->> DB: DELETE FROM menu WHERE id=...
      DB -->> MS: OK
      MS -->> UI: 200 OK
      UI -->> S: Hiển thị "Xóa thành công"
    else Món không tồn tại hoặc lỗi ràng buộc
      MS -->> UI: 409 Conflict
      UI -->> S: Thông báo "Không thể xóa món này"
    end
  end


