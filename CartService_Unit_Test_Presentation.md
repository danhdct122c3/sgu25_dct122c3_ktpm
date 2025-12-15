# Unit Testing

## Class : CartService

### Tính năng sẽ test: addToCart
**ID:** TC-01  
**Hàm:** CartResponse addToCart(String username, AddToCartRequest reqbauest)  
**Mục đích:** Kiểm tra chức năng thêm sản phẩm vào giỏ hàng, bao gồm thêm mới, cập nhật số lượng và xử lý vượt tồn kho  

#### Các trường hợp test :
| Tên hàm test | Mô tả | Dữ liệu nhập | Kết quả mong đợi | Kết quả chạy | Failed/ Pass |
|--------------|--------|--------------|------------------|--------------|-------------|
| addToCart_userNotFound_shouldThrowUserNotExisted | Ném exception khi user không tồn tại | username="unknown_user" | AppException với ErrorCode.USER_NOT_EXISTED |  |  |
| addToCart_variantNotFound_shouldThrowVariantNotFound | Ném exception khi variant không tồn tại | username="unit_test_user", variantId="non_existent_variant" | AppException với ErrorCode.VARIANT_NOT_FOUND |  |  |
| addToCart_newItem_shouldSaveCartItem | Thêm sản phẩm mới vào giỏ hàng | username="unit_test_user", variantId="variant-001", quantity=2 | CartItem được lưu với quantity=2 |  |  |
| addToCart_exceedStock_shouldThrowAppException | Ném exception khi thêm mới với số lượng vượt tồn kho | username="unit_test_user", variantId="variant-001", quantity=15 (tồn kho=10) | AppException với ErrorCode.OUT_OF_STOCK |  |  |
| addToCart_existingItem_shouldUpdateQuantity | Cập nhật số lượng khi thêm sản phẩm đã tồn tại trong giỏ | username="unit_test_user", variantId="variant-001", quantity=3 (cộng với 2 hiện có) | quantity=5 |  |  |
| addToCart_existingItemExceedStock_shouldThrowOutOfStock | Ném exception khi cập nhật số lượng vượt tồn kho | username="unit_test_user", variantId="variant-001", quantity=9 (cộng với 2 hiện có =11 >10) | AppException với ErrorCode.OUT_OF_STOCK |  |  |

### Tính năng sẽ test: getCart
**ID:** TC-02  
**Hàm:** CartResponse getCart(String username)  
**Mục đích:** Kiểm tra chức năng lấy thông tin giỏ hàng của user  

#### Các trường hợp test :
| Tên hàm test | Mô tả | Dữ liệu nhập | Kết quả mong đợi | Kết quả chạy | Failed/ Pass |
|--------------|--------|--------------|------------------|--------------|-------------|
| getCart_userNotFound_shouldThrowUserNotExisted | Ném exception khi user không tồn tại | username="unknown" | AppException với ErrorCode.USER_NOT_EXISTED |  |  |

### Tính năng sẽ test: updateCartItem
**ID:** TC-03  
**Hàm:** CartResponse updateCartItem(String username, UpdateCartItemRequest request)  
**Mục đích:** Kiểm tra chức năng cập nhật số lượng item trong giỏ hàng  

#### Các trường hợp test :
| Tên hàm test | Mô tả | Dữ liệu nhập | Kết quả mong đợi | Kết quả chạy | Failed/ Pass |
|--------------|--------|--------------|------------------|--------------|-------------|
| updateCartItem_validQuantity_shouldUpdateSuccess | Cập nhật số lượng item thành công | username="unit_test_user", variantId="variant-001", quantity=5 | quantity=5 |  |  |

### Tính năng sẽ test: removeFromCart
**ID:** TC-04  
**Hàm:** CartResponse removeFromCart(String username, String variantId)  
**Mục đích:** Kiểm tra chức năng xóa sản phẩm khỏi giỏ hàng  

#### Các trường hợp test :
| Tên hàm test | Mô tả | Dữ liệu nhập | Kết quả mong đợi | Kết quả chạy | Failed/ Pass |
|--------------|--------|--------------|------------------|--------------|-------------|
| removeFromCart_itemExists_shouldDelete | Xóa item tồn tại khỏi giỏ hàng | username="unit_test_user", variantId="variant-001" | CartItem bị xóa khỏi giỏ |  |  |

### Tính năng sẽ test: clearCart
**ID:** TC-05  
**Hàm:** void clearCart(String username)  
**Mục đích:** Kiểm tra chức năng xóa toàn bộ giỏ hàng  

#### Các trường hợp test :
| Tên hàm test | Mô tả | Dữ liệu nhập | Kết quả mong đợi | Kết quả chạy | Failed/ Pass |
|--------------|--------|--------------|------------------|--------------|-------------|
| (Chưa có test case) |  |  |  |  |  |

**Ghi chú:** File unit test hiện tại chưa bao gồm test case cho phương thức clearCart và một số trường hợp edge case khác như getCart thành công, updateCartItem với số lượng vượt tồn kho, removeFromCart với item không tồn tại, v.v.