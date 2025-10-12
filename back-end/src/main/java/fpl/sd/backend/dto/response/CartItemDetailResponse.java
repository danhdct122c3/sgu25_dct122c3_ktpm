package fpl.sd.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemDetailResponse {
    String cartItemId;
    String variantId;
    String productId;
    String productName;
    String imageUrl;
    double price;
    int quantity;
    double totalPrice;
    String size;
    String color;
    int stockQuantity;
}
