package fpl.sd.backend.service;

import fpl.sd.backend.configuration.VNPayConfig;
import fpl.sd.backend.constant.OrderConstants;
import fpl.sd.backend.dto.request.CreatePaymentOrderRequest;
import fpl.sd.backend.dto.request.OrderRequest;
import fpl.sd.backend.dto.request.PaymentCallbackRequest;
import fpl.sd.backend.dto.request.PaymentRequest;
import fpl.sd.backend.dto.response.OrderResponse;
import fpl.sd.backend.dto.response.PaymentResponse;
import fpl.sd.backend.entity.CustomerOrder;
import fpl.sd.backend.entity.PaymentDetail;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.repository.PaymentDetailRepository;
import fpl.sd.backend.utils.VNPayUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    VNPayConfig vnPayConfig;
    OrderService orderService;
    PaymentValidationService validationService;
    PaymentDetailRepository paymentDetailRepository;

    public String createPayment(String orderId, String ipAddress) {
        try {
            CustomerOrder order = orderService.getOrderById(orderId);
            if (order == null) {
                throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }

            // Kiểm tra trạng thái đơn hàng - chỉ cho phép thanh toán với đơn hàng CREATED
            if (order.getOrderStatus() != OrderConstants.OrderStatus.CREATED) {
                throw new AppException(ErrorCode.ORDER_CANNOT_BE_PAID);
            }

            long amount = (long) (order.getFinalTotal() * 100L);

            Map<String, String> vnpParams = vnPayConfig.getVNPayConfig();
            vnpParams.put("vnp_Amount", String.valueOf(amount));
            vnpParams.put("vnp_OrderInfo", order.getId());
            vnpParams.put("vnp_IpAddr", ipAddress);

            // Tạo query string và secure hash
            String queryUrl = VNPayUtils.generateQueryUrl(vnpParams, true);
            String hashData = VNPayUtils.generateQueryUrl(vnpParams, false);
            String vnpSecureHash = VNPayUtils.hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData);
            queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
            
            String paymentUrl = vnPayConfig.getVnpPayUrl() + "?" + queryUrl;
            log.info("Created VNPay payment URL for order {}: {}", orderId, paymentUrl);
            
            return paymentUrl;
        } catch (AppException e) {
            log.error("Error creating payment for order {}: {}", orderId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating payment for order {}", orderId, e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Tạo đơn hàng VNPay (không trừ tồn kho) và trả về payment URL
     * Tồn kho sẽ được trừ khi callback thành công từ VNPay
     */
    @Transactional
    public String createPaymentOrder(CreatePaymentOrderRequest request) {
        try {
            // Convert sang OrderRequest
            OrderRequest orderRequest = OrderRequest.builder()
                    .originalTotal(request.getOriginalTotal())
                    .discountAmount(request.getDiscountAmount())
                    .finalTotal(request.getFinalTotal())
                    .discountId(request.getDiscountId())
                    .userId(request.getUserId())
                    .items(request.getItems())
                    .build();
            
            // Tạo đơn hàng KHÔNG trừ tồn kho (deductInventory = false)
            OrderResponse orderResponse = orderService.createOrder(orderRequest, false);
            log.info("Created order {} without deducting inventory", orderResponse.getOrderId());
            
            // Tạo payment URL
            String paymentUrl = createPayment(orderResponse.getOrderId(), request.getIpAddress());
            
            return paymentUrl;
        } catch (AppException e) {
            log.error("Error creating payment order: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating payment order", e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional
    public PaymentResponse processPaymentCallback(PaymentCallbackRequest request) {
        log.info("Processing VNPay callback for order: {} with transaction: {}", 
                request.getOrderId(), request.getTransactionNo());

        // Kiểm tra xem giao dịch đã được xử lý chưa
        if (paymentDetailRepository.existsByTransactionNo(request.getTransactionNo())) {
            log.warn("Payment already processed for transaction: {}", request.getTransactionNo());
            PaymentDetail existingPayment = paymentDetailRepository.findByTransactionNo(request.getTransactionNo())
                    .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_ALREADY_PROCESSED));

            return PaymentResponse.builder()
                    .paymentDetail(existingPayment)
                    .orderStatus(existingPayment.getOrder().getOrderStatus())
                    .build();
        }

        // Validate payment signature
        try {
            validationService.validatePaymentSignature(request, vnPayConfig.getVnpHashSecret());
            log.info("Payment signature validated successfully for order: {}", request.getOrderId());
        } catch (AppException e) {
            log.error("Payment signature validation failed for order: {} - Error: {}", 
                     request.getOrderId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during payment signature validation for order: {}", 
                     request.getOrderId(), e);
            throw new AppException(ErrorCode.PAYMENT_SIGNATURE_INVALID);
        }

        // Validate payment status
        boolean isSuccess = validationService.validatePaymentStatus(
                request.getResponseCode(),
                request.getTransactionStatus()
        );

        if (!isSuccess) {
            log.warn("Payment failed for order: {} - Response code: {}, Transaction status: {}", 
                    request.getOrderId(), request.getResponseCode(), request.getTransactionStatus());
            
            try {
                PaymentDetail failedPayment = createPaymentDetail(request);
                CustomerOrder updatedOrder = orderService.updateStatus(
                        request.getOrderId(),
                        OrderConstants.OrderStatus.PAYMENT_FAILED,
                        failedPayment
                );

                failedPayment.setOrder(updatedOrder);
                paymentDetailRepository.save(failedPayment);
                
                log.info("Order {} status updated to PAYMENT_FAILED", request.getOrderId());
                
                return PaymentResponse.builder()
                        .paymentDetail(failedPayment)
                        .orderStatus(updatedOrder.getOrderStatus())
                        .build();
                        
            } catch (Exception e) {
                log.error("Error processing failed payment for order: {}", request.getOrderId(), e);
                throw new AppException(ErrorCode.PAYMENT_FAILED);
            }
        }

        // Tạo payment details cho giao dịch thành công
        try {
            PaymentDetail paymentDetail = createPaymentDetail(request);

            // Cập nhật trạng thái đơn hàng thành PAID
            CustomerOrder updatedOrder = orderService.updateStatus(
                    request.getOrderId(),
                    OrderConstants.OrderStatus.PAID,
                    paymentDetail
            );
            
            paymentDetail.setOrder(updatedOrder);
            paymentDetailRepository.save(paymentDetail);

            log.info("Payment processed successfully for order: {} with amount: {}", 
                    request.getOrderId(), paymentDetail.getAmount());

            return PaymentResponse.builder()
                    .paymentDetail(paymentDetail)
                    .orderStatus(updatedOrder.getOrderStatus())
                    .build();
                    
        } catch (Exception e) {
            log.error("Error processing successful payment for order: {}", request.getOrderId(), e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private PaymentDetail createPaymentDetail(PaymentCallbackRequest request) {
        log.debug("Creating payment detail for transaction: {} with amount: {}", 
                 request.getTransactionNo(), request.getAmount());
                 
        return PaymentDetail.builder()
                .amount(request.getAmount() / 100)
                .bankCode(request.getBankCode())
                .transactionNo(request.getTransactionNo())
                .paymentDate(request.getPaymentDate())
                .cardType(request.getCardType())
                .build();
    }

}
