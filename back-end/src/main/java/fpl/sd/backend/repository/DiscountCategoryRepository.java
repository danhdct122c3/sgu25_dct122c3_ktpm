package fpl.sd.backend.repository;

import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.entity.Discount;
import fpl.sd.backend.entity.DiscountCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DiscountCategoryRepository extends JpaRepository<DiscountCategory, Integer> {
    List<DiscountCategory> findByDiscount(Discount discount);
    List<DiscountCategory> findByCategory(ShoeConstants.Category category);
    List<DiscountCategory> findByDiscountAndCategory(Discount discount, ShoeConstants.Category category);
    boolean existsByDiscountAndCategory(Discount discount, ShoeConstants.Category category);
    @Transactional
    void deleteByDiscount(Discount discount);
}