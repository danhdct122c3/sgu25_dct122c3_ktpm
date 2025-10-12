package fpl.sd.backend.controller;

import fpl.sd.backend.dto.APIResponse;
import fpl.sd.backend.dto.request.AddToCartRequest;
import fpl.sd.backend.dto.request.UpdateCartItemRequest;
import fpl.sd.backend.dto.response.CartResponse;
import fpl.sd.backend.service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartController {
    CartService cartService;

    @GetMapping
    public APIResponse<CartResponse> getCart(Authentication authentication) {
        String username = authentication.getName();
        log.info("GET /cart - User: {}", username);
        
        CartResponse cart = cartService.getCart(username);
        return APIResponse.<CartResponse>builder()
                .result(cart)
                .build();
    }

    @PostMapping("/add")
    public APIResponse<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("POST /cart/add - User: {}, VariantId: {}, Quantity: {}", 
                username, request.getVariantId(), request.getQuantity());
        
        CartResponse cart = cartService.addToCart(username, request);
        return APIResponse.<CartResponse>builder()
                .result(cart)
                .build();
    }

    @PutMapping("/update")
    public APIResponse<CartResponse> updateCartItem(
            @Valid @RequestBody UpdateCartItemRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("PUT /cart/update - User: {}, VariantId: {}, Quantity: {}", 
                username, request.getVariantId(), request.getQuantity());
        
        CartResponse cart = cartService.updateCartItem(username, request);
        return APIResponse.<CartResponse>builder()
                .result(cart)
                .build();
    }

    @DeleteMapping("/remove/{variantId}")
    public APIResponse<CartResponse> removeFromCart(
            @PathVariable String variantId,
            Authentication authentication) {
        String username = authentication.getName();
        log.info("DELETE /cart/remove/{} - User: {}", variantId, username);
        
        CartResponse cart = cartService.removeFromCart(username, variantId);
        return APIResponse.<CartResponse>builder()
                .result(cart)
                .build();
    }

    @DeleteMapping("/clear")
    public APIResponse<Void> clearCart(Authentication authentication) {
        String username = authentication.getName();
        log.info("DELETE /cart/clear - User: {}", username);
        
        cartService.clearCart(username);
        return APIResponse.<Void>builder()
                .message("Cart cleared successfully")
                .build();
    }
}
