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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
                .shoeImages(new ArrayList<>())
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

    @DisplayName("addToCart_newItem_shouldSaveCartItem | Thêm item mới vào giỏ | username='unit_test_user', variantId='variant-001', quantity=2, stock=10 | Lưu CartItem mới với quantity=2")
    @Test
    void addToCart_newItem_shouldSaveCartItem() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(2);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(shoeVariantRepository.findById(VARIANT_ID)).thenReturn(Optional.of(shoeVariant));
        when(cartItemRepository.findByCartIdAndVariantId(cart.getId(), VARIANT_ID)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        CartResponse response = cartService.addToCart(TEST_USERNAME, request);

        // Capture saved item
        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository, times(1)).save(cartItemCaptor.capture());
        CartItem savedItem = cartItemCaptor.getValue();

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Saved new CartItem. Quantity: " + savedItem.getQuantity() + ", Total Price: " + response.getTotalPrice());

        // Assert
        assertThat(response).isNotNull();
        assertThat(savedItem.getVariant().getId()).isEqualTo(VARIANT_ID);
        assertThat(savedItem.getQuantity()).isEqualTo(2);
    }

    @DisplayName("addToCart_existingItem_shouldUpdateQuantity | Thêm item đã tồn tại trong giỏ | username='unit_test_user', variantId='variant-001', oldQty=2, addQty=3, stock=10 | Cập nhật quantity từ 2 lên 5")
    @Test
    void addToCart_existingItem_shouldUpdateQuantity() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(3); // Thêm 3 cái nữa
        cart.addItem(cartItem); // Trong giỏ đã có 2

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(shoeVariantRepository.findById(VARIANT_ID)).thenReturn(Optional.of(shoeVariant));
        when(cartItemRepository.findByCartIdAndVariantId(cart.getId(), VARIANT_ID)).thenReturn(Optional.of(cartItem));

        // Act
        CartResponse response = cartService.addToCart(TEST_USERNAME, request);

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Updated existing item. New Quantity: " + cartItem.getQuantity());

        // Assert
        assertThat(cartItem.getQuantity()).isEqualTo(5);
        verify(cartItemRepository, times(1)).save(cartItem);
    }

    @DisplayName("addToCart_exceedStock_shouldThrowAppException | Thêm vào giỏ vượt quá stock | username='unit_test_user', variantId='variant-001', quantity=15, stock=10 | Ném AppException với ErrorCode.OUT_OF_STOCK")
    @Test
    void addToCart_exceedStock_shouldThrowAppException() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(15);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(shoeVariantRepository.findById(VARIANT_ID)).thenReturn(Optional.of(shoeVariant));
        when(cartItemRepository.findByCartIdAndVariantId(anyString(), anyString())).thenReturn(Optional.empty());

        // Act & Capture Exception
        AppException exception = assertThrows(AppException.class, () -> {
            cartService.addToCart(TEST_USERNAME, request);
        });

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Exception: " + exception.getErrorCode().getMessage() + " (" + exception.getErrorCode() + ")");

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.OUT_OF_STOCK);
        verify(cartItemRepository, never()).save(any());
    }

    @DisplayName("addToCart_userNotFound_shouldThrowUserNotExisted | Thêm vào giỏ khi user không tồn tại | username='unknown_user' | Ném AppException với ErrorCode.USER_NOT_EXISTED")
    @Test
    void addToCart_userNotFound_shouldThrowUserNotExisted() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(2);

        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());

        // Act & Capture Exception
        AppException exception = assertThrows(AppException.class, () -> {
            cartService.addToCart("unknown_user", request);
        });

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Exception: " + exception.getErrorCode().getMessage() + " (" + exception.getErrorCode() + ")");

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);
    }

    @DisplayName("addToCart_variantNotFound_shouldThrowVariantNotFound | Thêm vào giỏ khi variant không tồn tại | username='unit_test_user', variantId='non_existent_variant', quantity=2 | Ném AppException với ErrorCode.VARIANT_NOT_FOUND")
    @Test
    void addToCart_variantNotFound_shouldThrowVariantNotFound() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId("non_existent_variant");
        request.setQuantity(2);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(shoeVariantRepository.findById("non_existent_variant")).thenReturn(Optional.empty());

        // Act & Capture Exception
        AppException exception = assertThrows(AppException.class, () -> {
            cartService.addToCart(TEST_USERNAME, request);
        });

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Exception: " + exception.getErrorCode().getMessage() + " (" + exception.getErrorCode() + ")");

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.VARIANT_NOT_FOUND);
    }

    @DisplayName("addToCart_existingItemExceedStock_shouldThrowOutOfStock | Thêm item đã tồn tại vượt stock | username='unit_test_user', variantId='variant-001', oldQty=2, addQty=9, stock=10 | Ném AppException với ErrorCode.OUT_OF_STOCK")
    @Test
    void addToCart_existingItemExceedStock_shouldThrowOutOfStock() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(9); // 2 + 9 = 11 > 10

        cart.addItem(cartItem);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(shoeVariantRepository.findById(VARIANT_ID)).thenReturn(Optional.of(shoeVariant));
        when(cartItemRepository.findByCartIdAndVariantId(cart.getId(), VARIANT_ID)).thenReturn(Optional.of(cartItem));

        // Act & Capture Exception
        AppException exception = assertThrows(AppException.class, () -> {
            cartService.addToCart(TEST_USERNAME, request);
        });

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Exception: " + exception.getErrorCode().getMessage() + " (" + exception.getErrorCode() + ")");

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.OUT_OF_STOCK);
        assertThat(cartItem.getQuantity()).isEqualTo(2); // Không đổi
    }

    @DisplayName("updateCartItem_validQuantity_shouldUpdateSuccess | Cập nhật số lượng item hợp lệ | username='unit_test_user', variantId='variant-001', oldQty=2, newQty=5, stock=10 | Cập nhật thành công quantity=5")
    @Test
    void updateCartItem_validQuantity_shouldUpdateSuccess() {
        // Arrange
        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(5);
        cart.addItem(cartItem);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndVariantId(cart.getId(), VARIANT_ID)).thenReturn(Optional.of(cartItem));

        // Act
        CartResponse response = cartService.updateCartItem(TEST_USERNAME, request);

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Update Success. New Quantity: " + cartItem.getQuantity());

        // Assert
        assertThat(cartItem.getQuantity()).isEqualTo(5);
        verify(cartItemRepository).save(cartItem);
    }

    @DisplayName("removeFromCart_itemExists_shouldDelete | Xóa item khỏi giỏ hàng | username='unit_test_user', variantId='variant-001' | Xóa CartItem thành công khỏi database và cart.items")
    @Test
    void removeFromCart_itemExists_shouldDelete() {
        // Arrange
        cart.addItem(cartItem);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndVariantId(cart.getId(), VARIANT_ID)).thenReturn(Optional.of(cartItem));

        // Act
        CartResponse response = cartService.removeFromCart(TEST_USERNAME, VARIANT_ID);

        // ==> IN KẾT QUẢ THỰC TẾ
        // Vì item bị xóa nên total quantity trong cart response sẽ giảm
        System.out.println("[ACTUAL] Item removed. Remaining items in cart: " + response.getTotalQuantity());

        // Assert
        verify(cartItemRepository, times(1)).delete(cartItem);
        assertThat(cart.getItems()).doesNotContain(cartItem);
    }

    @DisplayName("getCart_userNotFound_shouldThrowUserNotExisted | Lấy giỏ hàng khi user không tồn tại | username='unknown' | Ném AppException với ErrorCode.USER_NOT_EXISTED")
    @Test
    void getCart_userNotFound_shouldThrowUserNotExisted() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Capture Exception
        AppException exception = assertThrows(AppException.class, () -> {
            cartService.getCart("unknown");
        });

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Exception: " + exception.getErrorCode().getMessage() + " (" + exception.getErrorCode() + ")");

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);
    }
}