package fpl.sd.backend.service;

import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.entity.*;
import fpl.sd.backend.repository.DiscountCategoryRepository;
import fpl.sd.backend.repository.DiscountShoeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DiscountValidationService {
    
    DiscountCategoryRepository discountCategoryRepository;
    DiscountShoeRepository discountShoeRepository;

    /**
     * Kiểm tra xem discount có áp dụng được cho order không
     * Dựa trên category và sản phẩm cụ thể
     */
    public boolean isDiscountApplicableToOrder(Discount discount, List<OrderDetail> orderDetails) {
        // Nếu discount không có ràng buộc category hoặc shoe cụ thể, áp dụng cho tất cả
        List<DiscountCategory> discountCategories = discountCategoryRepository.findByDiscount(discount);
        List<DiscountShoe> discountShoes = discountShoeRepository.findByDiscount(discount);
        
        if (discountCategories.isEmpty() && discountShoes.isEmpty()) {
            log.info("Discount {} applies to all products", discount.getCode());
            return true;
        }

        // Kiểm tra từng order detail
        for (OrderDetail orderDetail : orderDetails) {
            Shoe shoe = orderDetail.getVariant().getShoe();
            
            // Kiểm tra xem sản phẩm có trong danh sách sản phẩm được áp dụng không
            if (isShoeEligibleForDiscount(discount, shoe, discountCategories, discountShoes)) {
                log.info("Discount {} is applicable to shoe {}", discount.getCode(), shoe.getName());
                return true;
            }
        }
        
        log.info("Discount {} is not applicable to any products in the order", discount.getCode());
        return false;
    }

    /**
     * Kiểm tra xem một sản phẩm có đủ điều kiện áp dụng discount không
     */
    private boolean isShoeEligibleForDiscount(Discount discount, Shoe shoe, 
                                            List<DiscountCategory> discountCategories, 
                                            List<DiscountShoe> discountShoes) {
        
        // Kiểm tra sản phẩm cụ thể
        for (DiscountShoe discountShoe : discountShoes) {
            if (discountShoe.getShoe().getId() == shoe.getId()) {
                return true;
            }
        }
        
        // Kiểm tra category
        for (DiscountCategory discountCategory : discountCategories) {
            if (discountCategory.getCategory() == shoe.getCategory()) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Tính toán số tiền giảm giá cho order
     * Chỉ áp dụng cho những sản phẩm đủ điều kiện
     */
    public double calculateDiscountAmount(Discount discount, List<OrderDetail> orderDetails) {
        if (!isDiscountApplicableToOrder(discount, orderDetails)) {
            return 0.0;
        }

        List<DiscountCategory> discountCategories = discountCategoryRepository.findByDiscount(discount);
        List<DiscountShoe> discountShoes = discountShoeRepository.findByDiscount(discount);
        
        double eligibleAmount = 0.0;
        
        // Nếu không có ràng buộc, áp dụng cho toàn bộ order
        if (discountCategories.isEmpty() && discountShoes.isEmpty()) {
            eligibleAmount = orderDetails.stream()
                    .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                    .sum();
        } else {
            // Chỉ tính những sản phẩm đủ điều kiện
            for (OrderDetail orderDetail : orderDetails) {
                Shoe shoe = orderDetail.getVariant().getShoe();
                if (isShoeEligibleForDiscount(discount, shoe, discountCategories, discountShoes)) {
                    eligibleAmount += orderDetail.getPrice() * orderDetail.getQuantity();
                }
            }
        }

        // Tính toán discount amount
        double discountAmount = 0.0;
        if (discount.getDiscountType() == fpl.sd.backend.constant.DiscountConstants.DiscountType.PERCENTAGE) {
            discountAmount = eligibleAmount * (discount.getPercentage() / 100.0);
        } else if (discount.getDiscountType() == fpl.sd.backend.constant.DiscountConstants.DiscountType.FIXED_AMOUNT) {
            discountAmount = discount.getFixedAmount();
        }

        log.info("Calculated discount amount: {} for discount code: {}", discountAmount, discount.getCode());
        return discountAmount;
    }
}