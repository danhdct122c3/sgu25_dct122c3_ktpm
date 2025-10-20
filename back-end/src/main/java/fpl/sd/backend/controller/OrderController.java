package fpl.sd.backend.controller;

import fpl.sd.backend.dto.APIResponse;
import fpl.sd.backend.dto.request.ApplyDiscountRequest;
import fpl.sd.backend.dto.request.OrderRequest;
import fpl.sd.backend.dto.response.ApplyDiscountResponse;
import fpl.sd.backend.dto.response.OrderResponse;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OrderController {
    OrderService orderService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public APIResponse<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request) {
        return APIResponse.<OrderResponse>builder()
                .code(200)
                .flag(true)
                .message("Order created")
                .result(orderService.createOrder(request))
                .build();

    }


//    @GetMapping("/{userId}/{coupon}")
//    public ApiResponse<?> applyCoupon(@PathVariable String userId, @PathVariable String coupon) {
//        try {
//            OrderDto orderDto = orderService.applyDiscount(userId, coupon);
//            return ApiResponse.<OrderDto>builder()
//                    .code(200)
//                    .flag(true)
//                    .message("Apply Successfully")
//                    .result(orderDto)
//                    .build();
//        } catch (ValidationException ex) {
//            return ApiResponse.builder()
//                    .code(400)
//                    .flag(false)
//                    .message("Discount has expired")
//                    .build();
//        } catch (AppException ex) {
//            return ApiResponse.builder()
//                    .code(404)
//                    .flag(false)
//                    .message(ex.getMessage())
//                    .build();
//        }
//    }

//    @PostMapping("/apply-discount")
//    public ApiResponse<?> applyDiscount(@RequestBody @Valid ApplyDiscountRequest request) {
//        try {
//            OrderDto orderDto = orderService.applyDiscount(request.getUserId(), request.getOrderId(),request.getDiscount());
//            return ApiResponse.<OrderDto>builder()
//                    .code(200)
//                    .flag(true)
//                    .message("Apply successfully")
//                    .result(orderDto)
//                    .build();
//        } catch (ValidationException ex) {
//            return ApiResponse.builder()
//                    .code(400)
//                    .flag(false)
//                    .message("Discount has expired")
//                    .build();
//        } catch (AppException ex) {
//            return ApiResponse.builder()
//                    .code(404)
//                    .flag(false)
//                    .message(ex.getMessage())
//                    .build();
//        }
//    }

    @PostMapping("/apply-discount")
    public APIResponse<?> applyDiscount(@RequestBody @Valid ApplyDiscountRequest request) {
        try {
            ApplyDiscountResponse applyDiscountResponse = orderService.applyDiscount(request.getDiscount(), request.getOrderAmount());
            return APIResponse.<ApplyDiscountResponse>builder()
                    .code(200)
                    .flag(true)
                    .message("Apply successfully")
                    .result(applyDiscountResponse)
                    .build();
        } catch (AppException ex) {
            return APIResponse.builder()
                    .code(404)
                    .flag(false)
                    .message(ex.getMessage())
                    .build();
        }
    }

    /**
     * Cancel an order
     * Protected endpoint - Authenticated users only
     * Users can only cancel their own orders
     * Orders can only be cancelled if status is PENDING or CONFIRMED (not SHIPPED, DELIVERED, or CANCELLED)
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{orderId}/cancel")
    public APIResponse<OrderResponse> cancelOrder(@PathVariable String orderId) {
        OrderResponse cancelledOrder = orderService.cancelOrder(orderId);
        return APIResponse.<OrderResponse>builder()
                .flag(true)
                .code(200)
                .message("Order cancelled successfully")
                .result(cancelledOrder)
                .build();
    }
}
