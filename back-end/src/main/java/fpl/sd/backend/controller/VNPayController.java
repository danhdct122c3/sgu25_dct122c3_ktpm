package fpl.sd.backend.controller;

import fpl.sd.backend.dto.APIResponse;
import fpl.sd.backend.dto.request.CreatePaymentOrderRequest;
import fpl.sd.backend.dto.request.PaymentCallbackRequest;
import fpl.sd.backend.dto.request.PaymentRequest;
import fpl.sd.backend.dto.response.PaymentResponse;
import fpl.sd.backend.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayController {
    private static final Logger log = LoggerFactory.getLogger(VNPayController.class);
    PaymentService paymentService;


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create-payment")
    public APIResponse<String> createPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            log.info("Creating payment for order: {}", paymentRequest.getOrderId());
            
            String paymentUrl = paymentService.createPayment(
                paymentRequest.getOrderId(), 
                paymentRequest.getIpAddress()
            );
            
            return APIResponse.<String>builder()
                    .flag(true)
                    .message("Payment created successfully")
                    .code(200)
                    .result(paymentUrl)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error creating payment for order: {}", paymentRequest.getOrderId(), e);
            return APIResponse.<String>builder()
                    .flag(false)
                    .message("Payment creation failed: " + e.getMessage())
                    .code(500)
                    .build();
        }
    }
    
    /**
     * Endpoint mới: Tạo đơn hàng VNPay (không trừ tồn kho) và tạo payment URL
     * Tồn kho sẽ được trừ khi callback thành công
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create-payment-order")
    public APIResponse<String> createPaymentOrder(@Valid @RequestBody CreatePaymentOrderRequest request) {
        try {
            log.info("Creating VNPay order for user: {}", request.getUserId());
            
            String paymentUrl = paymentService.createPaymentOrder(request);
            
            return APIResponse.<String>builder()
                    .flag(true)
                    .message("Payment order created successfully")
                    .code(200)
                    .result(paymentUrl)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error creating payment order for user: {}", request.getUserId(), e);
            return APIResponse.<String>builder()
                    .flag(false)
                    .message("Payment order creation failed: " + e.getMessage())
                    .code(500)
                    .build();
        }
    }

    @GetMapping("/payment-callback")
    public APIResponse<PaymentResponse> paymentCallback(HttpServletRequest request) {
        try {
            log.info("Received VNPay payment callback");
            
            //Extract payment callback data
            PaymentCallbackRequest callbackRequest = PaymentCallbackRequest.from(request);
            
            log.info("Processing payment callback for order: {}", callbackRequest.getOrderId());

            //Process payment
            PaymentResponse response = paymentService.processPaymentCallback(callbackRequest);

            return APIResponse.<PaymentResponse>builder()
                    .flag(true)
                    .message("Payment processed successfully")
                    .code(200)
                    .result(response)
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Invalid payment callback parameters: {}", e.getMessage());
            return APIResponse.<PaymentResponse>builder()
                    .flag(false)
                    .message("Invalid payment callback parameters: " + e.getMessage())
                    .code(400)
                    .build();
        } catch (Exception e) {
            log.error("Error processing payment callback", e);
            return APIResponse.<PaymentResponse>builder()
                    .flag(false)
                    .message("Payment processing failed")
                    .code(500)
                    .build();
        }
    }
}
