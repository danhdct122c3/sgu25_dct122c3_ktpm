package fpl.sd.backend.service;

import fpl.sd.backend.dto.request.AddToCartRequest;
import fpl.sd.backend.dto.request.UpdateCartItemRequest;
import fpl.sd.backend.dto.response.CartItemDetailResponse;
import fpl.sd.backend.dto.response.CartResponse;
import fpl.sd.backend.entity.Cart;
import fpl.sd.backend.entity.CartItem;
import fpl.sd.backend.entity.ShoeVariant;
import fpl.sd.backend.entity.User;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.repository.CartItemRepository;
import fpl.sd.backend.repository.CartRepository;
import fpl.sd.backend.repository.ShoeVariantRepository;
import fpl.sd.backend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartService {
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    ShoeVariantRepository shoeVariantRepository;

    @Transactional
    public CartResponse addToCart(String username, AddToCartRequest request) {
        log.info("Adding item to cart for user: {}", username);

        // Get or create cart for user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });

        // Get variant and validate stock
        ShoeVariant variant = shoeVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

        // Check if item already exists in cart
        CartItem cartItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId())
                .orElse(null);

        if (cartItem != null) {
            // Update quantity
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            if (newQuantity > variant.getStockQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }
            cartItem.setQuantity(newQuantity);
        } else {
            // Create new cart item
            if (request.getQuantity() > variant.getStockQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }
            cartItem = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(request.getQuantity())
                    .build();
            cart.addItem(cartItem);
        }

        cartItemRepository.save(cartItem);
        return getCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String username) {
        log.info("Getting cart for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    // Return empty cart if doesn't exist
                    return Cart.builder()
                            .user(user)
                            .build();
                });

        return getCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(String username, UpdateCartItemRequest request) {
        log.info("Updating cart item for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        CartItem cartItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), request.getVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        // Validate stock
        if (request.getQuantity() > cartItem.getVariant().getStockQuantity()) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return getCartResponse(cart);
    }

    @Transactional
    public CartResponse removeFromCart(String username, String variantId) {
        log.info("Removing item from cart for user: {}, variantId: {}", username, variantId);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        CartItem cartItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);

        return getCartResponse(cart);
    }

    @Transactional
    public void clearCart(String username) {
        log.info("Clearing cart for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElse(null);

        if (cart != null) {
            cartItemRepository.deleteByCartId(cart.getId());
            cart.getItems().clear();
        }
    }

    private CartResponse getCartResponse(Cart cart) {
        List<CartItemDetailResponse> items = cart.getItems().stream()
                .map(this::mapToCartItemDetailResponse)
                .collect(Collectors.toList());

        double totalPrice = items.stream()
                .mapToDouble(CartItemDetailResponse::getTotalPrice)
                .sum();

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalQuantity(cart.getTotalQuantity())
                .totalPrice(totalPrice)
                .build();
    }

    private CartItemDetailResponse mapToCartItemDetailResponse(CartItem cartItem) {
        ShoeVariant variant = cartItem.getVariant();
        String imageUrl = variant.getShoe().getShoeImages().isEmpty() ? 
                "" : variant.getShoe().getShoeImages().get(0).getUrl();

        return CartItemDetailResponse.builder()
                .cartItemId(cartItem.getId())
                .variantId(variant.getId())
                .productId(String.valueOf(variant.getShoe().getId()))
                .productName(variant.getShoe().getName())
                .imageUrl(imageUrl)
                .price(variant.getShoe().getPrice())
                .quantity(cartItem.getQuantity())
                .totalPrice(variant.getShoe().getPrice() * cartItem.getQuantity())
                .size(String.valueOf(variant.getSizeChart().getSizeNumber()))
                .color("") // ShoeVariant doesn't have color field
                .stockQuantity(variant.getStockQuantity())
                .build();
    }
}
