package fpl.sd.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import fpl.sd.backend.constant.DiscountConstants;
import fpl.sd.backend.constant.ShoeConstants;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountResponse {
    Integer id;
    Double percentage;
    Instant startDate;
    Instant endDate;
    String code;
    Double minimumOrderAmount;
    String description;
    Double fixedAmount;
    DiscountConstants.DiscountType discountType;
    boolean isActive;

    // Usage limit fields
    Integer usageLimit; // null means unlimited
    Integer usedCount;  // Số lần đã sử dụng (read-only)

    // Category and product specific fields
    List<ShoeConstants.Category> categories; // Danh sách category áp dụng discount
    List<String> shoeIds; // Danh sách ID sản phẩm áp dụng discount
}
