# 🛒 Fix: User không xem được lịch sử đơn hàng

## ❌ Vấn đề

User đăng nhập nhưng **KHÔNG thấy lịch sử đơn hàng** của mình.

### 🔍 Nguyên nhân gốc rễ:

**Frontend gửi sai parameter!**

```javascript
// ❌ SAI - Frontend gửi userData.id (số integer)
const response = await api.get(`/order-details/user/${userData.id}`);
// VD: GET /order-details/user/123
```

```java
// Backend check quyền bằng USERNAME (string)
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.claims['sub']")
@GetMapping("/user/{userId}")
public APIResponse<List<OrderDetailResponse>> getOrderDetailsByUserId(
    @PathVariable String userId  // ← Nhận "123" (string)
)
```

**Vấn đề xảy ra**:
1. Frontend gửi: `userId = 123` (number ID)
2. Backend nhận: `userId = "123"` (string)
3. Backend check: `"123" == authentication.principal.claims['sub']` 
4. Trong JWT token, `claims['sub']` là **USERNAME** (VD: "user123" hoặc "admin")
5. So sánh: `"123" == "user123"` → **FALSE** ❌
6. Spring Security từ chối request → **403 Forbidden**
7. User không thấy đơn hàng của mình!

---

## ✅ Giải pháp

### Sửa Frontend - Gửi USERNAME thay vì ID

**File**: `OrderDetailList.jsx`

**TRƯỚC:**
```javascript
useEffect(() => {
  const fetchOrderInfo = async () => {
    try {
      // ❌ Gửi userData.id (số)
      const response = await api.get(`/order-details/user/${userData.id}`);
      setOrderList(response.data.result);
    } catch (error) {
      console.error(error);
    }
  };
  fetchOrderInfo();
}, [userData.id]); // ❌ Dependency là userData.id
```

**SAU:**
```javascript
useEffect(() => {
  const fetchOrderInfo = async () => {
    // Kiểm tra xem đã có username chưa (từ JWT token)
    if (!userName) {
      console.log("Waiting for username...");
      return;
    }
    
    try {
      // ✅ GỬI USERNAME thay vì userData.id
      // Backend check quyền bằng username (authentication.principal.claims['sub'])
      const response = await api.get(`/order-details/user/${userName}`);
      console.log("📦 Order history:", response.data.result);
      setOrderList(response.data.result);
    } catch (error) {
      console.error("❌ Error fetching orders:", error);
      if (error.response?.status === 403) {
        console.error("🚫 Access denied - không có quyền xem đơn hàng này");
      }
    }
  };
  fetchOrderInfo();
}, [userName]); // ✅ Đổi dependency từ userData.id sang userName
```

**Thay đổi chính**:
1. ✅ Đổi từ `userData.id` → `userName`
2. ✅ Thêm check `if (!userName)` để tránh gọi API khi chưa có username
3. ✅ Dependency array đổi từ `[userData.id]` → `[userName]`
4. ✅ Thêm error handling rõ ràng cho 403 Forbidden

---

## 📊 Flow hoạt động sau khi fix

### ✅ FLOW ĐÚNG:

```
1. User đăng nhập
   ↓
2. JWT token được lưu (chứa username trong claims['sub'])
   ↓
3. Redux store lấy username từ token
   userName = user.sub (VD: "user123")
   ↓
4. Frontend gọi API
   GET /order-details/user/user123
   Headers: Authorization: Bearer <jwt-token>
   ↓
5. Backend nhận request
   @PathVariable userId = "user123"
   ↓
6. Spring Security check quyền
   @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.claims['sub']")
   
   Kiểm tra:
   - hasRole('ADMIN')? → false (user thường)
   - #userId == authentication.principal.claims['sub']?
     → "user123" == "user123" → TRUE ✅
   ↓
7. Cho phép truy cập ✅
   ↓
8. Service query database
   orderRepository.findByUserIdOrderByOrderDateDesc("user123")
   ↓
9. Trả về danh sách đơn hàng
   ↓
10. Frontend hiển thị lịch sử đơn hàng ✅
```

---

## 🧪 Cách kiểm tra (Testing)

### Test 1: User xem lịch sử đơn hàng của mình ✅

```
1. Đăng nhập với tài khoản USER:
   - Username: user
   - Password: user123

2. Vào trang "Lịch sử đơn hàng"
   - URL: http://localhost:3000/order-history

3. Mở DevTools (F12) → Tab Console

4. KẾT QUẢ MONG ĐỢI:
   ✅ Thấy log: "📦 Order history: [...]"
   ✅ Hiển thị danh sách đơn hàng của user
   ✅ Nếu chưa có đơn: Hiển thị "Chưa có đơn hàng nào"
   ✅ KHÔNG có lỗi 403 Forbidden
```

### Test 2: Kiểm tra Network Request

```
1. Mở DevTools (F12) → Tab Network
2. Reload trang lịch sử đơn hàng
3. Tìm request: /order-details/user/...

4. KẾT QUẢ MONG ĐỢI:
   ✅ Request URL: /order-details/user/user (hoặc username của bạn)
   ✅ Status: 200 OK
   ✅ Response body: { flag: true, result: [...] }
   ❌ KHÔNG phải: Status 403 Forbidden
```

### Test 3: User không thể xem đơn hàng người khác ❌

```
1. Đăng nhập với user1
2. Mở Console (F12)
3. Thử gọi API xem đơn hàng của user2:

fetch('/api/order-details/user/user2', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
})
.then(r => r.json())
.then(console.log)

4. KẾT QUẢ MONG ĐỢI:
   ❌ Status: 403 Forbidden
   ❌ Message: "Access Denied"
   
   → Bảo mật hoạt động đúng! User1 không xem được đơn của User2
```

### Test 4: Admin xem được TẤT CẢ đơn hàng ✅

```
1. Đăng nhập Admin
2. Có thể gọi:
   - /order-details/user/user1 → ✅ Xem được
   - /order-details/user/user2 → ✅ Xem được
   - /order-details → ✅ Xem tất cả

Lý do: @PreAuthorize("hasRole('ADMIN') or #userId == ...")
       Admin có quyền xem đơn của BẤT KỲ user nào
```

---

## 🔐 Giải thích bảo mật

### Backend Security Logic:

```java
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.claims['sub']")
@GetMapping("/user/{userId}")
public APIResponse<List<OrderDetailResponse>> getOrderDetailsByUserId(
    @PathVariable String userId
)
```

**Điều kiện cho phép truy cập**:
1. `hasRole('ADMIN')` → User có role ADMIN ✅
2. **HOẶC** `#userId == authentication.principal.claims['sub']`
   - `#userId`: Parameter từ URL path (VD: "user123")
   - `authentication.principal.claims['sub']`: Username trong JWT token
   - Chỉ cho phép user xem đơn hàng của **CHÍNH MÌNH**

**Ví dụ**:
- User "user123" gọi `/order-details/user/user123` → ✅ OK
- User "user123" gọi `/order-details/user/admin` → ❌ DENIED (403)
- Admin gọi `/order-details/user/user123` → ✅ OK (vì là ADMIN)

---

## 📝 File đã sửa

1. ✅ `front-end/src/pages/shop-pages/OrderDetailList.jsx`
   - Đổi từ `userData.id` sang `userName`
   - Thêm check `if (!userName)`
   - Dependency từ `[userData.id]` → `[userName]`
   - Thêm error handling cho 403

---

## 🎯 So sánh trước/sau

| Aspect | ❌ TRƯỚC | ✅ SAU |
|--------|---------|--------|
| **API Call** | `/order-details/user/123` | `/order-details/user/user123` |
| **Parameter** | userData.id (number) | userName (string) |
| **Security Check** | `"123" == "user123"` → FALSE | `"user123" == "user123"` → TRUE |
| **Result** | 403 Forbidden ❌ | 200 OK ✅ |
| **User Experience** | Không thấy đơn hàng | Thấy lịch sử đơn hàng ✅ |

---

## ⚠️ Lưu ý quan trọng

### 1. Backend sử dụng USERNAME để check quyền

Trong hệ thống này:
- `userId` trong database là **số** (integer)
- `userId` trong JWT token (`sub` claim) là **string username**
- Spring Security check quyền bằng **username**, không phải ID

### 2. Tại sao không dùng ID?

**Vấn đề với ID**:
- ID có thể đoán được (1, 2, 3, ...)
- Attacker có thể brute force: `/order-details/user/1`, `/order-details/user/2`, ...
- Dù có bảo vệ bằng `@PreAuthorize` nhưng username an toàn hơn

**Ưu điểm của Username**:
- Không đoán được ID của user khác
- JWT token đã chứa username (không cần query thêm)
- Phù hợp với logic Spring Security

### 3. Alternative: Dùng "me" endpoint

Một cách khác là tạo endpoint `/order-details/me`:

```java
@PreAuthorize("isAuthenticated()")
@GetMapping("/me")
public APIResponse<List<OrderDetailResponse>> getMyOrders() {
    // Lấy username từ SecurityContext
    String username = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName();
    
    return APIResponse.<List<OrderDetailResponse>>builder()
        .result(orderDetailService.getAllOrdersByUserId(username))
        .build();
}
```

Frontend chỉ cần gọi:
```javascript
const response = await api.get('/order-details/me');
```

Đơn giản và an toàn hơn!

---

## 🚀 Deploy & Test

```bash
# 1. Frontend đã sửa, restart:
cd front-end
npm run dev

# 2. Test ngay:
# - Đăng nhập với user/user123
# - Vào /order-history
# - Kiểm tra Console logs
# - Xem có lỗi 403 không

# 3. Nếu vẫn lỗi, check:
# - Token có hợp lệ không? (jwt.io)
# - Username trong token có đúng không?
# - API endpoint có được gọi đúng không? (Network tab)
```

---

## ✅ Checklist

Sau khi fix, kiểm tra:

- [ ] User đăng nhập thành công ✅
- [ ] Token được lưu trong localStorage ✅
- [ ] Username lấy từ Redux store đúng ✅
- [ ] API call dùng username thay vì ID ✅
- [ ] Request URL: `/order-details/user/username` ✅
- [ ] Status: 200 OK (không phải 403) ✅
- [ ] Hiển thị danh sách đơn hàng ✅
- [ ] Nếu chưa có đơn: Hiển thị "Chưa có đơn hàng nào" ✅
- [ ] User không xem được đơn của người khác ✅

---

## 🐛 Troubleshooting

### Vấn đề: Vẫn bị 403 Forbidden

**Kiểm tra**:
1. Xem Console log có username không?
   ```javascript
   console.log("Username:", userName);
   ```

2. Check JWT token:
   - Copy token từ localStorage
   - Paste vào https://jwt.io
   - Xem field `sub` có đúng username không?

3. Check API request trong Network tab:
   - URL có đúng `/order-details/user/username` không?
   - Header `Authorization` có token không?

### Vấn đề: Username là null/undefined

**Nguyên nhân**: Redux store chưa load user

**Giải pháp**: Đã thêm check `if (!userName) return;` trong code

### Vấn đề: Hiển thị "Chưa có đơn hàng" nhưng có đơn

**Nguyên nhân**: Database dùng userId khác với username

**Kiểm tra**:
```sql
-- Xem orders của user
SELECT * FROM customer_order WHERE user_id = 'user123';

-- Nếu không có, check xem user_id được lưu như thế nào
SELECT * FROM customer_order LIMIT 5;
```

---

**Chúc bạn fix thành công! 🎉**

Nếu vẫn có vấn đề, hãy check:
1. Console logs (F12)
2. Network tab (F12 → Network)
3. JWT token decode (jwt.io)
4. Backend logs
