//package fpl.sd.backend.service;
//
//import fpl.sd.backend.constant.OrderConstants;
//import fpl.sd.backend.dto.request.OrderUpdateRequest;
//import fpl.sd.backend.dto.response.OrderDetailResponse;
//import fpl.sd.backend.entity.*;
//import fpl.sd.backend.exception.AppException;
//import fpl.sd.backend.exception.ErrorCode;
//import fpl.sd.backend.mapper.OrderMapper;
//import fpl.sd.backend.repository.*;
//import org.springframework.data.domain.PageRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class OrderDetailServiceUnitTest {
//
//    @Mock
//    private CustomerOrderRepository orderRepository;
//    @Mock
//    private OrderDetailRepository orderDetailRepository;
//    @Mock
//    private ShoeVariantRepository shoeVariantRepository;
//    @Mock
//    private ShoeImageRepository shoeImageRepository;
//    @Mock
//    private OrderMapper orderMapper;
//
//    @InjectMocks
//    private OrderDetailService orderDetailService;
//
//    // Dummy Data
//    private User user;
//    private CustomerOrder order;
//    private OrderDetail orderDetail;
//    private ShoeVariant shoeVariant;
//    private final String USERNAME = "testuser";
//    private final String ORDER_ID = "order-123";
//
//    @BeforeEach
//    void setUp() {
//        // Setup User
//        user = User.builder()
//                .id("user-123")
//                .username(USERNAME)
//                .fullName("Test User")
//                .email("test@example.com")
//                .address("123 Test St")
//                .phone("1234567890")
//                .build();
//
//        // Setup Shoe and Variant
//        Shoe shoe = Shoe.builder()
//                .id(1)
//                .name("Test Shoe")
//                .price(100.0)
//                .build();
//
//        SizeChart size = SizeChart.builder()
//                .sizeNumber(42)
//                .build();
//
//        shoeVariant = ShoeVariant.builder()
//                .id("variant-123")
//                .shoe(shoe)
//                .sizeChart(size)
//                .stockQuantity(10)
//                .build();
//
//        // Setup Order
//        order = CustomerOrder.builder()
//                .id(ORDER_ID)
//                .user(user)
//                .orderDate(Instant.now())
//                .orderStatus(OrderConstants.OrderStatus.CREATED)
//                .finalTotal(200.0)
//                .originalTotal(200.0)
//                .discountAmount(0.0)
//                .build();
//
//        // Setup OrderDetail
//        orderDetail = OrderDetail.builder()
//                .id(new OrderDetail.OrderDetailId(ORDER_ID, "variant-123"))
//                .order(order)
//                .variant(shoeVariant)
//                .price(100.0)
//                .quantity(2)
//                .build();
//
//        order.setOrderDetails(List.of(orderDetail));
//    }
//
//    @Test
//    void getAllOrdersByUserId_success_shouldReturnOrderList() {
//        // Arrange
//        when(orderRepository.findByUserUsernameOrderByOrderDateDesc(USERNAME))
//                .thenReturn(List.of(order));
//
//        // Act
//        List<OrderDetailResponse> result = orderDetailService.getAllOrdersByUserId(USERNAME);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).getId()).isEqualTo(ORDER_ID);
//
//        verify(orderRepository).findByUserUsernameOrderByOrderDateDesc(USERNAME);
//    }
//
//    @Test
//    void getOrderByIdAndUserId_success_shouldReturnOrderDetail() {
//        // Arrange
//        when(orderRepository.findByIdAndUserUsername(ORDER_ID, USERNAME))
//                .thenReturn(Optional.of(order));
//
//        // Act
//        OrderDetailResponse result = orderDetailService.getOrderByIdAndUserId(ORDER_ID, USERNAME);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.getId()).isEqualTo(ORDER_ID);
//        assertThat(result.getUsername()).isEqualTo(USERNAME);
//        assertThat(result.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.CREATED);
//
//        verify(orderRepository).findByIdAndUserUsername(ORDER_ID, USERNAME);
//    }
//
//    @Test
//    void getOrderByIdAndUserId_orderNotFound_shouldThrowException() {
//        // Arrange
//        when(orderRepository.findByIdAndUserUsername(ORDER_ID, USERNAME))
//                .thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThatThrownBy(() -> orderDetailService.getOrderByIdAndUserId(ORDER_ID, USERNAME))
//                .isInstanceOf(AppException.class)
//                .extracting(e -> ((AppException) e).getErrorCode())
//                .isEqualTo(ErrorCode.ORDER_NOT_FOUND);
//    }
//
//    @Test
//    void getOrderById_success_shouldReturnOrderDetail() {
//        // Arrange
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//
//        // Act
//        OrderDetailResponse result = orderDetailService.getOrderById(ORDER_ID);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.getId()).isEqualTo(ORDER_ID);
//        assertThat(result.getFinalTotal()).isEqualTo(200.0);
//
//        verify(orderRepository).findById(ORDER_ID);
//    }
//
//    @Test
//    void getOrderById_orderNotFound_shouldThrowException() {
//        // Arrange
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThatThrownBy(() -> orderDetailService.getOrderById(ORDER_ID))
//                .isInstanceOf(AppException.class)
//                .extracting(e -> ((AppException) e).getErrorCode())
//                .isEqualTo(ErrorCode.ORDER_NOT_FOUND);
//    }
//
//    @Test
//    void getByOrderStatus_validStatus_shouldReturnOrders() {
//        // Arrange
//        when(orderRepository.findByOrderStatus(OrderConstants.OrderStatus.CREATED))
//                .thenReturn(List.of(order));
//
//        // Act
//        List<OrderDetailResponse> result = orderDetailService.getByOrderStatus("CREATED");
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.CREATED);
//
//        verify(orderRepository).findByOrderStatus(OrderConstants.OrderStatus.CREATED);
//    }
//
//    @Test
//    void getByOrderStatus_invalidStatus_shouldThrowException() {
//        // Act & Assert
//        assertThatThrownBy(() -> orderDetailService.getByOrderStatus("INVALID_STATUS"))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("Invalid orderStatus provided");
//    }
//
//    @Test
//    void getAllOrderDetails_shouldReturnAllOrders() {
//        // Arrange
//        when(orderRepository.findAll()).thenReturn(List.of(order));
//
//        // Act
//        List<OrderDetailResponse> result = orderDetailService.getAllOrderDetails();
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(1);
//
//        verify(orderRepository).findAll();
//    }
//
//    @Test
//    void getOrderStatusCounts_shouldReturnStatusCounts() {
//        // Arrange
//        CustomerOrder order2 = CustomerOrder.builder()
//                .id("order-456")
//                .user(user)
//                .orderStatus(OrderConstants.OrderStatus.PAID)
//                .build();
//
//        when(orderRepository.findAll()).thenReturn(List.of(order, order2));
//
//        // Act
//        var result = orderDetailService.getOrderStatusCounts();
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.get(OrderConstants.OrderStatus.CREATED)).isEqualTo(1L);
//        assertThat(result.get(OrderConstants.OrderStatus.PAID)).isEqualTo(1L);
//
//        verify(orderRepository).findAll();
//    }
//
//    @Test
//    void updateOrderDetail_staffUpdateStatusToConfirmed_shouldSucceed() {
//        // Arrange
//        order.setOrderStatus(OrderConstants.OrderStatus.CREATED);
//        OrderUpdateRequest request = new OrderUpdateRequest();
//        request.setOrderStatus(OrderConstants.OrderStatus.CONFIRMED);
//
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//
//        // Act
//        OrderDetailResponse result = orderDetailService.updateOrderDetail(ORDER_ID, request);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(order.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.CONFIRMED);
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void updateOrderDetail_staffUpdateStatusToPrepared_shouldSucceed() {
//        // Arrange
//        order.setOrderStatus(OrderConstants.OrderStatus.CONFIRMED);
//        OrderUpdateRequest request = new OrderUpdateRequest();
//        request.setOrderStatus(OrderConstants.OrderStatus.PREPARING);
//
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//
//        // Act
//        OrderDetailResponse result = orderDetailService.updateOrderDetail(ORDER_ID, request);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(order.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.PREPARING);
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void updateOrderDetail_staffUpdateStatusToReadyForDelivery_shouldSucceed() {
//        // Arrange
//        order.setOrderStatus(OrderConstants.OrderStatus.PREPARING);
//        OrderUpdateRequest request = new OrderUpdateRequest();
//        request.setOrderStatus(OrderConstants.OrderStatus.READY_FOR_DELIVERY);
//
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//
//        // Act
//        OrderDetailResponse result = orderDetailService.updateOrderDetail(ORDER_ID, request);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(order.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.READY_FOR_DELIVERY);
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void updateOrderDetail_staffUpdateStatusToOutForDelivery_shouldSucceed() {
//        // Arrange
//        order.setOrderStatus(OrderConstants.OrderStatus.READY_FOR_DELIVERY);
//        OrderUpdateRequest request = new OrderUpdateRequest();
//        request.setOrderStatus(OrderConstants.OrderStatus.OUT_FOR_DELIVERY);
//
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//
//        // Act
//        OrderDetailResponse result = orderDetailService.updateOrderDetail(ORDER_ID, request);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(order.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.OUT_FOR_DELIVERY);
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void updateOrderDetail_staffUpdateStatusToDelivered_shouldSucceed() {
//        // Arrange
//        order.setOrderStatus(OrderConstants.OrderStatus.OUT_FOR_DELIVERY);
//        OrderUpdateRequest request = new OrderUpdateRequest();
//        request.setOrderStatus(OrderConstants.OrderStatus.DELIVERED);
//
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//
//        // Act
//        OrderDetailResponse result = orderDetailService.updateOrderDetail(ORDER_ID, request);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(order.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.DELIVERED);
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void updateOrderDetail_invalidStatusTransition_shouldThrowException() {
//        // Arrange - Try to jump from CREATED directly to DELIVERED (invalid)
//        order.setOrderStatus(OrderConstants.OrderStatus.CREATED);
//        OrderUpdateRequest request = new OrderUpdateRequest();
//        request.setOrderStatus(OrderConstants.OrderStatus.DELIVERED);
//
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//
//        // Act & Assert
//        assertThatThrownBy(() -> orderDetailService.updateOrderDetail(ORDER_ID, request))
//                .isInstanceOf(AppException.class)
//                .extracting(e -> ((AppException) e).getErrorCode())
//                .isEqualTo(ErrorCode.ORDER_STATUS_TRANSITION_INVALID);
//    }
//
//    @Test
//    void updateOrderDetail_adminTryToChangeCreatedToPaid_shouldThrowException() {
//        // Arrange - Admin trying to manually change CREATED to PAID (should only happen via VNPay)
//        order.setOrderStatus(OrderConstants.OrderStatus.CREATED);
//        OrderUpdateRequest request = new OrderUpdateRequest();
//        request.setOrderStatus(OrderConstants.OrderStatus.PAID);
//
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//
//        // Act & Assert
//        assertThatThrownBy(() -> orderDetailService.updateOrderDetail(ORDER_ID, request))
//                .isInstanceOf(AppException.class)
//                .extracting(e -> ((AppException) e).getErrorCode())
//                .isEqualTo(ErrorCode.ORDER_STATUS_TRANSITION_INVALID);
//    }
//
//    @Test
//    void updateOrderDetail_statusChangeToCancelled_shouldRestoreInventory() {
//        // Arrange
//        order.setOrderStatus(OrderConstants.OrderStatus.CONFIRMED);
//        OrderUpdateRequest request = new OrderUpdateRequest();
//        request.setOrderStatus(OrderConstants.OrderStatus.CANCELLED);
//
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//        when(orderDetailRepository.findOrderDetailsByOrderId(ORDER_ID)).thenReturn(List.of(orderDetail));
//
//        // Act
//        OrderDetailResponse result = orderDetailService.updateOrderDetail(ORDER_ID, request);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(order.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.CANCELLED);
//        // Verify inventory was restored (10 + 2 = 12)
//        assertThat(shoeVariant.getStockQuantity()).isEqualTo(12);
//        verify(shoeVariantRepository).save(shoeVariant);
//    }
//
//    @Test
//    void updateOrderDetail_statusChangeToRejected_shouldRestoreInventory() {
//        // Arrange
//        order.setOrderStatus(OrderConstants.OrderStatus.PREPARING);
//        OrderUpdateRequest request = new OrderUpdateRequest();
//        request.setOrderStatus(OrderConstants.OrderStatus.REJECTED);
//
//        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
//        when(orderDetailRepository.findOrderDetailsByOrderId(ORDER_ID)).thenReturn(List.of(orderDetail));
//
//        // Act
//        OrderDetailResponse result = orderDetailService.updateOrderDetail(ORDER_ID, request);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(order.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.REJECTED);
//        // Verify inventory was restored (10 + 2 = 12)
//        assertThat(shoeVariant.getStockQuantity()).isEqualTo(12);
//        verify(shoeVariantRepository).save(shoeVariant);
//    }
//
//    @Test
//    void getOrderPaging_withStatusFilter_shouldReturnFilteredOrders() {
//        // Arrange
//        when(orderRepository.findCustomerOrderByFilters(OrderConstants.OrderStatus.CREATED, PageRequest.of(0, 8)))
//                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(order)));
//
//        // Act
//        var result = orderDetailService.getOrderPaging("CREATED", 1, 8, "date");
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.getData()).hasSize(1);
//        assertThat(result.getData().get(0).getId()).isEqualTo(ORDER_ID);
//    }
//
//    @Test
//    void getOrderPaging_withoutStatusFilter_shouldReturnAllOrders() {
//        // Arrange
//        when(orderRepository.findCustomerOrderByFilters(null, PageRequest.of(0, 8)))
//                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(order)));
//
//        // Act
//        var result = orderDetailService.getOrderPaging(null, 1, 8, "date");
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.getData()).hasSize(1);
//    }
//}