# 📝 Hướng dẫn đặt tên test để có báo cáo đẹp với dữ liệu nhập thực

## Vấn đề hiện tại

Với convention hiện tại: `addToCart_userNotFound_shouldThrowUserNotExisted`

Báo cáo sẽ hiển thị:
- **Dữ liệu nhập**: `userNotFound` ❌ (không phải dữ liệu thực)
- **Kết quả mong đợi**: `ThrowUserNotExisted`

## Giải pháp 1: Thêm dữ liệu vào tên test (Đơn giản nhất)

### Format:
```
methodName_inputData_scenario_shouldExpectedResult
```

### Ví dụ:

```java
@Test
void addToCart_username123VariantV001Qty2_newItem_shouldSaveCartItem() {
    // username = "unit_test_user"
    // variantId = "variant-001"  
    // quantity = 2
    // Test thêm item mới vào giỏ
}

@Test
void addToCart_usernameTestVariantV001Qty999_exceedStock_shouldThrowOutOfStock() {
    // username = "unit_test_user"
    // variantId = "variant-001"
    // quantity = 999 (vượt quá stock = 10)
}

@Test
void login_usernameAdminPasswordWrong_invalidPassword_shouldThrowUnauthenticated() {
    // username = "admin"
    // password = "wrong_password"
}
```

**Kết quả báo cáo:**
| Tên hàm test | Mô tả | Dữ liệu nhập | Kết quả mong đợi |
|--------------|-------|--------------|------------------|
| addToCart_username123VariantV001Qty2_newItem_shouldSaveCartItem | New item | username='123', variant='v001', qty='2' | Lưu CartItem |

## Giải pháp 2: Sử dụng @DisplayName (Linh hoạt nhất)

### Format:
```java
@DisplayName("Mô tả ngắn gọn | Dữ liệu nhập | Kết quả mong đợi")
@Test
void tenTestNormalTheoConvention() { ... }
```

### Ví dụ cụ thể:

```java
package fpl.sd.backend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
public class CartServiceUnitTest {

    @DisplayName("Thêm item mới vào giỏ | username='unit_test_user', variantId='variant-001', quantity=2 | Lưu CartItem mới")
    @Test
    void addToCart_newItem_shouldSaveCartItem() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId("variant-001");
        request.setQuantity(2);
        
        when(userRepository.findByUsername("unit_test_user")).thenReturn(Optional.of(user));
        // ...rest of test
    }

    @DisplayName("Thêm item đã tồn tại | username='unit_test_user', variantId='variant-001', oldQty=2, addQty=3 | Cập nhật quantity=5")
    @Test
    void addToCart_existingItem_shouldUpdateQuantity() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId("variant-001");
        request.setQuantity(3);
        // cartItem hiện tại có quantity = 2
        // ...rest of test
    }

    @DisplayName("User không tồn tại | username='unknown_user' | Ném AppException với USER_NOT_EXISTED")
    @Test
    void addToCart_userNotFound_shouldThrowUserNotExisted() {
        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());
        // ...rest of test
    }

    @DisplayName("Vượt quá stock | username='unit_test_user', variantId='variant-001', quantity=999, stock=10 | Ném AppException với OUT_OF_STOCK")
    @Test
    void addToCart_exceedStock_shouldThrowAppException() {
        // ...test code
    }
}
```

### Ví dụ cho AuthenticationService:

```java
@DisplayName("Login thành công | username='testuser', password='correct123' | Trả về AuthenticationResponse với token")
@Test
void login_withValidCredentials_shouldReturnToken() {
    // ...
}

@DisplayName("Login với password sai | username='testuser', password='wrong_password' | Ném AppException với UNAUTHENTICATED")
@Test
void login_withInvalidPassword_shouldThrowUnauthenticated() {
    // ...
}

@DisplayName("Logout với token hợp lệ | token='valid_jwt_token' | Lưu token vào InvalidatedToken table")
@Test
void logout_withValidToken_shouldSaveInvalidatedToken() {
    // ...
}
```

## Giải pháp 3: Viết comment đầu test (Đơn giản nhưng ít hiệu quả)

```java
@Test
void addToCart_newItem_shouldSaveCartItem() {
    /**
     * Dữ liệu nhập:
     * - username: "unit_test_user"
     * - variantId: "variant-001"
     * - quantity: 2
     * 
     * Kết quả mong đợi: Lưu CartItem mới vào database
     */
    // Test code...
}
```

❌ **Nhược điểm**: Script Python không thể đọc comment từ source code, chỉ đọc từ XML report.

## So sánh các giải pháp

| Giải pháp | Ưu điểm | Nhược điểm | Khuyên dùng |
|-----------|---------|------------|-------------|
| Tên test chi tiết | - Không cần annotation<br>- Script tự động parse | - Tên test dài<br>- Khó đọc trong code | ⭐⭐⭐ |
| @DisplayName | - Rất linh hoạt<br>- Dễ đọc trong code<br>- Có thể viết tiếng Việt | - Cần thêm annotation | ⭐⭐⭐⭐⭐ |
| Comment | - Đơn giản | - Script không đọc được | ⭐ |

## Khuyến nghị

### 🏆 Cách tốt nhất: Kết hợp cả 2

```java
@DisplayName("Thêm item mới | username='testuser', variantId='v001', qty=2 | Lưu CartItem")
@Test
void addToCart_newItem_shouldSaveCartItem() {
    // Arrange
    String username = "testuser";
    String variantId = "v001";
    int quantity = 2;
    
    // Test implementation...
}
```

**Lợi ích:**
- ✅ Báo cáo CI/CD đẹp và chi tiết
- ✅ Dễ đọc trong IDE khi chạy test
- ✅ Tên biến trong code khớp với mô tả
- ✅ Dễ maintain và review code

## Script đã được cải thiện

Script Python hiện tại (`generate_test_report.py`) đã có thể:

1. ✅ Parse các pattern phổ biến trong tên test:
   - `userXxx` → user='xxx'
   - `variantXxx` → variant='xxx'
   - `qtyN` → qty='N'
   - `passwordXxx` → password='xxx'

2. ✅ Parse các scenario thông dụng:
   - `notFound` → 'không tồn tại'
   - `invalid` → 'invalid'
   - `valid` → 'valid'
   - `exceed` → 'vượt quá stock'

3. ✅ Format kết quả mong đợi:
   - `shouldSave` → "Lưu"
   - `shouldThrow` → "Ném exception"
   - `shouldUpdate` → "Cập nhật"

## Test script ngay

```bash
cd back-end
mvn clean test -Dtest=*UnitTest
cd ..
python generate_test_report.py back-end/target/surefire-reports
```

Bạn sẽ thấy output đẹp hơn với dữ liệu được parse tự động! 🎉

