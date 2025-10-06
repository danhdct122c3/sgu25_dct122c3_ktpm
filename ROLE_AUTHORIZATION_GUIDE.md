# Hệ Thống Phân Quyền - 2 Role: USER và ADMIN

## 📋 Tổng Quan

Hệ thống đã được cập nhật để sử dụng **chỉ 2 role chính**:
- **USER**: Người dùng thông thường (thay thế MEMBER)
- **ADMIN**: Quản trị viên (quyền cao nhất)

**Đã xóa**: GUEST, MANAGER roles

---

## 🔐 Backend - Phân Quyền API

### 1. **SecurityConfig**
- Thêm `@EnableMethodSecurity` để kích hoạt phân quyền theo method
- Cấu hình JWT authentication với role extraction từ `scope` claim

### 2. **Phân Quyền Controllers**

#### **UserController**
| Endpoint | Role Required | Mô tả |
|----------|--------------|-------|
| `POST /users/register` | Public | Đăng ký user mới |
| `GET /users` | ADMIN | Xem tất cả users |
| `GET /users/{userId}` | ADMIN hoặc chính user đó | Xem thông tin user |
| `PUT /users/{userId}` | ADMIN hoặc chính user đó | Cập nhật thông tin |
| `GET /users/profile?username=` | Public | Xem profile theo username |
| `GET /users/role?role=` | ADMIN | Lọc users theo role |
| `GET /users/list-user` | ADMIN | Phân trang users |

#### **OrderController**
| Endpoint | Role Required | Mô tả |
|----------|--------------|-------|
| `POST /orders/create` | Authenticated | Tạo đơn hàng |
| `POST /orders/apply-discount` | Public | Áp dụng mã giảm giá |

#### **OrderDetailController**
| Endpoint | Role Required | Mô tả |
|----------|--------------|-------|
| `GET /order-details/user/{userId}` | ADMIN hoặc chính user đó | Xem đơn hàng của user |
| `GET /order-details` | ADMIN | Xem tất cả đơn hàng |
| `GET /order-details/order/{orderId}` | Public | Xem chi tiết đơn hàng |
| `PUT /order-details/order/{orderId}` | ADMIN | Cập nhật đơn hàng |
| `GET /order-details/list-order` | ADMIN | Phân trang đơn hàng |

#### **DiscountController**
| Endpoint | Role Required | Mô tả |
|----------|--------------|-------|
| `POST /discounts` | ADMIN | Tạo mã giảm giá |
| `GET /discounts` | ADMIN | Xem tất cả mã giảm giá |
| `GET /discounts/{id}` | Public | Xem chi tiết mã |
| `PUT /discounts/{id}` | ADMIN | Cập nhật mã giảm giá |
| `GET /discounts/isActive` | ADMIN | Lọc theo trạng thái |
| `GET /discounts/list-discount` | ADMIN | Phân trang mã giảm giá |

#### **ShoeController**
| Endpoint | Role Required | Mô tả |
|----------|--------------|-------|
| `GET /shoes/**` | Public | Xem sản phẩm |
| `POST /shoes` | ADMIN | Tạo sản phẩm mới |
| `PUT /shoes/{id}` | ADMIN | Cập nhật sản phẩm |

#### **ReportController**
| Endpoint | Role Required | Mô tả |
|----------|--------------|-------|
| `GET /report/**` | ADMIN | Tất cả báo cáo (revenue, top-seller, inventory, etc.) |

---

## 🎨 Frontend - Routing & UI

### 1. **Protected Routes**
```javascript
// Trong App.jsx
{
  path: "/admin",
  element: (
    <ProtectedRoute requiredRole="ADMIN">
      <RootLayoutAdmin />
    </ProtectedRoute>
  ),
  children: [...]
}
```

### 2. **ProtectedRoute Component**
- Kiểm tra JWT token có tồn tại không
- Kiểm tra role từ `user.scope` field
- Hỗ trợ cả 2 format: `"ADMIN"` và `"ROLE_ADMIN"`
- Hiển thị trang lỗi 403 nếu không có quyền

### 3. **UserDropDown**
- Chỉ hiển thị "Quản trị viên" menu khi `userRole === "ROLE_ADMIN"`
- Đã xóa kiểm tra MANAGER role

### 4. **Admin Pages**
- Member Management: Chọn role chỉ còn ADMIN và USER
- Tất cả trang admin đều được bảo vệ bởi ProtectedRoute

---

## 🗄️ Database Migration

### **Bước 1: Cập nhật Role Table**
Chạy script: `sql/_role__updated_2roles.sql`
```sql
DELETE FROM shop_shoe_superteam.`role`;
INSERT INTO shop_shoe_superteam.`role` (roles) VALUES ('USER'), ('ADMIN');
```

### **Bước 2: Cập nhật User Table**
Chạy script: `sql/_user__updated_2roles.sql`
```sql
-- Admin mẫu
username: admin
password: admin123
role_id: 2 (ADMIN)

-- User mẫu
username: user
password: user123
role_id: 1 (USER)
```

### **Lưu ý Migration**
⚠️ **QUAN TRỌNG**: Nếu có data cũ:
1. Backup database trước
2. Cập nhật tất cả user có `role_id = 2` (MEMBER cũ) → `role_id = 1` (USER mới)
3. Cập nhật tất cả user có `role_id = 3` (MANAGER cũ) → `role_id = 1` hoặc `role_id = 2` tùy nhu cầu
4. Cập nhật tất cả user có `role_id = 4` (ADMIN cũ) → `role_id = 2` (ADMIN mới)

---

## 🧪 Testing

### **Test User Permissions**
1. **USER Role**:
   - ✅ Đăng ký, đăng nhập
   - ✅ Xem sản phẩm, thêm giỏ hàng
   - ✅ Tạo đơn hàng
   - ✅ Xem lịch sử đơn hàng của mình
   - ❌ Không truy cập được `/admin`
   - ❌ Không sửa/xóa sản phẩm

2. **ADMIN Role**:
   - ✅ Tất cả quyền của USER
   - ✅ Truy cập `/admin`
   - ✅ Quản lý users, products, orders, discounts
   - ✅ Xem báo cáo, thống kê

---

## 📝 Files Đã Thay Đổi

### Backend
- ✅ `RoleConstants.java` - Chỉ còn USER, ADMIN
- ✅ `SecurityConfig.java` - Thêm @EnableMethodSecurity
- ✅ `UserController.java` - Thêm @PreAuthorize
- ✅ `OrderController.java` - Thêm @PreAuthorize
- ✅ `OrderDetailController.java` - Thêm @PreAuthorize
- ✅ `DiscountController.java` - Thêm @PreAuthorize
- ✅ `ShoeController.java` - Thêm @PreAuthorize
- ✅ `ReportController.java` - Thêm @PreAuthorize

### Frontend
- ✅ `ProtectedRoute.jsx` - Cải thiện logic kiểm tra role
- ✅ `UserDropDown.jsx` - Xóa kiểm tra MANAGER
- ✅ `MemberManagemantPaging.jsx` - Chỉ 2 role trong dropdown

### SQL
- ✅ `_role__updated_2roles.sql` - Script mới cho 2 roles
- ✅ `_user__updated_2roles.sql` - User mẫu với role mới

### Docs
- ✅ `ROLE_AUTHORIZATION_GUIDE.md` - File này

---

## ⚠️ Lưu Ý Quan Trọng

1. **JWT Token Format**: Backend tạo token với `scope: "ROLE_USER"` hoặc `scope: "ROLE_ADMIN"`
2. **Frontend Parsing**: Frontend nhận và parse role từ `user.scope`
3. **Backward Compatibility**: Code hỗ trợ cả 2 format "ADMIN" và "ROLE_ADMIN"
4. **Manager Routes**: Đã comment/giữ lại file `ManagerAside` và `RootLayoutManager` để tham khảo, nhưng không sử dụng trong routing

---

## 🚀 Triển Khai

### Bước 1: Update Database
```bash
# Chạy trong MySQL
source sql/_role__updated_2roles.sql
source sql/_user__updated_2roles.sql
```

### Bước 2: Build & Run Backend
```bash
cd back-end
mvn clean install
mvn spring-boot:run
```

### Bước 3: Run Frontend
```bash
cd front-end
npm install
npm run dev
```

### Bước 4: Test
1. Đăng nhập với `admin/admin123`
2. Kiểm tra truy cập `/admin` - Thành công ✅
3. Đăng nhập với `user/user123`
4. Kiểm tra truy cập `/admin` - Bị chặn ❌
5. Test các API với Postman/Swagger

---

## 📞 Hỗ Trợ

Nếu gặp vấn đề:
1. Kiểm tra JWT token có chứa `scope` claim không
2. Kiểm tra database đã migration chưa
3. Clear localStorage và đăng nhập lại
4. Kiểm tra console log trong browser và backend

---

**Version**: 2.0  
**Last Updated**: October 2025  
**Status**: ✅ Production Ready
