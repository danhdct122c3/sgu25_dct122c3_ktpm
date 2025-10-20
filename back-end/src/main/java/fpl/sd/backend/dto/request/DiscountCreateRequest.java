package fpl.sd.backend.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fpl.sd.backend.constant.DiscountConstants;
import fpl.sd.backend.constant.ShoeConstants;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscountCreateRequest {
    @DecimalMin(value = "0.0",inclusive = false, message = "Percentage must be greater than 0")
    @DecimalMax(value = "100.0", message = "Percentage cannot exceed 100")
    Double percentage;

    @NotNull(message = "Start date is mandatory")
    Instant startDate;

    @NotNull(message = "End date is mandatory")
    Instant endDate;

    @NotBlank(message = "Discount code is mandatory")
    String code;

    Double minimumOrderAmount;  // Giữ giá trị mặc định là 0

    @NotBlank(message = "Description is mandatory")
    String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "FixedAmount must be greater than 0")
    Double fixedAmount;

    @NotNull(message = "DiscountType is mandatory")
    DiscountConstants.DiscountType discountType;

    // Usage limit field
    @Min(value = 1, message = "Usage limit must be at least 1")
    Integer usageLimit; // null means unlimited

    // Category and product specific fields
    List<ShoeConstants.Category> categories; // Danh sách category áp dụng discount
    List<String> shoeIds; // Danh sách ID sản phẩm áp dụng discount
}
