package fpl.sd.backend.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import fpl.sd.backend.constant.OrderConstants;
import fpl.sd.backend.entity.CartItem;
import fpl.sd.backend.entity.CustomerOrder;
import fpl.sd.backend.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailResponse {
    String id;
    Instant orderDate;
    Double finalTotal;
    Double originalTotal;
    OrderConstants.OrderStatus orderStatus;
    String username;
    String userId;
    String fullName;
    String email;
    String address;
    String phone;
    String paymentId;
    String bankCode;
    String cardType;
    String discountId;
    String couponName;
    List<CartItemResponse> cartItems;


    public OrderDetailResponse getOrderDetailResponse(CustomerOrder customerOrder) {
        OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
        orderDetailResponse.setOrderDate(orderDate);
        orderDetailResponse.setFinalTotal(finalTotal);
        orderDetailResponse.setOriginalTotal(originalTotal);
        orderDetailResponse.setOrderStatus(orderStatus);
        orderDetailResponse.setUserId(id);
        orderDetailResponse.setId(id);
        orderDetailResponse.setFullName(fullName);
        orderDetailResponse.setEmail(email);
        orderDetailResponse.setAddress(address);
        orderDetailResponse.setPhone(phone);
        orderDetailResponse.setBankCode(bankCode);
        orderDetailResponse.setCardType(cardType);
        orderDetailResponse.setDiscountId(discountId);
        orderDetailResponse.setCouponName(couponName);

        // Ánh xạ username từ User entity
        if (customerOrder.getUser() != null) {
            orderDetailResponse.setUsername(customerOrder.getUser().getUsername());
        }

        List<CartItemResponse> cartItemResponses = customerOrder.getOrderDetails().stream()
                .map(orderDetail -> {
                    CartItemResponse cartItemResponse = new CartItemResponse();
                    cartItemResponse.setVariantId(orderDetail.getVariant().getId());
                    cartItemResponse.setQuantity(orderDetail.getQuantity());
                    cartItemResponse.setPrice(orderDetail.getPrice());
                    cartItemResponse.setProductId(orderDetail.getVariant().getShoe().getId());
                    cartItemResponse.setProductName(orderDetail.getVariant().getShoe().getName());

                    // Map first shoe image (if available) into cart item as an absolute URL
                    try {
                        if (orderDetail.getVariant() != null && orderDetail.getVariant().getShoe() != null) {
                            List<fpl.sd.backend.entity.ShoeImage> images = orderDetail.getVariant().getShoe().getShoeImages();
                            if (images != null && !images.isEmpty()) {
                                String file = images.get(0).getUrl();
                                if (file != null && !file.isBlank()) {
                                    String imageUrl;
                                    if (file.startsWith("http://") || file.startsWith("https://")) {
                                        imageUrl = file;
                                    } else if (file.startsWith("/uploads/")) {
                                        imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(file).toUriString();
                                    } else {
                                        // assume filename stored; serve from /uploads/shoes/{filename}
                                        String filename = file.startsWith("/") ? file.substring(1) : file;
                                        imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/uploads/shoes/").path(filename).toUriString();
                                    }
                                    cartItemResponse.setImageUrl(imageUrl);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        // swallow errors mapping images — we don't want to break the whole response
                    }

                    return cartItemResponse;
                })
                .collect(Collectors.toList());
        orderDetailResponse.setCartItems(cartItemResponses);
        return orderDetailResponse;
    }
}
