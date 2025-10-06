# 🔒 Hướng dẫn kiểm tra bảo mật trang Admin

## ❌ Vấn đề trước đó

1. **Người chưa đăng nhập vào được trang Admin**: Khi reload trang, Redux store bị reset nhưng token vẫn còn trong localStorage, user state không được khôi phục từ token
2. **User thường vào được trang Admin**: Logic kiểm tra role chưa đủ chặt chẽ

## ✅ Giải pháp đã áp dụng

### 1. Cải thiện `auth.js` (Redux Store)
**File**: `front-end/src/store/auth.js`

**Thay đổi**:
- Thêm hàm `getUserFromToken()` để tự động khôi phục user từ token trong localStorage khi khởi tạo app
- Kiểm tra token có còn hạn không (exp field)
- Tự động xóa token hết hạn hoặc invalid

```javascript
// Khôi phục user ngay khi khởi tạo Redux store
const initialState = {
  user: getUserFromToken(), // ✅ User được khôi phục ngay từ đầu
  token: localStorage.getItem("token") || null,
  isLoading: false,
  error: null,
};
```

### 2. Cải thiện `ProtectedRoute.jsx`
**File**: `front-end/src/components/ProtectedRoute.jsx`

**Thay đổi**:
- Cải thiện logic kiểm tra role với chuẩn hóa format (hỗ trợ cả "ADMIN" và "ROLE_ADMIN")
- Thêm logging chi tiết để debug
- Hiển thị thông tin role rõ ràng khi bị từ chối truy cập
- Kiểm tra chặt chẽ token và user trước khi cho phép truy cập

```javascript
// Kiểm tra đăng nhập
if (!token || !user) {
  return <Navigate to="/login" replace />; // ✅ Redirect về login nếu chưa đăng nhập
}

// Kiểm tra role
const normalizedUserRole = userRole.replace('ROLE_', '');
const normalizedRequiredRole = requiredRole.replace('ROLE_', '');
const hasRequiredRole = normalizedUserRole === normalizedRequiredRole;

if (!hasRequiredRole) {
  return <AccessDeniedPage />; // ✅ Hiển thị trang lỗi nếu không đủ quyền
}
```

### 3. Tối ưu `App.jsx`
**File**: `front-end/src/App.jsx`

**Thay đổi**:
- Xóa useEffect khôi phục token (không cần nữa vì đã xử lý trong auth.js)
- Tránh decode token 2 lần
- Đơn giản hóa component

```javascript
function App() {
  // ✅ Không cần useEffect nữa, user đã được khôi phục trong initialState
  return <RouterProvider router={router}></RouterProvider>;
}
```

---

## 🧪 Hướng dẫn kiểm tra (Testing Guide)

### Test Case 1: Người chưa đăng nhập truy cập trang Admin ❌

**Các bước**:
1. Đảm bảo đã logout (hoặc clear localStorage)
2. Mở trình duyệt ở chế độ Incognito/Private
3. Truy cập trực tiếp: `http://localhost:5173/admin`

**Kết quả mong đợi**: ✅
- Tự động redirect về trang `/login`
- Không thấy nội dung trang admin
- Console log hiển thị: `"❌ No token or user found - redirecting to login"`

### Test Case 2: User thường (ROLE_USER) truy cập trang Admin ❌

**Các bước**:
1. Đăng nhập với tài khoản USER thường:
   - Username: `user`
   - Password: `user123`
2. Sau khi đăng nhập thành công, thử truy cập: `http://localhost:5173/admin`

**Kết quả mong đợi**: ✅
- Hiển thị trang "Không có quyền truy cập"
- Thấy thông báo: "🚫 Không có quyền truy cập"
- Hiển thị rõ ràng:
  - "Quyền của bạn: **USER**"
  - "Quyền yêu cầu: **ADMIN**"
- Console log hiển thị: `"❌ Access denied - insufficient permissions"`
- Có 2 nút: "🏠 Về trang chủ" và "⬅️ Quay lại"

### Test Case 3: Admin (ROLE_ADMIN) truy cập trang Admin ✅

**Các bước**:
1. Đăng nhập với tài khoản ADMIN:
   - Username: `admin`
   - Password: `admin123`
2. Truy cập: `http://localhost:5173/admin`

**Kết quả mong đợi**: ✅
- Vào được trang admin bình thường
- Hiển thị dashboard admin
- Console log hiển thị: `"✅ Access granted - rendering protected content"`
- Thấy tên user và role: "Xin chào: **admin (ADMIN)**"

### Test Case 4: Token hết hạn ⏰

**Các bước**:
1. Đăng nhập với bất kỳ tài khoản nào
2. Chờ token hết hạn (hoặc sửa exp trong localStorage thành thời gian quá khứ)
3. Reload trang hoặc truy cập `/admin`

**Kết quả mong đợi**: ✅
- Token hết hạn tự động bị xóa khỏi localStorage
- User bị redirect về `/login`
- Console log hiển thị: `"Invalid token"` hoặc token đã hết hạn

### Test Case 5: Reload trang khi đã đăng nhập 🔄

**Các bước**:
1. Đăng nhập với tài khoản ADMIN
2. Vào trang admin: `http://localhost:5173/admin`
3. Reload trang (F5 hoặc Ctrl+R)

**Kết quả mong đợi**: ✅
- Vẫn giữ trạng thái đăng nhập
- Không bị redirect về login
- Vẫn thấy dashboard admin
- User info vẫn hiển thị đúng

### Test Case 6: Thay đổi role trong localStorage (Hacking attempt) 🔓

**Các bước**:
1. Đăng nhập với tài khoản USER
2. Mở DevTools → Application → Local Storage
3. Thử sửa token hoặc thêm fake data
4. Reload trang và truy cập `/admin`

**Kết quả mong đợi**: ✅
- Token invalid tự động bị xóa
- Hoặc role vẫn được lấy từ JWT token (không thể fake)
- Vẫn bị chặn truy cập nếu không phải ADMIN

---

## 🔍 Debug Console Logs

Khi test, hãy mở Console (F12) để xem các log sau:

### Khi vào ProtectedRoute:
```
🔒 ProtectedRoute - User: {sub: "admin", scope: "ROLE_ADMIN", ...}
🔒 ProtectedRoute - Token exists: true
🔒 ProtectedRoute - Required Role: ADMIN
👤 User role from token: ROLE_ADMIN
🎯 Required role: ADMIN
✅ Has required role: true
✅ Access granted - rendering protected content
```

### Khi bị chặn:
```
🔒 ProtectedRoute - User: {sub: "user", scope: "ROLE_USER", ...}
🔒 ProtectedRoute - Token exists: true
🔒 ProtectedRoute - Required Role: ADMIN
👤 User role from token: ROLE_USER
🎯 Required role: ADMIN
✅ Has required role: false
❌ Access denied - insufficient permissions
```

### Khi chưa đăng nhập:
```
🔒 ProtectedRoute - User: null
🔒 ProtectedRoute - Token exists: false
🔒 ProtectedRoute - Required Role: ADMIN
❌ No token or user found - redirecting to login
```

---

## 📊 Tổng kết các Route được bảo vệ

| Route Pattern | Yêu cầu đăng nhập | Yêu cầu Role | Protected |
|--------------|-------------------|--------------|-----------|
| `/` | ❌ Không | - | ❌ Public |
| `/shoes` | ❌ Không | - | ❌ Public |
| `/shoes/:id` | ❌ Không | - | ❌ Public |
| `/cart` | ❌ Không | - | ❌ Public |
| `/login` | ❌ Không | - | ❌ Public |
| `/register` | ❌ Không | - | ❌ Public |
| `/admin` | ✅ Có | ADMIN | ✅ Protected |
| `/admin/*` | ✅ Có | ADMIN | ✅ Protected (tất cả sub-routes) |

---

## 🛠️ Troubleshooting

### Vấn đề: Vẫn vào được admin sau khi fix

**Giải pháp**:
1. Clear cache trình duyệt (Ctrl+Shift+Delete)
2. Clear localStorage: DevTools → Application → Local Storage → Clear All
3. Restart frontend dev server:
   ```bash
   cd front-end
   npm run dev
   ```
4. Test trong chế độ Incognito

### Vấn đề: Token bị xóa liên tục

**Nguyên nhân**: Token có thể hết hạn hoặc invalid

**Kiểm tra**:
1. Mở DevTools → Application → Local Storage
2. Copy token
3. Vào https://jwt.io và paste token
4. Kiểm tra field `exp` (expiration time)
5. Kiểm tra `scope` có đúng format không

### Vấn đề: Console báo lỗi decode token

**Giải pháp**:
- Token có thể bị corrupt
- Clear localStorage và đăng nhập lại
- Kiểm tra backend có trả đúng JWT format không

---

## 📝 Checklist sau khi fix

- [ ] Test Case 1: Chưa đăng nhập → Redirect về /login ✅
- [ ] Test Case 2: USER vào /admin → Hiển thị "Không có quyền" ✅
- [ ] Test Case 3: ADMIN vào /admin → Vào được bình thường ✅
- [ ] Test Case 4: Token hết hạn → Tự động logout ✅
- [ ] Test Case 5: Reload trang → Giữ trạng thái đăng nhập ✅
- [ ] Test Case 6: Hack localStorage → Vẫn bị chặn ✅
- [ ] Console logs hiển thị đúng ✅
- [ ] UI hiển thị thông báo lỗi rõ ràng ✅

---

## 🔐 Bảo mật bổ sung (Optional)

### 1. Rate Limiting cho Login API
Backend nên implement rate limiting để chống brute force attack.

### 2. Refresh Token
Thêm refresh token mechanism để tăng security (access token ngắn hạn + refresh token dài hạn).

### 3. HTTPS
Production phải dùng HTTPS để bảo vệ token khi truyền qua network.

### 4. Content Security Policy (CSP)
Thêm CSP headers để chống XSS attacks.

### 5. Backend Validation
**QUAN TRỌNG**: Frontend security chỉ là UI protection. Backend PHẢI có validation với `@PreAuthorize("hasRole('ADMIN')")` trên tất cả admin endpoints.

---

## 📞 Liên hệ

Nếu có vấn đề gì, hãy check:
1. Console logs (F12)
2. Network tab để xem API responses
3. Application → Local Storage để xem token
4. Backend logs để xem authorization

**Chúc bạn test thành công! 🎉**
