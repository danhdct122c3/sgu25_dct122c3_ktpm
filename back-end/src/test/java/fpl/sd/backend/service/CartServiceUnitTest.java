package fpl.sd.backend.service;

import fpl.sd.backend.dto.request.AddToCartRequest;
import fpl.sd.backend.dto.request.UpdateCartItemRequest;
import fpl.sd.backend.dto.response.CartResponse;
import fpl.sd.backend.entity.*;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.repository.CartItemRepository;
import fpl.sd.backend.repository.CartRepository;
import fpl.sd.backend.repository.ShoeVariantRepository;
import fpl.sd.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceUnitTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoeVariantRepository shoeVariantRepository;

    @InjectMocks
    private CartService cartService;

    // Dummy Data
    private User user;
    private Cart cart;
    private ShoeVariant shoeVariant;
    private Shoe shoe;
    private CartItem cartItem;
    private final String TEST_USERNAME = "unit_test_user";
    private final String VARIANT_ID = "variant-001";

    @BeforeEach
    void setUp() {
        // 1. Setup User
        user = User.builder()
                .id("user-123")
                .username(TEST_USERNAME)
                .build();

        // 2. Setup Shoe & Variant (Product info)
        shoe = Shoe.builder()
                .id(1)
                .name("Nike Air Jordan")
                .price(200.0)
                .shoeImages(new ArrayList<>()) // Tránh NullPointerException khi map response
                .build();

        SizeChart size = SizeChart.builder().sizeNumber(42).build();

        shoeVariant = ShoeVariant.builder()
                .id(VARIANT_ID)
                .shoe(shoe)
                .sizeChart(size)
                .stockQuantity(10) // Tồn kho mặc định là 10
                .build();

        // 3. Setup Cart
        cart = Cart.builder()
                .id("cart-001")
                .user(user)
                .items(new ArrayList<>()) // List rỗng ban đầu
                .build();

        // 4. Setup CartItem (dùng cho các case update/remove)
        cartItem = CartItem.builder()
                .id(String.valueOf(100L))
                .cart(cart)
                .variant(shoeVariant)
                .quantity(2)
                .build();
    }

    @DisplayName("Thêm item mới vào giỏ | username='unit_test_user', variantId='variant-001', quantity=2, stock=10 | Lưu CartItem mới với quantity=2")
    @Test
    void addToCart_newItem_shouldSaveCartItem() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(2);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(shoeVariantRepository.findById(VARIANT_ID)).thenReturn(Optional.of(shoeVariant));
        // Giả lập chưa có item này trong giỏ
        when(cartItemRepository.findByCartIdAndVariantId(cart.getId(), VARIANT_ID)).thenReturn(Optional.empty());

        // Mock save để trả về chính item đó (cho luồng chạy tiếp không lỗi)
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        CartResponse response = cartService.addToCart(TEST_USERNAME, request);

        // Assert
        assertThat(response).isNotNull();

        // Capture lại đối tượng được save để kiểm tra kỹ hơn
        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository, times(1)).save(cartItemCaptor.capture());

        CartItem savedItem = cartItemCaptor.getValue();
        assertThat(savedItem.getVariant().getId()).isEqualTo(VARIANT_ID);
        assertThat(savedItem.getQuantity()).isEqualTo(2);
    }

    @DisplayName("Thêm item đã tồn tại trong giỏ | username='unit_test_user', variantId='variant-001', oldQty=2, addQty=3, stock=10 | Cập nhật quantity từ 2 lên 5")
    @Test
    void addToCart_existingItem_shouldUpdateQuantity() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(3); // Thêm 3 cái nữa

        // Add sẵn 1 item vào giỏ ảo để tính toán total price không bị lỗi
        cart.addItem(cartItem);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(shoeVariantRepository.findById(VARIANT_ID)).thenReturn(Optional.of(shoeVariant));
        // Giả lập ĐÃ có item trong giỏ (đang có số lượng 2)
        when(cartItemRepository.findByCartIdAndVariantId(cart.getId(), VARIANT_ID)).thenReturn(Optional.of(cartItem));

        // Act
        cartService.addToCart(TEST_USERNAME, request);

        // Assert
        // Kiểm tra quantity: 2 (cũ) + 3 (mới) = 5
        assertThat(cartItem.getQuantity()).isEqualTo(5);
        verify(cartItemRepository, times(1)).save(cartItem);
    }

    @DisplayName("Thêm vào giỏ vượt quá stock | username='unit_test_user', variantId='variant-001', quantity=15, stock=10 | Ném AppException với ErrorCode.OUT_OF_STOCK")
    @Test
    void addToCart_exceedStock_shouldThrowAppException() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(15); // Mua 15 > Tồn kho 10

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(shoeVariantRepository.findById(VARIANT_ID)).thenReturn(Optional.of(shoeVariant));
        when(cartItemRepository.findByCartIdAndVariantId(anyString(), anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.addToCart(TEST_USERNAME, request))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.OUT_OF_STOCK);

        // Đảm bảo không có lệnh save nào được gọi
        verify(cartItemRepository, never()).save(any());
    }

    @DisplayName("Thêm vào giỏ khi user không tồn tại | username='unknown_user' | Ném AppException với ErrorCode.USER_NOT_EXISTED")
    @Test
    void addToCart_userNotFound_shouldThrowUserNotExisted() {
        // Arrange - Đường 1: User không tồn tại
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(2);

        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.addToCart("unknown_user", request))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_EXISTED);

        // Đảm bảo không gọi các repository khác
        verify(cartRepository, never()).findByUserId(anyString());
        verify(shoeVariantRepository, never()).findById(anyString());
        verify(cartItemRepository, never()).save(any());
    }

    @DisplayName("Thêm vào giỏ khi variant không tồn tại | username='unit_test_user', variantId='non_existent_variant', quantity=2 | Ném AppException với ErrorCode.VARIANT_NOT_FOUND")
    @Test
    void addToCart_variantNotFound_shouldThrowVariantNotFound() {
        // Arrange - Đường 2: Variant không tồn tại
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId("non_existent_variant");
        request.setQuantity(2);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(shoeVariantRepository.findById("non_existent_variant")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.addToCart(TEST_USERNAME, request))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.VARIANT_NOT_FOUND);

        // Đảm bảo không có lệnh save nào được gọi
        verify(cartItemRepository, never()).findByCartIdAndVariantId(anyString(), anyString());
        verify(cartItemRepository, never()).save(any());
    }

    @DisplayName("Thêm item đã tồn tại vượt stock | username='unit_test_user', variantId='variant-001', oldQty=2, addQty=9, stock=10 | Ném AppException với ErrorCode.OUT_OF_STOCK")
    @Test
    void addToCart_existingItemExceedStock_shouldThrowOutOfStock() {
        // Arrange - Đường 6: Item đã tồn tại trong giỏ, cộng thêm vượt quá stock
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(9); // Item hiện có 2, thêm 9 = 11 > stock (10)

        cart.addItem(cartItem); // Giỏ đang có item với quantity = 2

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(shoeVariantRepository.findById(VARIANT_ID)).thenReturn(Optional.of(shoeVariant));
        when(cartItemRepository.findByCartIdAndVariantId(cart.getId(), VARIANT_ID)).thenReturn(Optional.of(cartItem));

        // Act & Assert
        assertThatThrownBy(() -> cartService.addToCart(TEST_USERNAME, request))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.OUT_OF_STOCK);

        // Đảm bảo quantity không thay đổi và không có lệnh save
        assertThat(cartItem.getQuantity()).isEqualTo(2); // Vẫn giữ nguyên quantity cũ
        verify(cartItemRepository, never()).save(any());
    }

    @DisplayName("Cập nhật số lượng item hợp lệ | username='unit_test_user', variantId='variant-001', oldQty=2, newQty=5, stock=10 | Cập nhật thành công quantity=5")
    @Test
    void updateCartItem_validQuantity_shouldUpdateSuccess() {
        // Arrange
        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(5);

        cart.addItem(cartItem); // Giỏ hàng đang chứa item

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndVariantId(cart.getId(), VARIANT_ID)).thenReturn(Optional.of(cartItem));

        // Act
        cartService.updateCartItem(TEST_USERNAME, request);

        // Assert
        assertThat(cartItem.getQuantity()).isEqualTo(5);
        verify(cartItemRepository).save(cartItem);
    }

    @DisplayName("Xóa item khỏi giỏ hàng | username='unit_test_user', variantId='variant-001' | Xóa CartItem thành công khỏi database và cart.items")
    @Test
    void removeFromCart_itemExists_shouldDelete() {
        // Arrange
        cart.addItem(cartItem);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndVariantId(cart.getId(), VARIANT_ID)).thenReturn(Optional.of(cartItem));

        // Act
        cartService.removeFromCart(TEST_USERNAME, VARIANT_ID);

        // Assert
        verify(cartItemRepository, times(1)).delete(cartItem);
        assertThat(cart.getItems()).doesNotContain(cartItem); // Item phải bị xóa khỏi list trong object cart
    }

    @DisplayName("Lấy giỏ hàng khi user không tồn tại | username='unknown' | Ném AppException với ErrorCode.USER_NOT_EXISTED")
    @Test
    void getCart_userNotFound_shouldThrowUserNotExisted() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.getCart("unknown"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_EXISTED);
    }
}