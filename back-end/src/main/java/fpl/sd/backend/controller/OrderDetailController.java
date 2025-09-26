package fpl.sd.backend.controller;

import fpl.sd.backend.constant.OrderConstants;
import fpl.sd.backend.dto.APIResponse;
import fpl.sd.backend.dto.PageResponse;
import fpl.sd.backend.dto.request.OrderUpdateRequest;

import fpl.sd.backend.dto.response.EnumResponse;
import fpl.sd.backend.dto.response.OrderDetailResponse;
import fpl.sd.backend.service.OrderDetailService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order-details")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailController {
    OrderDetailService orderDetailService;

    @GetMapping("/user/{userId}")
    public APIResponse<List<OrderDetailResponse>> getOrderDetailsByUserId(@PathVariable String userId) {
        return APIResponse.<List<OrderDetailResponse>>builder()
                .code(200)
                .flag(true)
                .message("Successfully loaded")
                .result(orderDetailService.getAllOrdersByUserId(userId))
                .build();
    }

    @GetMapping
    public APIResponse<List<OrderDetailResponse>> getOrderDetails() {
        return APIResponse.<List<OrderDetailResponse>>builder()
                .code(200)
                .flag(true)
                .message("Successfully loaded")
                .result(orderDetailService.getAllOrderDetails())
                .build();
    }


    @GetMapping("/order/{orderId}")
    public APIResponse<OrderDetailResponse> getOrderDetailByOrderId(@PathVariable String orderId) {
        return APIResponse.<OrderDetailResponse>builder()
                .code(200)
                .flag(true)
                .message("Successfully loaded")
                .result(orderDetailService.getOrderById(orderId))
                .build();
    }




    @PutMapping("/order/{orderId}")
    public APIResponse<OrderDetailResponse> updateOrderDetail(@PathVariable String orderId, @RequestBody @Valid OrderUpdateRequest request) {
        OrderDetailResponse orderDetailResponse = orderDetailService.updateOrderDetail(orderId, request);
        return APIResponse.<OrderDetailResponse>builder()
                .flag(true)
                .code(200)
                .message("Order updated successfully")
                .result(orderDetailResponse)
                .build();
    }


    @GetMapping("/order/{orderId}/user/{userId}")
    public APIResponse<OrderDetailResponse> getOrderDetailByOrderIdAndUserId(@PathVariable String orderId, @PathVariable String userId) {
        return APIResponse.<OrderDetailResponse>builder()
                .code(200)
                .flag(true)
                .message("Successfully loaded")
                .result(orderDetailService.getOrderByIdAndUserId(orderId, userId))
                .build();
    }

    @GetMapping("/orderStatus")
    public APIResponse<List<EnumResponse>> getByOrderStatus() {
        return APIResponse.<List<EnumResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(OrderConstants.getAllOrderStatusResponses())
                .build();
    }


    @GetMapping("/list-order")
    public APIResponse<PageResponse<OrderDetailResponse>> getOrderPaging(
            @RequestParam(required = false) String orderStatus,
            @RequestParam(defaultValue = "date") String sortOrder,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        PageResponse<OrderDetailResponse> pageResponse = orderDetailService.getOrderPaging(orderStatus, page, size, sortOrder);
        Map<OrderConstants.OrderStatus, Long> orderStatusCount = orderDetailService.getOrderStatusCounts();

        return APIResponse.<PageResponse<OrderDetailResponse>>builder()
                .flag(true)
                .message("OK")
                .result(pageResponse)
                .additionalData(Map.of("statusCounts", orderStatusCount))
                .build();
    }
}
