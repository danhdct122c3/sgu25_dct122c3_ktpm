package fpl.sd.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fpl.sd.backend.constant.DiscountConstants;
import fpl.sd.backend.constant.ShoeConstants;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscountUpdateRequest {
    Double percentage;

    Instant startDate;

    Instant endDate;

    String code;

    String description;

    Double minimumOrderAmount;

    Double fixedAmount;

    DiscountConstants.DiscountType discountType;

    Boolean active;

    // Usage limit field
    Integer usageLimit; // null means unlimited

    // Category and product specific fields
    List<ShoeConstants.Category> categories; // Danh sách category áp dụng discount
    List<String> shoeIds; // Danh sách ID sản phẩm áp dụng discount
}
