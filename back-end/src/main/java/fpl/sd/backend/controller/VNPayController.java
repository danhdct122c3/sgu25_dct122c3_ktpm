package fpl.sd.backend.controller;

import fpl.sd.backend.dto.APIResponse;
import fpl.sd.backend.dto.request.PaymentCallbackRequest;
import fpl.sd.backend.dto.request.PaymentRequest;
import fpl.sd.backend.dto.response.PaymentResponse;
import fpl.sd.backend.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayController {
    private static final Logger log = LoggerFactory.getLogger(VNPayController.class);
    PaymentService paymentService;


    @PostMapping("/create-payment")
    public APIResponse<String> createPayment(@RequestBody PaymentRequest paymentRequest) {
        return APIResponse.<String>builder()
                .flag(true)
                .message("Payment created successfully")
                .code(200)
                .result(paymentService.createPayment(paymentRequest.getOrderId(), paymentRequest.getIpAddress()))
                .build();
    }

    @GetMapping("/payment-callback")
    public APIResponse<PaymentResponse> paymentCallback(HttpServletRequest request) {
        //Extract payment callback data
        PaymentCallbackRequest callbackRequest = PaymentCallbackRequest.from(request);

        //Process payment
        PaymentResponse response = paymentService.processPaymentCallback(callbackRequest);

        return APIResponse.<PaymentResponse>builder()
                .flag(true)
                .message("Payment successful")
                .code(200)
                .result(response)
                .build();

    }
}
