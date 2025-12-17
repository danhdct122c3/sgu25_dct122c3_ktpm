# 🧪 Auto Test trong Postman - Hướng dẫn chi tiết

## 📋 3 Cách Test Tự Động

### 1️⃣ **Test Scripts trong Request** (Khuyến nghị)
### 2️⃣ **Collection Runner** (Chạy nhiều requests)
### 3️⃣ **Newman CLI** (Command line - CI/CD)

---

## 1. Test Scripts trong Request ⭐

### Cách thêm:
1. Mở request trong Postman
2. Click tab **"Tests"** (bên dưới URL)
3. Viết JavaScript test code
4. Send request → Tests tự động chạy

### 📝 Test Scripts Mẫu:

#### A. Login - POST /api/v1/auth/token
```javascript
// Test status code
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// Test response time
pm.test("Response time < 1000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(1000);
});

// Test response structure
pm.test("Has token field", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.result).to.have.property('token');
    pm.expect(jsonData.result.token).to.not.be.empty;
});

// Auto-save token
if (pm.response.code === 200) {
    var token = pm.response.json().result.token;
    pm.environment.set("access_token", token);
    console.log("✅ Token saved");
}
```

#### B. Login Failed (Password sai)
```javascript
pm.test("Status is 401 Unauthorized", function () {
    pm.response.to.have.status(401);
});

pm.test("Error message is correct", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(401);
    pm.expect(jsonData.message).to.eql("Unauthenticated");
});
```

#### C. Get All Shoes
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is array", function () {
    var shoes = pm.response.json().result;
    pm.expect(shoes).to.be.an('array');
    pm.expect(shoes.length).to.be.above(0);
});

pm.test("Shoes have required fields", function () {
    var shoe = pm.response.json().result[0];
    pm.expect(shoe).to.have.property('id');
    pm.expect(shoe).to.have.property('name');
    pm.expect(shoe).to.have.property('price');
});

// Save first shoe ID
if (pm.response.code === 200) {
    var shoeId = pm.response.json().result[0].id;
    pm.environment.set("shoe_id", shoeId);
}
```

#### D. Create Brand
```javascript
pm.test("Brand created successfully", function () {
    pm.response.to.have.status(200);
    var data = pm.response.json();
    pm.expect(data.flag).to.be.true;
    pm.expect(data.result).to.have.property('id');
});

pm.test("Brand name matches", function () {
    var request = JSON.parse(pm.request.body.raw);
    var response = pm.response.json();
    pm.expect(response.result.name).to.eql(request.name);
});

// Save brand ID
if (pm.response.code === 200) {
    pm.environment.set("brand_id", pm.response.json().result.id);
}
```

#### E. Add to Cart
```javascript
pm.test("Item added to cart", function () {
    pm.response.to.have.status(200);
});

pm.test("Cart updated", function () {
    var data = pm.response.json();
    pm.expect(data.flag).to.be.true;
});
```

#### F. Create Order
```javascript
pm.test("Order created", function () {
    pm.response.to.have.status(200);
    var data = pm.response.json();
    pm.expect(data.result).to.have.property('orderId');
    pm.expect(data.result.orderStatus).to.eql("CREATED");
});

pm.test("Order total is valid", function () {
    var total = pm.response.json().result.finalTotal;
    pm.expect(total).to.be.a('number');
    pm.expect(total).to.be.above(0);
});

// Save order ID
if (pm.response.code === 200) {
    pm.environment.set("order_id", pm.response.json().result.orderId);
}
```

---

## 2. Collection Runner 🏃

### Cách dùng:

#### Bước 1: Mở Runner
- Click **"Runner"** ở góc trái
- Hoặc: `Ctrl/Cmd + Alt + R`

#### Bước 2: Cấu hình
```
✅ Collection: Shoe Shop API Collection
✅ Environment: Local Environment
✅ Iterations: 1
✅ Delay: 500ms (giữa các requests)
✅ Save responses: Check
```

#### Bước 3: Chọn Folder
- Check folder muốn test:
  - ✅ Authentication (9 requests)
  - ✅ User Management (11 requests)
  - ✅ Brands (6 requests)
  - ... hoặc chọn tất cả

#### Bước 4: Run
- Click **"Run Shoe Shop API"**
- Xem kết quả real-time

### Kết quả hiển thị:
```
Iteration 1/1

✅ Login (Get Token)                3/3 passed    250ms
✅ Get All Shoes                    4/4 passed    180ms
✅ Get Shoe by ID                   5/5 passed    120ms
✅ Create Brand                     3/3 passed    200ms
❌ Delete Brand                     2/3 failed    150ms
✅ Add to Cart                      2/2 passed    175ms
✅ Create Order                     4/4 passed    300ms

Total: 23/26 tests passed (88.5%)
```

---

## 3. Newman CLI - Command Line 💻

### Cài đặt:
```powershell
npm install -g newman newman-reporter-htmlextra
```

### Chạy cơ bản:
```powershell
newman run postman_collection.json -e postman_environment_local.json
```

### Output:
```
Shoe Shop API Collection

→ Login (Get Token)
  POST http://localhost:8080/api/v1/auth/token [200 OK, 850B, 245ms]
  ✓  Status code is 200
  ✓  Has token field
  ✓  Response time < 1000ms

→ Get All Shoes
  GET http://localhost:8080/api/v1/shoes [200 OK, 5.2KB, 180ms]
  ✓  Status code is 200
  ✓  Response is array
  ✓  Shoes have required fields

┌─────────────────────────┬──────────┬──────────┐
│                         │ executed │   failed │
├─────────────────────────┼──────────┼──────────┤
│              iterations │        1 │        0 │
│                requests │       96 │        0 │
│            test-scripts │       96 │        0 │
│              assertions │      288 │        0 │
└─────────────────────────┴──────────┴──────────┘
```

### Chạy với HTML Report:
```powershell
newman run postman_collection.json `
  -e postman_environment_local.json `
  -r htmlextra `
  --reporter-htmlextra-export test-reports/report.html
```

### Chạy folder cụ thể:
```powershell
# Chỉ test Authentication
newman run postman_collection.json `
  -e postman_environment_local.json `
  --folder "Authentication"
```

### Chạy với options:
```powershell
newman run postman_collection.json `
  -e postman_environment_local.json `
  --timeout-request 10000 `
  --bail `
  --color on
```

---

## 📚 Common Test Scripts - Copy/Paste

### Status Code Tests
```javascript
pm.test("Status 200", () => pm.response.to.have.status(200));
pm.test("Status 201", () => pm.response.to.have.status(201));
pm.test("Status 400", () => pm.response.to.have.status(400));
pm.test("Status 401", () => pm.response.to.have.status(401));
pm.test("Status 404", () => pm.response.to.have.status(404));
```

### Response Time
```javascript
pm.test("Response < 500ms", () => {
    pm.expect(pm.response.responseTime).to.be.below(500);
});

pm.test("Response < 1000ms", () => {
    pm.expect(pm.response.responseTime).to.be.below(1000);
});
```

### Content Type
```javascript
pm.test("Content-Type is JSON", () => {
    pm.expect(pm.response.headers.get("Content-Type"))
        .to.include("application/json");
});
```

### Response Structure
```javascript
pm.test("Has result field", () => {
    pm.expect(pm.response.json()).to.have.property('result');
});

pm.test("Has flag field", () => {
    var data = pm.response.json();
    pm.expect(data).to.have.property('flag');
    pm.expect(data.flag).to.be.a('boolean');
});
```

### Array Tests
```javascript
pm.test("Response is array", () => {
    pm.expect(pm.response.json().result).to.be.an('array');
});

pm.test("Array not empty", () => {
    pm.expect(pm.response.json().result.length).to.be.above(0);
});
```

### Save to Environment
```javascript
// Save token
var token = pm.response.json().result.token;
pm.environment.set("access_token", token);

// Save ID
var id = pm.response.json().result.id;
pm.environment.set("shoe_id", id);

// Save múltiple fields
var data = pm.response.json().result;
pm.environment.set("user_id", data.userId);
pm.environment.set("username", data.username);
```

### Conditional Tests
```javascript
if (pm.response.code === 200) {
    pm.test("Success response", () => {
        pm.expect(pm.response.json().flag).to.be.true;
    });
}

if (pm.response.code === 401) {
    pm.test("Unauthorized error", () => {
        pm.expect(pm.response.json().message).to.include("Unauthenticated");
    });
}
```

---

## 🎯 Test Scenarios Hoàn Chỉnh

### Scenario 1: Customer Purchase Flow
```
1. Login as Customer
   → Save token
2. Browse Shoes
   → Save shoe_id
3. Get Shoe Detail
4. Add to Cart
   → Verify cart updated
5. Apply Discount Code
   → Verify discount applied
6. Create Order
   → Save order_id
7. Get Payment URL
8. Verify Order Status = CREATED
```

### Scenario 2: Manager Product Management
```
1. Login as Manager
   → Save token
2. Create Brand
   → Save brand_id
3. Upload Brand Logo
4. Create Shoe
   → Save shoe_id
5. Upload Shoe Images
6. Add Variants
   → Save variant_id
7. Update Stock Quantity
8. Get Product List
   → Verify new product appears
```

### Scenario 3: Staff Order Fulfillment
```
1. Login as Staff
   → Save token
2. Get Pending Orders
   → Save order_id
3. Update Status: CONFIRMED
4. Update Status: PREPARING
5. Update Status: READY_FOR_DELIVERY
6. Update Status: OUT_FOR_DELIVERY
7. Update Status: DELIVERED
8. Verify Final Status = DELIVERED
```

---

## 🚀 Quick Start - 5 phút

### Bước 1: Thêm test vào Login
1. Mở request "Login (Get Token)"
2. Click tab "Tests"
3. Copy paste:
```javascript
pm.test("Login OK", () => pm.response.to.have.status(200));
pm.test("Has token", () => {
    pm.expect(pm.response.json().result.token).to.not.be.empty;
});
if (pm.response.code === 200) {
    pm.environment.set("access_token", pm.response.json().result.token);
}
```

### Bước 2: Test một lần
- Send request
- Xem kết quả test bên dưới response

### Bước 3: Chạy toàn bộ collection
- Click "Runner"
- Select collection
- Click "Run"

### Bước 4: Xem report
- Export results
- Hoặc chạy Newman: `newman run postman_collection.json -e postman_environment_local.json -r htmlextra`

---

## 💡 Tips & Best Practices

### ✅ Nên làm:
- Test status code trước
- Test response structure
- Auto-save IDs vào environment
- Test response time
- Test error cases (401, 404, 400)

### ❌ Không nên:
- Hard-code values trong test
- Quên check response time
- Bỏ qua negative tests
- Tests quá phức tạp

### 🎯 Test Coverage:
```
✅ Happy path (200 OK)
✅ Validation errors (400)
✅ Authentication (401)
✅ Authorization (403)
✅ Not found (404)
✅ Response time
✅ Data structure
```

---

## 📊 Báo cáo Newman HTML

Chạy Newman với htmlextra reporter để có báo cáo đẹp:

```powershell
newman run postman_collection.json `
  -e postman_environment_local.json `
  -r htmlextra `
  --reporter-htmlextra-export test-reports/report.html `
  --reporter-htmlextra-title "Shoe Shop API Tests" `
  --reporter-htmlextra-showOnlyFails
```

Report bao gồm:
- Summary dashboard
- Request/response details
- Failed tests highlighted
- Performance metrics
- Environment variables used

---

**Bạn muốn tôi tạo collection MỚI với tất cả test scripts đã setup sẵn không?** 🚀
