package fpl.sd.backend.service;

import fpl.sd.backend.constant.DiscountConstants;
import fpl.sd.backend.constant.OrderConstants;
import fpl.sd.backend.dto.request.CartItemRequest;
import fpl.sd.backend.dto.request.OrderRequest;
import fpl.sd.backend.dto.response.ApplyDiscountResponse;
import fpl.sd.backend.dto.response.OrderResponse;
import fpl.sd.backend.entity.*;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.mapper.OrderMapper;
import fpl.sd.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderServiceUnitTest {

    @Mock
    private OrderMapper orderMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoeVariantRepository shoeVariantRepository;
    @Mock
    private CustomerOrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private DiscountRepository discountRepository;
    @Mock
    private DiscountValidationService discountValidationService;

    @InjectMocks
    private OrderService orderService;

    // Dummy Data
    private User user;
    private ShoeVariant shoeVariant;
    private Discount discount;
    private CustomerOrder order;
    private OrderRequest orderRequest;
    private final String USER_ID = "user-123";
    private final String VARIANT_ID = "variant-123";
    private final String DISCOUNT_CODE = "SAVE10";

    @BeforeEach
    void setUp() {
        // Setup User
        user = User.builder()
                .id(USER_ID)
                .username("testuser")
                .build();

        // Setup ShoeVariant
        Shoe shoe = Shoe.builder()
                .id(1)
                .name("Test Shoe")
                .price(100.0)
                .build();

        SizeChart size = SizeChart.builder()
                .sizeNumber(42)
                .build();

        shoeVariant = ShoeVariant.builder()
                .id(VARIANT_ID)
                .shoe(shoe)
                .sizeChart(size)
                .stockQuantity(10)
                .build();

        // Setup Discount
        discount = Discount.builder()
                .id(1)
                .code(DISCOUNT_CODE)
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .percentage(10.0)
                .minimumOrderAmount(50.0)
                .usageLimit(100)
                .usedCount(5)
                .isActive(true)
                .startDate(Instant.now().minusSeconds(3600))
                .endDate(Instant.now().plusSeconds(3600))
                .build();

        // Setup Order
        order = CustomerOrder.builder()
                .id("order-123")
                .user(user)
                .orderStatus(OrderConstants.OrderStatus.CREATED)
                .build();

        // Setup OrderRequest
        CartItemRequest cartItem = CartItemRequest.builder()
                .variantId(VARIANT_ID)
                .quantity(2)
                .price(100.0)
                .productId(1)
                .build();

        orderRequest = OrderRequest.builder()
                .userId(USER_ID)
                .originalTotal(200.0)
                .discountAmount(20.0)
                .finalTotal(180.0)
                .discountId(1)
                .items(List.of(cartItem))
                .build();
    }

    @Test
    void createOrder_success_shouldCreateOrderAndDeductInventory() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(shoeVariantRepository.findById(VARIANT_ID)).thenReturn(Optional.of(shoeVariant));
        when(discountRepository.findById(1)).thenReturn(Optional.of(discount));
        when(orderMapper.toCustomerOrder(orderRequest)).thenReturn(order);
        when(orderRepository.save(any(CustomerOrder.class))).thenReturn(order);
        when(orderDetailRepository.save(any(OrderDetail.class))).thenAnswer(i -> i.getArguments()[0]);
        when(discountValidationService.isDiscountApplicableToOrder(any(Discount.class), anyList())).thenReturn(true);

        OrderResponse expectedResponse = OrderResponse.builder()
                .orderId("order-123")
                .build();
        when(orderMapper.toOrderResponse(order)).thenReturn(expectedResponse);

        // Act
        OrderResponse response = orderService.createOrder(orderRequest, true);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo("order-123");

        // Verify inventory was deducted
        assertThat(shoeVariant.getStockQuantity()).isEqualTo(8); // 10 - 2

        // Verify discount usedCount was incremented
        assertThat(discount.getUsedCount()).isEqualTo(6); // 5 + 1

        verify(orderRepository, times(2)).save(order); // Once for order, once for final save
        verify(orderDetailRepository).save(any(OrderDetail.class));
    }

    @Test
    void createOrder_insufficientInventory_shouldThrowException() {
        // Arrange
        CartItemRequest cartItem = CartItemRequest.builder()
                .variantId(VARIANT_ID)
                .quantity(15) // More than available stock (10)
                .price(100.0)
                .productId(1)
                .build();

        OrderRequest request = OrderRequest.builder()
                .userId(USER_ID)
                .originalTotal(1500.0)
                .finalTotal(1500.0)
                .items(List.of(cartItem))
                .build();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(shoeVariantRepository.findById(VARIANT_ID)).thenReturn(Optional.of(shoeVariant));
        when(orderMapper.toCustomerOrder(request)).thenReturn(order);
        when(orderRepository.save(any(CustomerOrder.class))).thenReturn(order);

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request, true))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.INSUFFICIENT_INVENTORY);

        // Verify inventory was not deducted
        assertThat(shoeVariant.getStockQuantity()).isEqualTo(10);
    }

    @Test
    void createOrder_invalidTotal_shouldThrowException() {
        // Arrange
        OrderRequest request = OrderRequest.builder()
                .userId(USER_ID)
                .originalTotal(0.0) // Invalid total
                .finalTotal(0.0)
                .items(List.of())
                .build();

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request, true))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_ORDER_TOTAL);
    }

    @Test
    void createOrder_userNotFound_shouldThrowException() {
        // Arrange - Create request without discount to avoid discount lookup before user lookup
        OrderRequest requestWithoutDiscount = OrderRequest.builder()
                .userId(USER_ID)
                .originalTotal(200.0)
                .finalTotal(200.0)
                .items(List.of(CartItemRequest.builder()
                        .variantId(VARIANT_ID)
                        .quantity(2)
                        .price(100.0)
                        .productId(1)
                        .build()))
                .build();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        when(orderMapper.toCustomerOrder(requestWithoutDiscount)).thenReturn(order);

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(requestWithoutDiscount, true))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("User Not Found");
    }

    @Test
    void createOrder_noDeductInventory_shouldNotDeductInventoryOrIncrementDiscount() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(shoeVariantRepository.findById(VARIANT_ID)).thenReturn(Optional.of(shoeVariant));
        when(discountRepository.findById(1)).thenReturn(Optional.of(discount));
        when(orderMapper.toCustomerOrder(orderRequest)).thenReturn(order);
        when(orderRepository.save(any(CustomerOrder.class))).thenReturn(order);
        when(orderDetailRepository.save(any(OrderDetail.class))).thenAnswer(i -> i.getArguments()[0]);
        when(discountValidationService.isDiscountApplicableToOrder(any(Discount.class), anyList())).thenReturn(true);

        OrderResponse expectedResponse = OrderResponse.builder()
                .orderId("order-123")
                .build();
        when(orderMapper.toOrderResponse(order)).thenReturn(expectedResponse);

        // Act
        orderService.createOrder(orderRequest, false);

        // Assert
        // Verify inventory was NOT deducted
        assertThat(shoeVariant.getStockQuantity()).isEqualTo(10);

        // Verify discount usedCount was NOT incremented
        assertThat(discount.getUsedCount()).isEqualTo(5);
    }

    @Test
    void applyDiscount_validDiscount_shouldReturnDiscountResponse() {
        // Arrange
        when(discountRepository.findByCode(DISCOUNT_CODE)).thenReturn(Optional.of(discount));

        // Act
        ApplyDiscountResponse response = orderService.applyDiscount(DISCOUNT_CODE, 100.0);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCoupon()).isEqualTo(DISCOUNT_CODE);
        assertThat(response.getPercentage()).isEqualTo(10.0);
        assertThat(response.getDiscountType()).isEqualTo(DiscountConstants.DiscountType.PERCENTAGE);
        assertThat(response.getMinimumOrderAmount()).isEqualTo(50.0);
    }

    @Test
    void applyDiscount_invalidCode_shouldThrowException() {
        // Arrange
        when(discountRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.applyDiscount("INVALID", 100.0))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.COUPON_INVALID);
    }

    @Test
    void applyDiscount_expiredDiscount_shouldThrowException() {
        // Arrange
        discount.setEndDate(Instant.now().minusSeconds(3600)); // Expired
        when(discountRepository.findByCode(DISCOUNT_CODE)).thenReturn(Optional.of(discount));

        // Act & Assert
        assertThatThrownBy(() -> orderService.applyDiscount(DISCOUNT_CODE, 100.0))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.COUPON_INVALID);
    }

    @Test
    void applyDiscount_insufficientOrderAmount_shouldThrowException() {
        // Arrange
        when(discountRepository.findByCode(DISCOUNT_CODE)).thenReturn(Optional.of(discount));

        // Act & Assert
        assertThatThrownBy(() -> orderService.applyDiscount(DISCOUNT_CODE, 30.0)) // Below minimum 50.0
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.MINIMUM_AMOUNT_NOT_MET);
    }

    @Test
    void applyDiscount_usageLimitReached_shouldThrowException() {
        // Arrange
        discount.setUsedCount(100); // At limit
        discount.setUsageLimit(100);
        when(discountRepository.findByCode(DISCOUNT_CODE)).thenReturn(Optional.of(discount));

        // Act & Assert
        assertThatThrownBy(() -> orderService.applyDiscount(DISCOUNT_CODE, 100.0))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.COUPON_INVALID);
    }

    @Test
    void cancelOrder_createdStatus_shouldCancelAndRestoreInventory() {
        // Arrange
        order.setOrderStatus(OrderConstants.OrderStatus.CREATED);

        OrderDetail orderDetail = OrderDetail.builder()
                .variant(shoeVariant)
                .quantity(2)
                .build();

        when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));
        when(orderDetailRepository.findOrderDetailsByOrderId("order-123")).thenReturn(List.of(orderDetail));
        when(orderRepository.save(order)).thenReturn(order);

        OrderResponse expectedResponse = OrderResponse.builder()
                .orderId("order-123")
                .build();
        when(orderMapper.toOrderResponse(order)).thenReturn(expectedResponse);

        // Act
        OrderResponse response = orderService.cancelOrder("order-123");

        // Assert
        assertThat(response).isNotNull();
        assertThat(order.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.CANCELLED);

        // Verify inventory was restored
        assertThat(shoeVariant.getStockQuantity()).isEqualTo(12); // 10 + 2

        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_deliveredStatus_shouldThrowException() {
        // Arrange
        order.setOrderStatus(OrderConstants.OrderStatus.DELIVERED);
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.cancelOrder("order-123"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
    }

    @Test
    void isOrderOwner_correctOwner_shouldReturnTrue() {
        // Arrange
        order.setUser(user);
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));

        // Act
        boolean result = orderService.isOrderOwner("order-123", "testuser");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void isOrderOwner_wrongOwner_shouldReturnFalse() {
        // Arrange
        order.setUser(user);
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));

        // Act
        boolean result = orderService.isOrderOwner("order-123", "wronguser");

        // Assert
        assertThat(result).isFalse();
    }
}