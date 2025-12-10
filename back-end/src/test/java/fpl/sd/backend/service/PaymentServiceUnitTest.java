package fpl.sd.backend.service;

import fpl.sd.backend.configuration.VNPayConfig;
import fpl.sd.backend.constant.OrderConstants;
import fpl.sd.backend.dto.request.CreatePaymentOrderRequest;
import fpl.sd.backend.dto.request.OrderRequest;
import fpl.sd.backend.dto.request.PaymentCallbackRequest;
import fpl.sd.backend.dto.response.OrderResponse;
import fpl.sd.backend.dto.response.PaymentResponse;
import fpl.sd.backend.entity.CustomerOrder;
import fpl.sd.backend.entity.PaymentDetail;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.repository.PaymentDetailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceUnitTest {

    @Mock
    private VNPayConfig vnPayConfig;
    @Mock
    private OrderService orderService;
    @Mock
    private PaymentValidationService validationService;
    @Mock
    private PaymentDetailRepository paymentDetailRepository;

    @InjectMocks
    private PaymentService paymentService;

    // Dummy Data
    private CustomerOrder order;
    private PaymentCallbackRequest callbackRequest;
    private final String ORDER_ID = "order-123";
    private final String IP_ADDRESS = "192.168.1.1";

    @BeforeEach
    void setUp() {
        // Setup Order
        order = CustomerOrder.builder()
                .id(ORDER_ID)
                .finalTotal(1000.0)
                .orderStatus(OrderConstants.OrderStatus.CREATED)
                .build();

        // Setup PaymentCallbackRequest
        callbackRequest = PaymentCallbackRequest.builder()
                .orderId(ORDER_ID)
                .transactionNo("txn-123")
                .amount(100000L) // 1000.00 * 100
                .responseCode("00")
                .transactionStatus("00")
                .bankCode("NCB")
                .cardType("ATM")
                .paymentDate("20241125143000")
                .build();
    }

    @Test
    void createPayment_success_shouldReturnPaymentUrl() {
        // Arrange
        when(orderService.getOrderById(ORDER_ID)).thenReturn(order);

        Map<String, String> vnpParams = new java.util.HashMap<>();
        vnpParams.put("vnp_TmnCode", "TMNCODE");
        vnpParams.put("vnp_ReturnUrl", "http://return.url");
        vnpParams.put("vnp_TxnRef", "txn-ref");
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_CreateDate", "20241125143000");
        vnpParams.put("vnp_ExpireDate", "20241125150000");
        when(vnPayConfig.getVNPayConfig()).thenReturn(vnpParams);
        when(vnPayConfig.getVnpPayUrl()).thenReturn("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        when(vnPayConfig.getVnpHashSecret()).thenReturn("HASHSECRET");
        when(vnPayConfig.isVnpHashEncode()).thenReturn(true);
        when(vnPayConfig.isVnpHashUpper()).thenReturn(true);
        when(vnPayConfig.isVnpSendHashType()).thenReturn(false);

        // Act
        String paymentUrl = paymentService.createPayment(ORDER_ID, IP_ADDRESS);

        // Assert
        assertThat(paymentUrl).isNotNull();
        assertThat(paymentUrl).contains("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        assertThat(paymentUrl).contains("vnp_Amount=100000");
        assertThat(paymentUrl).contains("vnp_OrderInfo=" + ORDER_ID);
        assertThat(paymentUrl).contains("vnp_IpAddr=" + IP_ADDRESS);
        assertThat(paymentUrl).contains("vnp_SecureHash=");
    }

    @Test
    void createPayment_orderNotFound_shouldThrowException() {
        // Arrange
        when(orderService.getOrderById(ORDER_ID)).thenThrow(new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Act & Assert
        assertThatThrownBy(() -> paymentService.createPayment(ORDER_ID, IP_ADDRESS))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    void createPayment_orderNotCreatedStatus_shouldThrowException() {
        // Arrange
        order.setOrderStatus(OrderConstants.OrderStatus.CONFIRMED);
        when(orderService.getOrderById(ORDER_ID)).thenReturn(order);

        // Act & Assert
        assertThatThrownBy(() -> paymentService.createPayment(ORDER_ID, IP_ADDRESS))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.ORDER_CANNOT_BE_PAID);
    }

    @Test
    void createPaymentOrder_success_shouldReturnPaymentUrl() {
        // Arrange
        CreatePaymentOrderRequest request = CreatePaymentOrderRequest.builder()
                .userId("user-123")
                .originalTotal(1000.0)
                .discountAmount(100.0)
                .finalTotal(900.0)
                .items(java.util.List.of())
                .ipAddress(IP_ADDRESS)
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(ORDER_ID)
                .build();

        when(orderService.createOrder(any(OrderRequest.class), eq(false))).thenReturn(orderResponse);
        when(orderService.getOrderById(ORDER_ID)).thenReturn(order);

        Map<String, String> vnpParams = new java.util.HashMap<>();
        vnpParams.put("vnp_TmnCode", "TMNCODE");
        vnpParams.put("vnp_ReturnUrl", "http://return.url");
        vnpParams.put("vnp_TxnRef", "txn-ref");
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_CreateDate", "20241125143000");
        vnpParams.put("vnp_ExpireDate", "20241125150000");
        when(vnPayConfig.getVNPayConfig()).thenReturn(vnpParams);
        when(vnPayConfig.getVnpPayUrl()).thenReturn("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        when(vnPayConfig.getVnpHashSecret()).thenReturn("HASHSECRET");
        when(vnPayConfig.isVnpHashEncode()).thenReturn(true);
        when(vnPayConfig.isVnpHashUpper()).thenReturn(true);
        when(vnPayConfig.isVnpSendHashType()).thenReturn(false);

        // Act
        String paymentUrl = paymentService.createPaymentOrder(request);

        // Assert
        assertThat(paymentUrl).isNotNull();
        assertThat(paymentUrl).contains("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");

        // Verify order creation was called with deductInventory = false
        ArgumentCaptor<OrderRequest> orderRequestCaptor = ArgumentCaptor.forClass(OrderRequest.class);
        verify(orderService).createOrder(orderRequestCaptor.capture(), eq(false));
        OrderRequest capturedRequest = orderRequestCaptor.getValue();
        assertThat(capturedRequest.getOriginalTotal()).isEqualTo(1000.0);
        assertThat(capturedRequest.getFinalTotal()).isEqualTo(900.0);
    }

    @Test
    void processPaymentCallback_successfulPayment_shouldUpdateOrderToPaid() {
        // Arrange
        when(paymentDetailRepository.existsByTransactionNo("txn-123")).thenReturn(false);
        doNothing().when(validationService).validatePaymentSignature(callbackRequest);
        when(validationService.validatePaymentStatus("00", "00")).thenReturn(true);

        CustomerOrder updatedOrder = CustomerOrder.builder()
                .id(ORDER_ID)
                .orderStatus(OrderConstants.OrderStatus.PAID)
                .build();

        when(orderService.updateStatus(eq(ORDER_ID), eq(OrderConstants.OrderStatus.PAID), any(PaymentDetail.class)))
                .thenReturn(updatedOrder);

        when(paymentDetailRepository.save(any(PaymentDetail.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        PaymentResponse response = paymentService.processPaymentCallback(callbackRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.PAID);

        // Verify payment detail creation
        ArgumentCaptor<PaymentDetail> paymentDetailCaptor = ArgumentCaptor.forClass(PaymentDetail.class);
        verify(paymentDetailRepository).save(paymentDetailCaptor.capture());
        PaymentDetail savedPayment = paymentDetailCaptor.getValue();
        assertThat(savedPayment.getAmount()).isEqualTo(1000L);
        assertThat(savedPayment.getTransactionNo()).isEqualTo("txn-123");
        assertThat(savedPayment.getBankCode()).isEqualTo("NCB");
    }

    @Test
    void processPaymentCallback_failedPayment_shouldUpdateOrderToPaymentFailed() {
        // Arrange
        when(paymentDetailRepository.existsByTransactionNo("txn-123")).thenReturn(false);
        doNothing().when(validationService).validatePaymentSignature(callbackRequest);
        when(validationService.validatePaymentStatus("00", "00")).thenReturn(false); // Payment failed

        CustomerOrder updatedOrder = CustomerOrder.builder()
                .id(ORDER_ID)
                .orderStatus(OrderConstants.OrderStatus.PAYMENT_FAILED)
                .build();

        when(orderService.updateStatus(eq(ORDER_ID), eq(OrderConstants.OrderStatus.PAYMENT_FAILED), any(PaymentDetail.class)))
                .thenReturn(updatedOrder);

        when(paymentDetailRepository.save(any(PaymentDetail.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        PaymentResponse response = paymentService.processPaymentCallback(callbackRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.PAYMENT_FAILED);
    }

    @Test
    void processPaymentCallback_duplicateTransaction_shouldReturnExistingPayment() {
        // Arrange
        PaymentDetail existingPayment = PaymentDetail.builder()
                .transactionNo("txn-123")
                .order(order)
                .build();

        when(paymentDetailRepository.existsByTransactionNo("txn-123")).thenReturn(true);
        when(paymentDetailRepository.findByTransactionNo("txn-123")).thenReturn(java.util.Optional.of(existingPayment));

        // Act
        PaymentResponse response = paymentService.processPaymentCallback(callbackRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getPaymentDetail()).isEqualTo(existingPayment);

        // Verify no new processing occurred
        verify(validationService, never()).validatePaymentSignature(any());
        verify(orderService, never()).updateStatus(anyString(), any(), any());
    }

    @Test
    void processPaymentCallback_invalidSignature_shouldThrowException() {
        // Arrange
        when(paymentDetailRepository.existsByTransactionNo("txn-123")).thenReturn(false);
        doThrow(new AppException(ErrorCode.PAYMENT_SIGNATURE_INVALID))
                .when(validationService).validatePaymentSignature(callbackRequest);

        // Act & Assert
        assertThatThrownBy(() -> paymentService.processPaymentCallback(callbackRequest))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_SIGNATURE_INVALID);
    }
}