# 📮 Postman Collection - Hướng Dẫn Sử Dụng

## 📥 Import Collection vào Postman

### Bước 1: Import Collection
1. Mở Postman Desktop hoặc Web
2. Click **Import** ở góc trên bên trái
3. Chọn file `postman_collection.json`
4. Click **Import**

### Bước 2: Import Environment
1. Click biểu tượng **⚙️ Settings** → **Manage Environments**
2. Click **Import**
3. Chọn file `postman_environment_local.json` (hoặc `production`)
4. Chọn environment vừa import từ dropdown ở góc trên bên phải

---

## 🔑 Authentication Flow

### 1. Login để lấy Token
Trước khi test các API khác, bạn cần đăng nhập:

```
POST {{base_url}}/auth/token
Body:
{
    "username": "admin",
    "password": "admin123"
}
```

**Response sẽ tự động lưu token vào biến `{{access_token}}`**

### 2. Các loại tài khoản để test

| Username | Password | Role | Mô tả |
|----------|----------|------|-------|
| `admin` | `admin123` | ADMIN | Quản lý users |
| `manager` | `manager123` | MANAGER | Quản lý sản phẩm, đơn hàng, báo cáo |
| `staff01` | `staff123` | STAFF | Xử lý đơn hàng |
| `customer01` | `password123` | CUSTOMER | Khách hàng |

---

## 📂 Cấu Trúc Collection

Collection được chia thành **14 folders** theo chức năng:

### 1️⃣ Authentication (9 requests)
- ✅ Login (lưu token tự động)
- ✅ Introspect token
- ✅ Get current user info
- ✅ Change password
- ✅ Send OTP email
- ✅ Verify OTP
- ✅ Reset password
- ✅ Logout

### 2️⃣ User Management (10 requests)
- ✅ Register new user
- ✅ Get all users (ADMIN)
- ✅ Get user by ID
- ✅ Get user profile
- ✅ Update user
- ✅ Get users by role/status
- ✅ Pagination
- ✅ Create admin account

### 3️⃣ Brand Management (8 requests)
- ✅ CRUD operations
- ✅ Upload logo
- ✅ Update with logo
- ✅ Get summary

### 4️⃣ Shoe Management (13 requests)
- ✅ CRUD operations
- ✅ Filter by gender/brand/category
- ✅ Multi-filter shop page
- ✅ Pagination & sorting
- ✅ Get categories/genders

### 5️⃣ Shoe Variants (7 requests)
- ✅ Get all sizes
- ✅ Initialize size chart
- ✅ CRUD variants (size + stock)

### 6️⃣ Shoe Images (6 requests)
- ✅ CRUD shoe images
- ✅ Get images by shoe ID
- ✅ Delete all images

### 7️⃣ Image Upload (2 requests)
- ✅ Upload single/multiple images

### 8️⃣ Shopping Cart (5 requests)
- ✅ Get cart
- ✅ Add/Update/Remove items
- ✅ Clear cart

### 9️⃣ Orders (9 requests)
- ✅ Create order
- ✅ Apply discount
- ✅ Cancel order
- ✅ Get orders by user/status
- ✅ Update order status
- ✅ Pagination

### 🔟 Payment (VNPay) (3 requests)
- ✅ Create payment URL
- ✅ Create payment for order
- ✅ Payment callback

### 1️⃣1️⃣ Discounts (8 requests)
- ✅ CRUD discounts
- ✅ Filter by active/type
- ✅ Pagination

### 1️⃣2️⃣ Reports (6 requests)
- ✅ Daily/Monthly revenue
- ✅ Top selling products
- ✅ Top customers
- ✅ Inventory status

### 1️⃣3️⃣ AI Chat (2 requests)
- ✅ Query shoe data
- ✅ Query discount data

### 1️⃣4️⃣ Debug (1 request)
- ✅ Debug shoe images

---

## 🚀 Quick Start - Test Flow

### Scenario 1: Customer mua hàng

```
1. Login as Customer
   POST /auth/token (username: customer01)

2. Browse shoes
   GET /shoes/list-shoes?page=0&size=10

3. View shoe detail
   GET /shoes/{shoe_id}

4. Add to cart
   POST /cart/add
   Body: { "variantId": "xxx", "quantity": 2 }

5. View cart
   GET /cart

6. Apply discount
   POST /orders/apply-discount
   Body: { "userId": "xxx", "coupon": "SUMMER2024" }

7. Create order
   POST /orders/create
   Body: { "discountCode": "SUMMER2024" }

8. Create payment
   POST /payment/create-payment-order
   Body: { "orderId": "xxx" }

9. Check order status
   GET /order-details/order/{order_id}
```

### Scenario 2: Manager quản lý sản phẩm

```
1. Login as Manager
   POST /auth/token (username: manager)

2. Create brand
   POST /brands
   Body: { "brandName": "Nike", "description": "..." }

3. Upload brand logo
   POST /brands/{brand_id}/logo
   Form-data: file

4. Create shoe
   POST /shoes
   Body: { "name": "Air Max", "price": 120, "brandId": 1, ... }

5. Upload shoe images
   POST /images/upload
   Form-data: files[]

6. Add shoe image reference
   POST /shoe-images/shoe/{shoe_id}
   Body: { "imageUrl": "..." }

7. Create variants (sizes)
   POST /shoes/{shoe_id}/variants
   Body: { "sizeId": 1, "stockQuantity": 50 }

8. View inventory
   GET /report/inventory-status
```

### Scenario 3: Staff xử lý đơn hàng

```
1. Login as Staff
   POST /auth/token (username: staff01)

2. Get pending orders
   GET /order-details/orderStatus?orderStatus=CREATED

3. Update order status
   PUT /order-details/order/{order_id}
   Body: { "newStatus": "CONFIRMED" }

4. Continue processing
   PUT /order-details/order/{order_id}
   Body: { "newStatus": "PREPARING" }

5. Ready for delivery
   PUT /order-details/order/{order_id}
   Body: { "newStatus": "READY_FOR_DELIVERY" }

6. Out for delivery
   PUT /order-details/order/{order_id}
   Body: { "newStatus": "OUT_FOR_DELIVERY" }

7. Delivered
   PUT /order-details/order/{order_id}
   Body: { "newStatus": "DELIVERED" }
```

---

## 🔧 Environment Variables

Collection sử dụng các biến sau:

| Variable | Mô tả | Auto-save |
|----------|-------|-----------|
| `{{base_url}}` | API base URL | ❌ |
| `{{access_token}}` | JWT token | ✅ Auto |
| `{{user_id}}` | User ID hiện tại | Manual |
| `{{shoe_id}}` | Shoe ID để test | ✅ Auto |
| `{{brand_id}}` | Brand ID để test | ✅ Auto |
| `{{order_id}}` | Order ID để test | ✅ Auto |
| `{{discount_id}}` | Discount ID để test | ✅ Auto |
| `{{variant_id}}` | Variant ID để test | ✅ Auto |

**✅ Các biến được đánh dấu "Auto" sẽ tự động lưu khi tạo resource mới**

---

## 📝 Notes quan trọng

### 1. Authorization
- Collection đã cấu hình **Bearer Token** authentication tự động
- Token được lưu vào `{{access_token}}` sau khi login
- Các endpoint public (không cần token) đã được đánh dấu `auth: noauth`

### 2. Order Status Flow
```
CREATED → PAID (auto by VNPay)
       ↓
    CONFIRMED (by Staff/Manager)
       ↓
    PREPARING
       ↓
    READY_FOR_DELIVERY
       ↓
    OUT_FOR_DELIVERY
       ↓
    DELIVERED

Hoặc: CANCELLED (by Customer) / REJECTED (by Admin)
```

### 3. Discount Types
- `PERCENTAGE`: Giảm theo phần trăm
- `FIXED_AMOUNT`: Giảm số tiền cố định

### 4. Gender Values
- `MAN` / `WOMEN` / `UNISEX`

### 5. Category Values
- `RUNNING` / `CASUAL` / `SNEAKER` / `SPORT`

---

## 🐛 Troubleshooting

### Token hết hạn
```
Error: 401 Unauthorized
Solution: Gọi lại endpoint "Login" để lấy token mới
```

### CORS Error
```
Error: CORS policy blocked
Solution: Đảm bảo backend đã enable CORS cho origin của bạn
```

### Variable không tự động lưu
```
Problem: {{shoe_id}} vẫn trống sau khi tạo shoe
Solution: 
1. Kiểm tra tab "Tests" của request
2. Đảm bảo response trả về status 200
3. Check console log trong Postman
```

---

## 📊 Test Scripts Có Sẵn

Một số request đã được cấu hình **Test Scripts** để tự động lưu biến:

```javascript
// Login
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.collectionVariables.set("access_token", jsonData.result.token);
}

// Create Shoe
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.collectionVariables.set("shoe_id", jsonData.result.id);
}

// Tương tự cho: brand_id, order_id, discount_id, variant_id
```

---

## 📞 Support

Nếu gặp vấn đề:
1. Check server đang chạy: `http://localhost:8080/api/v1/actuator/health`
2. Kiểm tra database connection
3. Xem logs trong terminal backend
4. Review Postman Console (View → Show Postman Console)

---

**Happy Testing! 🚀**
