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
                return to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED || to == OrderStatus.REJECTED || to == OrderStatus.PAID;
            case CONFIRMED:
                return to == OrderStatus.PREPARING || to == OrderStatus.CANCELLED || to == OrderStatus.REJECTED;
            case PREPARING:
                return to == OrderStatus.READY_FOR_DELIVERY || to == OrderStatus.CANCELLED;
            case READY_FOR_DELIVERY:
                return to == OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY:
                return to == OrderStatus.DELIVERED;
            case PAID:
                return to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED || to == OrderStatus.REJECTED;
            case DELIVERED:
            case CANCELLED:
            case REJECTED:
            case PAYMENT_FAILED:
                return false; // Trạng thái cuối, không thể chuyển
            default:
                return false;
        }
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
