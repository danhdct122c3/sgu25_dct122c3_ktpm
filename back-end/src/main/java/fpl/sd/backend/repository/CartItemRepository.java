package fpl.sd.backend.repository;

import fpl.sd.backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    List<CartItem> findByCartId(String cartId);
    
    Optional<CartItem> findByCartIdAndVariantId(String cartId, String variantId);
    
    void deleteByCartId(String cartId);
    
    void deleteByCartIdAndVariantId(String cartId, String variantId);
}
