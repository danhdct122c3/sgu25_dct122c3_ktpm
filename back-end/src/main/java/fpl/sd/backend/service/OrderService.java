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
import jakarta.transaction.Transactional;
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
    DiscountValidationService discountValidationService;


    public OrderResponse createOrder(OrderRequest request) {
        return createOrder(request, true);
    }

    public OrderResponse createOrder(OrderRequest request, boolean deductInventory) {
        
        // Validate và tính toán lại tổng tiền từ items
        double calculatedTotal = request.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        
        // Kiểm tra tổng tiền phải > 0
        if (calculatedTotal <= 0) {
            throw new AppException(ErrorCode.INVALID_ORDER_TOTAL);
        }
        
        // Cập nhật lại originalTotal nếu không đúng
        if (request.getOriginalTotal() != calculatedTotal) {
            log.warn("Original total mismatch. Expected: {}, Received: {}", calculatedTotal, request.getOriginalTotal());
            request.setOriginalTotal(calculatedTotal);
        }
        
        // Tính toán finalTotal bao gồm thuế và phí vận chuyển
        // finalTotal từ frontend đã bao gồm: originalPrice - discountAmount + tax + storePickup
        // Chúng ta sẽ sử dụng finalTotal từ frontend để đảm bảo tính nhất quán
        log.info("Using finalTotal from frontend: {}", request.getFinalTotal());

        CustomerOrder newOrder = orderMapper.toCustomerOrder(request);
        newOrder.setOrderDate(Instant.now());
        newOrder.setOrderStatus(OrderConstants.OrderStatus.CREATED);

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

                    // Kiểm tra tồn kho
                    int newQuantity = variant.getStockQuantity() - cartItem.getQuantity();
                    if (newQuantity < 0) {
                        throw new AppException(ErrorCode.INSUFFICIENT_INVENTORY);
                    }

                    // CHỈ TRỪ TỒN KHO NẾU deductInventory = true (cho COD)
                    // Với VNPay, sẽ trừ sau khi thanh toán thành công
                    if (deductInventory) {
                        variant.setStockQuantity(newQuantity);
                        shoeVariantRepository.save(variant);
                        log.info("Deducted inventory for variant {}: {} -> {}", 
                                variantId, variant.getStockQuantity() + cartItem.getQuantity(), newQuantity);
                    }

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
        
        // Kiểm tra và áp dụng discount nếu có
        // CHỈ TĂNG usedCount NẾU deductInventory = true (cho COD)
        // Với VNPay, sẽ tăng sau khi thanh toán thành công
        if (savedOrder.getDiscount() != null) {
            Discount discount = savedOrder.getDiscount();
            
            // Kiểm tra xem discount có áp dụng được cho order này không
            if (!discountValidationService.isDiscountApplicableToOrder(discount, orderDetails)) {
                throw new AppException(ErrorCode.COUPON_INVALID);
            }
            
            if (deductInventory) {
                // Tăng usedCount khi discount được áp dụng thành công
                discount.incrementUsedCount();
                discountRepository.save(discount);
                log.info("Incremented discount usedCount for discount {}: {}", 
                        discount.getId(), discount.getUsedCount());
            }
        }
        
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

    public ApplyDiscountResponse applyDiscount(String code, Double orderAmount) {
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

        // Kiểm tra giới hạn lượt sử dụng
        if (discount.isUsageLimitReached()) {
            throw new AppException(ErrorCode.COUPON_INVALID);
        }

        // Kiểm tra minimum order amount
        if (orderAmount != null && orderAmount < discount.getMinimumOrderAmount()) {
            throw new AppException(ErrorCode.MINIMUM_AMOUNT_NOT_MET);
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

    public CustomerOrder updateStatus(String id, OrderConstants.OrderStatus newStatus, PaymentDetail paymentDetail) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Kiểm tra xem có thể chuyển trạng thái không
        if (!OrderConstants.canTransitionTo(order.getOrderStatus(), newStatus)) {
            log.warn("Cannot transition order {} from {} to {}", id, order.getOrderStatus(), newStatus);
            throw new AppException(ErrorCode.ORDER_STATUS_TRANSITION_INVALID);
        }

        log.info("Updating order {} status from {} to {}", id, order.getOrderStatus(), newStatus);
        
        // Nếu chuyển sang PAID, trừ tồn kho và tăng discount usedCount
        if (newStatus == OrderConstants.OrderStatus.PAID && order.getOrderStatus() == OrderConstants.OrderStatus.CREATED) {
            deductInventoryForOrder(order);
            incrementDiscountUsageForOrder(order);
        }
        
        order.setOrderStatus(newStatus);
        order.setPaymentDetail(paymentDetail);
        order.setUpdateDate(Instant.now());

        return orderRepository.save(order);
    }
    
    /**
     * Trừ tồn kho cho đơn hàng (dùng khi thanh toán VNPay thành công)
     */
    @Transactional
    public void deductInventoryForOrder(CustomerOrder order) {
        log.info("Deducting inventory for order: {}", order.getId());
        
        List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailsByOrderId(order.getId());
        
        for (OrderDetail detail : orderDetails) {
            ShoeVariant variant = detail.getVariant();
            int quantity = detail.getQuantity();
            int newQuantity = variant.getStockQuantity() - quantity;
            
            if (newQuantity < 0) {
                log.error("Insufficient inventory for variant {}: requested {}, available {}", 
                         variant.getId(), quantity, variant.getStockQuantity());
                throw new AppException(ErrorCode.INSUFFICIENT_INVENTORY);
            }
            
            variant.setStockQuantity(newQuantity);
            shoeVariantRepository.save(variant);
            
            log.info("Deducted inventory for variant {}: {} -> {}", 
                    variant.getId(), variant.getStockQuantity() + quantity, newQuantity);
        }
    }
    
    /**
     * Tăng usedCount của discount (dùng khi thanh toán VNPay thành công)
     */
    @Transactional
    public void incrementDiscountUsageForOrder(CustomerOrder order) {
        if (order.getDiscount() != null) {
            Discount discount = order.getDiscount();
            discount.incrementUsedCount();
            discountRepository.save(discount);
            
            log.info("Incremented discount usedCount for order {}, discount {}: {}", 
                    order.getId(), discount.getId(), discount.getUsedCount());
        }
    }

    /**
     * Cancel an order
     * Only allows cancellation if order status is CREATED or CONFIRMED
     * User can only cancel their own orders (checked via authentication context)
     */
    public OrderResponse cancelOrder(String orderId) {
        log.info("=== CANCELING ORDER {} ===", orderId);
        
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Check if order can be cancelled using the new business logic
        if (!OrderConstants.canCustomerCancel(order.getOrderStatus())) {
            log.warn("Cannot cancel order {}. Current status: {}", orderId, order.getOrderStatus());
            throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
        }

        // Update order status to CANCELLED
        order.setOrderStatus(OrderConstants.OrderStatus.CANCELLED);
        order.setUpdateDate(Instant.now());
        
        log.info("Order {} status changed to CANCELLED", orderId);

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
        log.info("=== ORDER {} CANCELLED SUCCESSFULLY ===", orderId);
        
        return orderMapper.toOrderResponse(savedOrder);
    }
}
