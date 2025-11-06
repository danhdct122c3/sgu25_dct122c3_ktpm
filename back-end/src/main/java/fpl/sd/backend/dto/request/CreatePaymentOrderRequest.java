package fpl.sd.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentOrderRequest {
    @NotNull
    Double originalTotal;
    
    @NotNull
    Double discountAmount;
    
    @NotNull
    Double finalTotal;
    
    Integer discountId;
    
    @NotNull
    String userId;
    
    @NotNull
    List<CartItemRequest> items;
    
    @NotNull
    String ipAddress;
}
