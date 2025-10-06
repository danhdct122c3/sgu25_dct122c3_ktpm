# 🗑️ Fix: Sản phẩm đã xóa vẫn hiển thị ở trang mua hàng

## ❌ Vấn đề

### Vấn đề 1: Xóa sản phẩm chỉ ẩn chứ không xóa hẳn
**Nguyên nhân**: Backend đã implement **SOFT DELETE** (xóa mềm) thay vì hard delete (xóa hẳn).
- Khi admin bấm "Xóa", sản phẩm chỉ được đánh dấu `status = false` (ẩn)
- Sản phẩm vẫn còn trong database
- Mục đích: Giữ lại dữ liệu lịch sử, có thể khôi phục sau

### Vấn đề 2: Sản phẩm đã ẩn vẫn hiện ở trang mua hàng ⚠️
**Nguyên nhân**: API `GET /shoes` trả về **TẤT CẢ** sản phẩm (kể cả status=false)
- Frontend gọi API để lấy danh sách sản phẩm
- Backend trả về cả shoes đã ẩn (status=false)
- Khách hàng thấy cả sản phẩm đã xóa → **BUG nghiêm trọng!**

---

## ✅ Giải pháp đã áp dụng

### 1. Thêm Query Methods vào ShoeRepository
**File**: `ShoeRepository.java`

```java
// Thêm các query methods để lọc theo status
List<Shoe> findByStatusTrue(); // Chỉ lấy shoes active
List<Shoe> findByStatusTrueOrderByCreatedAtDesc(); // Shoes active, sắp xếp theo ngày tạo
List<Shoe> findByStatusTrueAndGender(ShoeConstants.Gender gender);
List<Shoe> findByStatusTrueAndBrand(Brand brand);
List<Shoe> findByStatusTrueAndCategory(ShoeConstants.Category category);
List<Shoe> findByStatusTrueAndNameContainingIgnoreCase(String shoeName);
```

### 2. Tách API cho Customer vs Admin
**File**: `ShoeService.java`

#### Cho khách hàng (Customer) - CHỈ trả về sản phẩm active:
```java
public List<ShoeResponse> getAllShoes() {
    // ✅ CHỈ lấy shoes có status = true
    List<Shoe> shoes = shoeRepository.findByStatusTrue();
    return shoes.stream()
            .map(shoeHelper::getShoeResponse)
            .toList();
}

public List<ShoeResponse> getShoesByGender(String gender) {
    // ✅ CHỈ lấy shoes active theo gender
    List<Shoe> shoes = shoeRepository.findByStatusTrueAndGender(...);
    ...
}

// Tương tự cho: getShoesByBrand, getShoesByCategory, getShoesByName
```

#### Cho Admin - Trả về TẤT CẢ (kể cả đã ẩn):
```java
@PreAuthorize("hasRole('ADMIN')")
public List<ShoeResponse> getAllShoesForAdmin() {
    // ✅ Lấy TẤT CẢ shoes (bao gồm status=false) để admin quản lý
    List<Shoe> shoes = shoeRepository.findAll();
    return shoes.stream()
            .map(shoeHelper::getShoeResponse)
            .toList();
}
```

### 3. Thêm endpoint mới cho Admin
**File**: `ShoeController.java`

```java
// Endpoint cũ (cho khách hàng) - CHỈ trả về active shoes
@GetMapping
public APIResponse<List<ShoeResponse>> getAllShoes() {
    return APIResponse.<List<ShoeResponse>>builder()
            .result(shoeService.getAllShoes()) // ✅ Chỉ active
            .build();
}

// Endpoint mới (cho admin) - Trả về TẤT CẢ
@GetMapping("/admin/all")
@PreAuthorize("hasRole('ADMIN')")
public APIResponse<List<ShoeResponse>> getAllShoesForAdmin() {
    return APIResponse.<List<ShoeResponse>>builder()
            .result(shoeService.getAllShoesForAdmin()) // ✅ Tất cả
            .build();
}
```

---

## 📊 So sánh Before/After

### ❌ TRƯỚC KHI SỬA (BUG):

```
Customer gọi API: GET /shoes
Backend trả về:
[
  { id: 1, name: "Nike Air Max", status: true },   ← Active
  { id: 2, name: "Adidas Ultra", status: true },   ← Active
  { id: 3, name: "Puma Classic", status: false },  ← ĐÃ XÓA nhưng vẫn trả về ⚠️
  { id: 4, name: "Reebok Sport", status: false }   ← ĐÃ XÓA nhưng vẫn trả về ⚠️
]

→ Khách hàng thấy cả sản phẩm đã xóa!
```

### ✅ SAU KHI SỬA (FIXED):

```
Customer gọi API: GET /shoes
Backend trả về:
[
  { id: 1, name: "Nike Air Max", status: true },   ← Active
  { id: 2, name: "Adidas Ultra", status: true }    ← Active
]
→ Khách hàng CHỈ thấy sản phẩm active ✅

---

Admin gọi API: GET /shoes/admin/all
Backend trả về:
[
  { id: 1, name: "Nike Air Max", status: true },   ← Active
  { id: 2, name: "Adidas Ultra", status: true },   ← Active
  { id: 3, name: "Puma Classic", status: false },  ← Đã ẩn
  { id: 4, name: "Reebok Sport", status: false }   ← Đã ẩn
]
→ Admin thấy TẤT CẢ để quản lý ✅
```

---

## 🧪 Cách kiểm tra (Testing Guide)

### Test 1: Xóa sản phẩm ở Admin
```
1. Đăng nhập Admin
2. Vào trang "Quản lý sản phẩm"
3. Chọn 1 sản phẩm và bấm "Xóa"
4. KẾT QUẢ:
   ✅ Sản phẩm biến mất khỏi danh sách (nếu dùng /shoes)
   ✅ Sản phẩm vẫn hiện nhưng có mark "Đã ẩn" (nếu dùng /shoes/admin/all)
```

### Test 2: Kiểm tra trang mua hàng (Customer)
```
1. Logout khỏi admin (hoặc mở Incognito)
2. Vào trang shop: http://localhost:5173/shoes
3. Xem danh sách sản phẩm
4. KẾT QUẢ:
   ✅ KHÔNG thấy sản phẩm đã xóa
   ✅ CHỈ thấy sản phẩm active (status=true)
```

### Test 3: Kiểm tra filter (Gender, Brand, Category)
```
1. Ở trang shop, thử filter:
   - Theo giới tính (Nam/Nữ/Unisex)
   - Theo thương hiệu (Nike, Adidas, ...)
   - Theo loại giày (Sneaker, Running, ...)
2. KẾT QUẢ:
   ✅ Tất cả đều CHỈ hiển thị sản phẩm active
   ✅ Sản phẩm đã ẩn KHÔNG xuất hiện trong filter
```

### Test 4: Kiểm tra tìm kiếm (Search)
```
1. Ở trang shop, tìm kiếm tên sản phẩm đã xóa
2. VD: Tìm "Nike Air Max" (đã xóa)
3. KẾT QUẢ:
   ✅ KHÔNG tìm thấy (nếu status=false)
   ✅ Chỉ tìm thấy sản phẩm active
```

### Test 5: Kiểm tra bằng API trực tiếp
Mở Postman hoặc browser console:

```javascript
// Test API customer (CHỈ active shoes)
fetch('http://localhost:8080/shoes')
  .then(r => r.json())
  .then(data => {
    console.log('Customer API:', data.result);
    console.log('Có sản phẩm status=false?', 
      data.result.some(shoe => shoe.status === false)
    ); // ✅ Phải là false
  });

// Test API admin (TẤT CẢ shoes)
fetch('http://localhost:8080/shoes/admin/all', {
  headers: {
    'Authorization': 'Bearer <admin-token>'
  }
})
  .then(r => r.json())
  .then(data => {
    console.log('Admin API:', data.result);
    console.log('Có sản phẩm status=false?', 
      data.result.some(shoe => shoe.status === false)
    ); // ✅ Có thể là true (nếu có shoes đã ẩn)
  });
```

---

## 🔧 Cập nhật Frontend (Nếu cần)

### Nếu Admin page cần hiển thị cả shoes đã ẩn:

**File**: `front-end/src/pages/admin-pages/ListManageShoePage.jsx` (hoặc tương tự)

```javascript
// Thay đổi API call
const fetchAllShoes = async () => {
  try {
    // ✅ Dùng endpoint admin để xem tất cả
    const response = await api.get('/shoes/admin/all');
    setShoes(response.data.result);
  } catch (error) {
    console.error('Error fetching shoes:', error);
  }
};

// Hiển thị với badge status
{shoes.map(shoe => (
  <div key={shoe.id}>
    <h3>{shoe.name}</h3>
    {shoe.status === false && (
      <span className="badge badge-danger">Đã ẩn</span>
    )}
    <button onClick={() => deleteShoe(shoe.id)}>
      {shoe.status ? 'Ẩn' : 'Khôi phục'}
    </button>
  </div>
))}
```

### Optional: Thêm chức năng "Khôi phục" sản phẩm đã ẩn

Nếu muốn admin có thể khôi phục sản phẩm (undelete):

**Backend** - Thêm endpoint:
```java
@PutMapping("/{id}/restore")
@PreAuthorize("hasRole('ADMIN')")
public APIResponse<Void> restoreShoe(@PathVariable("id") int id) {
    Shoe shoe = shoeRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    shoe.setStatus(true); // Khôi phục
    shoe.setUpdatedAt(Instant.now());
    shoeRepository.save(shoe);
    return APIResponse.<Void>builder()
            .flag(true)
            .message("Shoe restored successfully")
            .build();
}
```

---

## 📝 File đã sửa

1. ✅ `ShoeRepository.java` - Thêm 6 query methods filter theo status
2. ✅ `ShoeService.java` - Tách logic customer vs admin, thêm `getAllShoesForAdmin()`
3. ✅ `ShoeController.java` - Thêm endpoint `/shoes/admin/all`

---

## 🎯 Tóm tắt các thay đổi

| Endpoint | Trước đây | Sau khi fix |
|----------|-----------|-------------|
| `GET /shoes` | Trả về TẤT CẢ (kể cả deleted) ❌ | Trả về CHỈ active shoes ✅ |
| `GET /shoes/by-gender` | Trả về TẤT CẢ ❌ | Trả về CHỈ active ✅ |
| `GET /shoes/by-brand` | Trả về TẤT CẢ ❌ | Trả về CHỈ active ✅ |
| `GET /shoes/by-category` | Trả về TẤT CẢ ❌ | Trả về CHỈ active ✅ |
| `GET /shoes/search?name=` | Trả về TẤT CẢ ❌ | Trả về CHỈ active ✅ |
| `GET /shoes/admin/all` | Không có ❌ | Trả về TẤT CẢ cho admin ✅ |

---

## ⚠️ LƯU Ý

### Về Soft Delete
**Tại sao dùng Soft Delete thay vì Hard Delete?**

✅ **Ưu điểm**:
- Giữ lại dữ liệu lịch sử (orders cũ vẫn reference đến shoe)
- Có thể khôi phục nếu xóa nhầm
- Audit trail (biết ai xóa, xóa khi nào)
- Không làm hỏng referential integrity (foreign keys)

❌ **Nhược điểm**:
- Database tích lũy dữ liệu "rác"
- Phải luôn nhớ filter `status=true` ở mọi query
- Performance có thể chậm hơn (nhiều data hơn)

### Nếu muốn Hard Delete (xóa hẳn)

Sửa `ShoeService.java`:
```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteShoe(int id) {
    Shoe shoe = shoeRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    
    // Hard delete: XÓA HẲN khỏi database
    shoeRepository.delete(shoe); // ← Thay đổi ở đây
    
    // Cũng xóa luôn variants
    List<ShoeVariant> variants = shoeVariantRepository.findShoeVariantByShoeId(id);
    shoeVariantRepository.deleteAll(variants);
}
```

⚠️ **Cảnh báo**: Hard delete có thể làm hỏng orders cũ nếu chúng reference đến shoe đã xóa!

---

## 🔄 Workflow sau khi fix

```
                    ┌─────────────────┐
                    │   Customer      │
                    │  Vào trang shop │
                    └────────┬────────┘
                             │
                             ▼
                    GET /shoes (public)
                             │
                             ▼
              ┌──────────────────────────┐
              │ ShoeService.getAllShoes()│
              │  findByStatusTrue()      │ ← CHỈ lấy active
              └──────────────────────────┘
                             │
                             ▼
                    [Shoe1✅, Shoe2✅]
                    (Không có Shoe3❌)


                    ┌─────────────────┐
                    │   Admin         │
                    │  Quản lý shoes  │
                    └────────┬────────┘
                             │
                             ▼
              GET /shoes/admin/all (protected)
                             │
                             ▼
          ┌─────────────────────────────────┐
          │ ShoeService.getAllShoesForAdmin()│
          │  findAll()                      │ ← Lấy TẤT CẢ
          └─────────────────────────────────┘
                             │
                             ▼
            [Shoe1✅, Shoe2✅, Shoe3❌]
            (Có cả shoes đã ẩn)
```

---

## ✅ Checklist

Sau khi restart backend, kiểm tra:

- [ ] Backend build thành công không có lỗi ✅
- [ ] API `/shoes` chỉ trả về shoes active ✅
- [ ] API `/shoes/admin/all` trả về tất cả shoes (cần auth) ✅
- [ ] Xóa shoe ở admin → biến mất khỏi trang shop ✅
- [ ] Filter (gender, brand, category) chỉ hiện active ✅
- [ ] Search chỉ tìm được active shoes ✅
- [ ] Admin vẫn thấy shoes đã ẩn (nếu dùng endpoint mới) ✅

---

## 🚀 Deploy

Sau khi test xong:

```bash
# 1. Commit changes
git add .
git commit -m "Fix: Filter deleted shoes from customer view"

# 2. Restart backend
cd back-end
./mvnw clean install
./mvnw spring-boot:run

# 3. Test lại toàn bộ
```

---

**Chúc bạn fix thành công! 🎉**

Nếu còn vấn đề gì, hãy check:
1. Console logs backend
2. API response trong Network tab
3. Database records (query `SELECT * FROM shoe WHERE status = false`)
