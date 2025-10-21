package fpl.sd.backend.repository;

import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.entity.Brand;
import fpl.sd.backend.entity.Shoe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoeRepository extends JpaRepository<Shoe, Integer> {
    List<Shoe> findShoesByGender(ShoeConstants.Gender gender);
    List<Shoe> findShoesByBrand(Brand brand);
    List<Shoe> findShoesByCategory(ShoeConstants.Category category);

    List<Shoe> findShoesByNameContainingIgnoreCase(String shoeName);
    
    // Query methods để lọc theo status (true = active, false = deleted/hidden)
    List<Shoe> findByStatusTrue(); // Chỉ lấy shoes active
    List<Shoe> findByStatusTrueOrderByCreatedAtDesc(); // Shoes active, sắp xếp theo ngày tạo
    List<Shoe> findByStatusTrueAndGender(ShoeConstants.Gender gender);
    List<Shoe> findByStatusTrueAndBrand(Brand brand);
    List<Shoe> findByStatusTrueAndCategory(ShoeConstants.Category category);
    List<Shoe> findByStatusTrueAndNameContainingIgnoreCase(String shoeName);


    @Query("""
    SELECT s FROM Shoe s 
    JOIN s.brand b 
    WHERE (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))
    AND (:minPrice IS NULL OR s.price >= :minPrice)
    AND (:maxPrice IS NULL OR s.price <= :maxPrice)
    AND (:brandId IS NULL OR b.id = :brandId)
    AND (:gender IS NULL OR s.gender = :gender)
    AND (:category IS NULL OR s.category = :category)
    AND s.status = COALESCE(:status, true)
    """)
    Page<Shoe> findShoesByFilters(@Param("name") String name,
                                @Param("minPrice") Long minPrice,
                                @Param("maxPrice") Long maxPrice,
                                @Param("brandId") Integer brandId,
                                @Param("gender") ShoeConstants.Gender gender,
                                @Param("category") ShoeConstants.Category category,
                                @Param("status") Boolean status,
                                Pageable pageable
                                );
}
