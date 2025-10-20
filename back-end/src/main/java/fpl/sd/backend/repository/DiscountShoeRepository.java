package fpl.sd.backend.repository;

import fpl.sd.backend.entity.Discount;
import fpl.sd.backend.entity.DiscountShoe;
import fpl.sd.backend.entity.Shoe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountShoeRepository extends JpaRepository<DiscountShoe, Integer> {
    List<DiscountShoe> findByDiscount(Discount discount);
    List<DiscountShoe> findByShoe(Shoe shoe);
    List<DiscountShoe> findByDiscountAndShoe(Discount discount, Shoe shoe);
    boolean existsByDiscountAndShoe(Discount discount, Shoe shoe);
}