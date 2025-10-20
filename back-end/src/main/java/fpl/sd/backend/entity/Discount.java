package fpl.sd.backend.entity;

import fpl.sd.backend.constant.DiscountConstants;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = true)
    Double percentage;

    @Column(nullable = false)
    Instant startDate;

    @Column(nullable = false)
    Instant endDate;

    @Column(nullable = false)
    String code;

    @Builder.Default
    Double minimumOrderAmount = 0.0;

    @Column(nullable = false)
    String description;

    @Column(nullable = true)
    Double fixedAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    DiscountConstants.DiscountType discountType;

    @Column(nullable = false)
    @Builder.Default
    boolean isActive = true;

    // Usage limit fields
    @Column(nullable = true)
    Integer usageLimit; // null means unlimited

    @Column(nullable = false)
    @Builder.Default
    Integer usedCount = 0;

    @OneToMany(mappedBy = "discount")
    List<CustomerOrder> customerOrders = new ArrayList<>();

    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<DiscountCategory> discountCategories = new ArrayList<>();

    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<DiscountShoe> discountShoes = new ArrayList<>();

    // Phương thức giúp kiểm tra và điều chỉnh giá trị khi cập nhật Discount
    public void setDiscountValues() {
        if (this.discountType == DiscountConstants.DiscountType.FIXED_AMOUNT) {
            this.percentage = null; // Disable percentage
        } else if (this.discountType == DiscountConstants.DiscountType.PERCENTAGE) {
            this.fixedAmount = null; // Disable fixedAmount
            // Không set minimumOrderAmount = 0.0 vì cả PERCENTAGE và FIXED_AMOUNT đều có thể có minimum amount
        }
    }

    // Helper methods for usage limit
    public boolean isUsageLimitReached() {
        return usageLimit != null && usedCount >= usageLimit;
    }

    public void incrementUsedCount() {
        this.usedCount++;
    }
}
