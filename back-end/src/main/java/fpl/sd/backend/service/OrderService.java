package fpl.sd.backend.service;

import fpl.sd.backend.constant.DiscountConstants;
import fpl.sd.backend.constant.OrderConstants;
import fpl.sd.backend.dto.request.OrderRequest;
import fpl.sd.backend.dto.response.ApplyDiscountResponse;
import fpl.sd.backend.dto.response.CartItemResponse;
import fpl.sd.backend.dto.response.OrderResponse;
import fpl.sd.backend.entity.*;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.mapper.OrderMapper;
import fpl.sd.backend.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {

    OrderMapper orderMapper;
    UserRepository userRepository;
    ShoeVariantRepository shoeVariantRepository;
    CustomerOrderRepository orderRepository;
    OrderDetailRepository orderDetailRepository;
    DiscountRepository discountRepository;


    public OrderResponse createOrder(OrderRequest request) {

        CustomerOrder newOrder = orderMapper.toCustomerOrder(request);
        newOrder.setOrderDate(Instant.now());
        newOrder.setOrderStatus(OrderConstants.OrderStatus.PENDING);

        if (request.getDiscountId() != null) {

            Discount discount = discountRepository.findById(request.getDiscountId())
                    .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

            newOrder.setDiscount(discount);
        }

        User userOrder = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        newOrder.setUser(userOrder);

        CustomerOrder savedOrder = orderRepository.save(newOrder);
        CustomerOrder finalSavedOrder = savedOrder;

        if (finalSavedOrder.getId() == null) {
            throw new AppException(ErrorCode.ORDER_SAVE_ERROR);
        }


        List<OrderDetail> orderDetails = request.getItems().stream()
                .map(cartItem -> {
                    String variantId = cartItem.getVariantId();
                    ShoeVariant variant = shoeVariantRepository.findById(variantId)
                            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

                    int newQuantity = variant.getStockQuantity() - cartItem.getQuantity();
                    if (newQuantity < 0) {
                        throw new AppException(ErrorCode.INSUFFICIENT_INVENTORY);
                    }

                    variant.setStockQuantity(newQuantity);
                    shoeVariantRepository.save(variant);

                    OrderDetailId orderDetailId = new OrderDetailId(finalSavedOrder.getId(), variantId);

                    OrderDetail orderDetail = OrderDetail.builder()
                            .id(orderDetailId)
                            .order(finalSavedOrder)
                            .variant(variant)
                            .price(cartItem.getPrice())
                            .quantity(cartItem.getQuantity())
                            .build();
                    return orderDetailRepository.save(orderDetail);
                }).collect(Collectors.toList());


        savedOrder.setOrderDetails(orderDetails);
        savedOrder = orderRepository.save(savedOrder);

        List<CartItemResponse> cartItemsResponse = savedOrder.getOrderDetails().stream()
                .map(item -> CartItemResponse.builder()
                        .quantity(item.getQuantity())
                        .productId(item.getVariant().getShoe().getId())
                        .price(item.getPrice())
                        .variantId(item.getVariant().getId())
                        .build())
                .toList();


        OrderResponse orderResponse = orderMapper.toOrderResponse(savedOrder);

        orderResponse.setItems(cartItemsResponse);

        return orderResponse;
    }

    public boolean couponIsExpired(Discount discount) {
        Date currentdate = new Date();
        Date expirationDate = Date.from(discount.getEndDate());
        return currentdate.after(expirationDate);
    }

    public ApplyDiscountResponse applyDiscount(String code) {
        // Validate input
        if (code == null || code.trim().isEmpty()) {
            throw new AppException(ErrorCode.COUPON_INVALID);
        }

        Discount discount = discountRepository.findByCode(code.trim())
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_INVALID));

        // Kiểm tra nếu mã giảm giá không hoạt động
        if (!discount.isActive()) {
            throw new AppException(ErrorCode.COUPON_INVALID);
        }

        // Kiểm tra nếu mã giảm giá đã hết hạn
        if (couponIsExpired(discount)) {
            throw new AppException(ErrorCode.COUPON_INVALID);
        }


        ApplyDiscountResponse response = new ApplyDiscountResponse();
        response.setId(discount.getId());
        response.setCoupon(discount.getCode());
        response.setActive(discount.isActive());
        response.setMinimumOrderAmount(discount.getMinimumOrderAmount());
        if (discount.getDiscountType() == DiscountConstants.DiscountType.PERCENTAGE) {
            response.setPercentage(discount.getPercentage());
            response.setDiscountType(DiscountConstants.DiscountType.PERCENTAGE);
        } else if (discount.getDiscountType() == DiscountConstants.DiscountType.FIXED_AMOUNT) {
            response.setFixedAmount(discount.getFixedAmount());
            response.setDiscountType(DiscountConstants.DiscountType.FIXED_AMOUNT);
        }

        return response;
    }

    public CustomerOrder getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }

    public CustomerOrder updateStatus(String id, OrderConstants.OrderStatus status, PaymentDetail paymentDetail) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        order.setOrderStatus(status);
        order.setPaymentDetail(paymentDetail);
        order.setUpdateDate(Instant.now());

        return orderRepository.save(order);
    }

    /**
     * Cancel an order
     * Only allows cancellation if order status is PENDING or RECEIVED
     * User can only cancel their own orders (checked via authentication context)
     */
    public OrderResponse cancelOrder(String orderId) {
        log.info("=== CANCELING ORDER {} ===", orderId);
        
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Check if order can be cancelled (only PENDING or RECEIVED status)
        if (order.getOrderStatus() != OrderConstants.OrderStatus.PENDING
                && order.getOrderStatus() != OrderConstants.OrderStatus.RECEIVED) {
            log.warn("Cannot cancel order {}. Current status: {}", orderId, order.getOrderStatus());
            throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
        }

        // Update order status to CANCELED
        order.setOrderStatus(OrderConstants.OrderStatus.CANCELED);
        order.setUpdateDate(Instant.now());
        
        log.info("Order {} status changed to CANCELED", orderId);

        // Restore inventory for all order items
        List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailsByOrderId(orderId);
        log.info("Found {} order details to restore inventory", orderDetails.size());
        
        for (OrderDetail detail : orderDetails) {
            ShoeVariant variant = detail.getVariant();
            int oldStock = variant.getStockQuantity();
            int quantity = detail.getQuantity();
            int newStock = oldStock + quantity;
            
            log.info("Restoring inventory for variant {}: {} + {} = {}", 
                    variant.getId(), oldStock, quantity, newStock);
            
            variant.setStockQuantity(newStock);
            shoeVariantRepository.save(variant);
            
            log.info("Successfully restored stock for variant {}", variant.getId());
        }

        CustomerOrder savedOrder = orderRepository.save(order);
        log.info("=== ORDER {} CANCELED SUCCESSFULLY ===", orderId);
        
        return orderMapper.toOrderResponse(savedOrder);
    }
}
