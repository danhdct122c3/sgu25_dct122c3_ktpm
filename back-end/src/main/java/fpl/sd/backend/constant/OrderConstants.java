package fpl.sd.backend.constant;

import fpl.sd.backend.dto.response.EnumResponse;

import java.util.Arrays;
import java.util.List;

public class OrderConstants {
    public enum OrderStatus {
        CREATED, CONFIRMED, PREPARING, READY_FOR_DELIVERY, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, REJECTED, PAID, PAYMENT_FAILED
    }
    
    // Phương thức kiểm tra xem có thể chuyển từ trạng thái này sang trạng thái khác không
    public static boolean canTransitionTo(OrderStatus from, OrderStatus to) {
        switch (from) {
            case CREATED:
                return to == OrderStatus.CONFIRMED || to == OrderStatus.REJECTED || to == OrderStatus.PAID || to == OrderStatus.CANCELLED;
            case CONFIRMED:
                return to == OrderStatus.PREPARING || to == OrderStatus.REJECTED || to == OrderStatus.CANCELLED;
            case PREPARING:
                return to == OrderStatus.READY_FOR_DELIVERY || to == OrderStatus.CANCELLED;
            case READY_FOR_DELIVERY:
                return to == OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY:
                return to == OrderStatus.DELIVERED;
            case PAID:
                return to == OrderStatus.CONFIRMED || to == OrderStatus.REJECTED || to == OrderStatus.CANCELLED;
            case DELIVERED:
            case CANCELLED:
            case REJECTED:
            case PAYMENT_FAILED:
                return false; // Trạng thái cuối, không thể chuyển
            default:
                return false;
        }
    }
    
    // Phương thức kiểm tra chuyển đổi theo workflow tuần tự (từng bước một) dành cho Admin
    public static boolean canAdminTransitionToNextStep(OrderStatus from, OrderStatus to) {
        // Admin không được phép CANCELLED (chỉ khách hàng)
        if (to == OrderStatus.CANCELLED) {
            return false;
        }
        
        // Chỉ cho phép chuyển sang trạng thái tiếp theo hoặc REJECTED
        switch (from) {
            case CREATED:
                // Admin không được chuyển CREATED → PAID (chỉ VNPay callback tự động)
                // Chỉ cho phép CREATED → CONFIRMED (COD) hoặc REJECTED
                return to == OrderStatus.CONFIRMED || to == OrderStatus.REJECTED;
            case PAID:
                return to == OrderStatus.CONFIRMED || to == OrderStatus.REJECTED;
            case CONFIRMED:
                return to == OrderStatus.PREPARING || to == OrderStatus.REJECTED;
            case PREPARING:
                return to == OrderStatus.READY_FOR_DELIVERY;
            case READY_FOR_DELIVERY:
                return to == OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY:
                return to == OrderStatus.DELIVERED;
            case DELIVERED:
            case CANCELLED:
            case REJECTED:
            case PAYMENT_FAILED:
                return false; // Trạng thái cuối
            default:
                return false;
        }
    }
    
    // Phương thức kiểm tra xem có thể chuyển từ trạng thái này sang trạng thái khác không (dành cho Admin - không cho phép CANCELLED)
    public static boolean canAdminTransitionTo(OrderStatus from, OrderStatus to) {
        // Admin không được phép chuyển sang trạng thái CANCELLED
        if (to == OrderStatus.CANCELLED) {
            return false;
        }
        return canTransitionTo(from, to);
    }
    
    // Phương thức kiểm tra xem khách hàng có thể hủy đơn hàng không
    public static boolean canCustomerCancel(OrderStatus status) {
        return status == OrderStatus.CREATED || status == OrderStatus.CONFIRMED;
    }
    
    public static OrderConstants.OrderStatus getOrderStatusFromString(String orderStatusString) {
        for (OrderConstants.OrderStatus orderStatus : OrderConstants.OrderStatus.values()) {
            if (orderStatus.name().equalsIgnoreCase(orderStatusString)) {
                return orderStatus;
            }
        }
        return null;
    }
    
    public static List<EnumResponse> getAllOrderStatusResponses() {
        return Arrays.stream(OrderConstants.OrderStatus.values())
                .map(orderStatus -> new EnumResponse(orderStatus.name(), orderStatus.name().toLowerCase()))
                .toList();
    }
}
