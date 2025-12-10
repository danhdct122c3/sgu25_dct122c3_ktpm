# Unit Test Specification for ShoeShop Store

## Part 1 — Unit Test Design

| Test ID | Layer | Module/Class | Method/Function | Scenario | Input/Test Data | Expected Output/Behavior |
|---------|-------|--------------|-----------------|----------|-----------------|--------------------------|
| UT_BE_AUTH_001 | BE | AuthenticationService | authenticateUser | Valid credentials | username: "customer@example.com", password: "password123" | Returns User object with correct role |
| UT_BE_AUTH_002 | BE | AuthenticationService | authenticateUser | Invalid password | username: "customer@example.com", password: "wrongpass" | Throws AuthenticationException |
| UT_BE_AUTH_003 | BE | AuthenticationService | authenticateUser | User not found | username: "nonexistent@example.com", password: "password123" | Throws UserNotFoundException |
| UT_BE_AUTH_004 | BE | AuthenticationService | generateToken | Valid user | User object with role "CUSTOMER" | Returns JWT token string |
| UT_BE_AUTH_005 | BE | AuthenticationService | validateToken | Valid token | JWT token for customer | Returns true |
| UT_BE_AUTH_006 | BE | AuthenticationService | validateToken | Expired token | Expired JWT token | Returns false |
| UT_BE_AUTH_007 | BE | UserService | getUserByUsername | Existing user | username: "customer@example.com" | Returns User entity |
| UT_BE_AUTH_008 | BE | UserService | getUserByUsername | Non-existing user | username: "nonexistent@example.com" | Returns null |
| UT_BE_CATALOG_001 | BE | ProductService | getProducts | No filters | page: 0, size: 10 | Returns Page<Product> with 10 items |
| UT_BE_CATALOG_002 | BE | ProductService | getProducts | With brand filter | brandId: 1, page: 0, size: 10 | Returns products only from brand 1 |
| UT_BE_CATALOG_003 | BE | ProductService | getProducts | With price range | minPrice: 50, maxPrice: 200 | Returns products within price range |
| UT_BE_CATALOG_004 | BE | ProductService | getProductById | Existing product | productId: 1 | Returns Product entity |
| UT_BE_CATALOG_005 | BE | ProductService | getProductById | Non-existing product | productId: 999 | Throws ProductNotFoundException |
| UT_BE_CATALOG_006 | BE | ProductService | searchProducts | Valid search term | query: "running shoes" | Returns matching products |
| UT_BE_CATALOG_007 | BE | ProductService | getProductsSorted | Sort by price ascending | sortBy: "price", direction: "asc" | Returns products sorted by price low to high |
| UT_BE_CART_001 | BE | CartService | calculateTotal | Cart with items | Cart with 2 items: $50 and $30 | Returns 80.0 |
| UT_BE_CART_002 | BE | CartService | calculateTotal | Empty cart | Empty cart | Returns 0.0 |
| UT_BE_CART_003 | BE | CartService | applyDiscount | Valid discount code | total: 100.0, discountCode: "SAVE10" | Returns 90.0 |
| UT_BE_CART_004 | BE | CartService | applyDiscount | Invalid discount code | total: 100.0, discountCode: "INVALID" | Throws InvalidDiscountException |
| UT_BE_CART_005 | BE | CartService | validateStock | Sufficient stock | productId: 1, requestedQuantity: 2, availableStock: 5 | Returns true |
| UT_BE_CART_006 | BE | CartService | validateStock | Insufficient stock | productId: 1, requestedQuantity: 10, availableStock: 5 | Returns false |
| UT_BE_CHECKOUT_001 | BE | CheckoutService | createOrder | Valid cart and user | Cart with items, User object | Returns Order with status CREATED |
| UT_BE_CHECKOUT_002 | BE | CheckoutService | createOrder | Empty cart | Empty cart, User object | Throws InvalidOrderException |
| UT_BE_CHECKOUT_003 | BE | CheckoutService | processPayment | COD payment | Order object, paymentMethod: "COD" | Order status remains CREATED |
| UT_BE_CHECKOUT_004 | BE | CheckoutService | processPayment | VNPay payment | Order object, paymentMethod: "VNPAY" | Returns VNPay payment URL |
| UT_BE_ORDER_001 | BE | OrderService | updateOrderStatus | Valid transition | Order status: CREATED, newStatus: CONFIRMED | Order status updated to CONFIRMED |
| UT_BE_ORDER_002 | BE | OrderService | updateOrderStatus | Invalid transition | Order status: DELIVERED, newStatus: CREATED | Throws InvalidStatusTransitionException |
| UT_BE_ORDER_003 | BE | OrderService | cancelOrder | Order in cancellable state | Order status: CREATED | Order status changed to CANCELLED |
| UT_BE_ORDER_004 | BE | OrderService | cancelOrder | Order not cancellable | Order status: SHIPPED | Throws OrderNotCancellableException |
| UT_BE_ORDER_005 | BE | OrderService | getOrdersByUser | User with orders | User object | Returns list of user's orders |
| UT_BE_PAYMENT_001 | BE | PaymentService | createVNPayPayment | Valid order | Order with amount 100.0 | Returns VNPay payment URL with correct parameters |
| UT_BE_PAYMENT_002 | BE | PaymentService | processVNPayCallback | Success callback | vnp_ResponseCode: "00", orderId: "ORD001" | Order status updated to CONFIRMED |
| UT_BE_PAYMENT_003 | BE | PaymentService | processVNPayCallback | Failure callback | vnp_ResponseCode: "01", orderId: "ORD001" | Order remains unpaid, error logged |
| UT_BE_PAYMENT_004 | BE | PaymentService | validateVNPaySignature | Valid signature | VNPay callback parameters with valid hash | Returns true |
| UT_BE_PAYMENT_005 | BE | PaymentService | validateVNPaySignature | Invalid signature | VNPay callback parameters with invalid hash | Returns false |
| UT_FE_VALIDATION_001 | FE | LoginForm | validateEmail | Valid email | email: "user@example.com" | Returns true |
| UT_FE_VALIDATION_002 | FE | LoginForm | validateEmail | Invalid email | email: "invalid-email" | Returns false |
| UT_FE_VALIDATION_003 | FE | CheckoutForm | validatePhone | Valid phone | phone: "+1234567890" | Returns true |
| UT_FE_VALIDATION_004 | FE | CheckoutForm | validatePhone | Invalid phone | phone: "invalid" | Returns false |
| UT_FE_UTILS_001 | FE | priceUtils | formatPrice | Valid price | price: 99.99 | Returns "$99.99" |
| UT_FE_UTILS_002 | FE | priceUtils | formatPrice | Zero price | price: 0 | Returns "$0.00" |
| UT_FE_UTILS_003 | FE | discountUtils | calculateDiscountedPrice | Valid discount | originalPrice: 100, discountPercent: 10 | Returns 90 |
| UT_FE_UTILS_004 | FE | discountUtils | calculateDiscountedPrice | Zero discount | originalPrice: 100, discountPercent: 0 | Returns 100 |
| UT_FE_UTILS_005 | FE | orderStatusUtils | getStatusDisplayText | Valid status | status: "CONFIRMED" | Returns "Order Confirmed" |
| UT_FE_UTILS_006 | FE | orderStatusUtils | getStatusDisplayText | Invalid status | status: "INVALID" | Returns "Unknown Status" |

## Part 2 — Unit Test Code Examples

### Backend: Authentication Service (JUnit 5 + Mockito)

```java
@SpringBootTest
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void authenticateUser_ValidCredentials_ReturnsUser() {
        // Arrange
        String username = "customer@example.com";
        String password = "password123";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("encodedPassword");
        mockUser.setRole("CUSTOMER");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);

        // Act
        User result = authenticationService.authenticateUser(username, password);

        // Assert
        assertNotNull(result);
        assertEquals("CUSTOMER", result.getRole());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void authenticateUser_InvalidPassword_ThrowsException() {
        // Arrange
        String username = "customer@example.com";
        String password = "wrongpass";
        User mockUser = new User();
        mockUser.setPassword("encodedPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> 
            authenticationService.authenticateUser(username, password));
    }
}
```

### Backend: Cart Service (JUnit 5 + Mockito)

```java
@SpringBootTest
class CartServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void calculateTotal_ValidCart_ReturnsCorrectTotal() {
        // Arrange
        Cart cart = new Cart();
        CartItem item1 = new CartItem();
        item1.setProductId(1L);
        item1.setQuantity(2);
        item1.setPrice(50.0);

        CartItem item2 = new CartItem();
        item2.setProductId(2L);
        item2.setQuantity(1);
        item2.setPrice(30.0);

        cart.setItems(Arrays.asList(item1, item2));

        // Act
        double total = cartService.calculateTotal(cart);

        // Assert
        assertEquals(130.0, total);
    }

    @Test
    void validateStock_SufficientStock_ReturnsTrue() {
        // Arrange
        Long productId = 1L;
        int requestedQuantity = 2;
        Product mockProduct = new Product();
        mockProduct.setStockQuantity(5);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        // Act
        boolean result = cartService.validateStock(productId, requestedQuantity);

        // Assert
        assertTrue(result);
    }
}
```

### Backend: Payment Service (JUnit 5 + Mockito)

```java
@SpringBootTest
class PaymentServiceTest {

    @Mock
    private VNPayConfig vnPayConfig;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void processVNPayCallback_SuccessResponse_UpdatesOrderStatus() {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_TxnRef", "ORD001");
        params.put("vnp_SecureHash", "valid_hash");

        Order mockOrder = new Order();
        mockOrder.setId("ORD001");
        mockOrder.setStatus("CREATED");

        when(orderRepository.findById("ORD001")).thenReturn(Optional.of(mockOrder));
        when(vnPayConfig.getSecretKey()).thenReturn("secret");

        // Act
        paymentService.processVNPayCallback(params);

        // Assert
        assertEquals("CONFIRMED", mockOrder.getStatus());
        verify(orderRepository).save(mockOrder);
    }

    @Test
    void validateVNPaySignature_InvalidHash_ReturnsFalse() {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_SecureHash", "invalid_hash");

        when(vnPayConfig.getSecretKey()).thenReturn("secret");

        // Act
        boolean result = paymentService.validateVNPaySignature(params);

        // Assert
        assertFalse(result);
    }
}
```

### Frontend: Form Validation (Jest + React Testing Library)

```typescript
// LoginForm.test.tsx
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LoginForm from './LoginForm';

describe('LoginForm', () => {
  test('validates email format correctly', async () => {
    // Arrange
    const user = userEvent.setup();
    render(<LoginForm onSubmit={jest.fn()} />);
    
    const emailInput = screen.getByLabelText(/email/i);
    
    // Act
    await user.type(emailInput, 'invalid-email');
    await user.click(screen.getByRole('button', { name: /login/i }));
    
    // Assert
    expect(screen.getByText(/invalid email format/i)).toBeInTheDocument();
  });

  test('accepts valid email format', async () => {
    // Arrange
    const user = userEvent.setup();
    const mockSubmit = jest.fn();
    render(<LoginForm onSubmit={mockSubmit} />);
    
    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    
    // Act
    await user.type(emailInput, 'user@example.com');
    await user.type(passwordInput, 'password123');
    await user.click(screen.getByRole('button', { name: /login/i }));
    
    // Assert
    expect(mockSubmit).toHaveBeenCalledWith({
      email: 'user@example.com',
      password: 'password123'
    });
  });
});
```

### Frontend: Utility Functions (Jest)

```typescript
// priceUtils.test.ts
import { formatPrice } from './priceUtils';

describe('formatPrice', () => {
  test('formats price with dollar sign and two decimals', () => {
    // Arrange & Act
    const result = formatPrice(99.99);
    
    // Assert
    expect(result).toBe('$99.99');
  });

  test('formats zero price correctly', () => {
    // Arrange & Act
    const result = formatPrice(0);
    
    // Assert
    expect(result).toBe('$0.00');
  });
});

// discountUtils.test.ts
import { calculateDiscountedPrice } from './discountUtils';

describe('calculateDiscountedPrice', () => {
  test('calculates discounted price correctly', () => {
    // Arrange
    const originalPrice = 100;
    const discountPercent = 10;
    
    // Act
    const result = calculateDiscountedPrice(originalPrice, discountPercent);
    
    // Assert
    expect(result).toBe(90);
  });

  test('handles zero discount', () => {
    // Arrange
    const originalPrice = 100;
    const discountPercent = 0;
    
    // Act
    const result = calculateDiscountedPrice(originalPrice, discountPercent);
    
    // Assert
    expect(result).toBe(100);
  });
});