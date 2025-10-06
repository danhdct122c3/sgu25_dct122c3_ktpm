package fpl.sd.backend.controller;

import fpl.sd.backend.dto.APIResponse;
import fpl.sd.backend.dto.response.SizeResponse;
import fpl.sd.backend.service.SizeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/shoes")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ShoeVariantController {

    SizeService sizeService;

    @GetMapping("/sizes")
    public APIResponse<List<SizeResponse>> sizes() {
        return APIResponse.<List<SizeResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(sizeService.getAllSizes())
                .build();
    }

    // @PreAuthorize("hasRole('ADMIN')")
    // @PostMapping("/sizes/init")
    // public APIResponse<String> initializeSizes() {
    //     sizeService.initializeDefaultSizes();
    //     return APIResponse.<String>builder()
    //             .flag(true)
    //             .code(200)
    //             .message("Sizes initialized successfully")
    //             .result("Default shoe sizes have been created")
    //             .build();
    // }

    /**
     * Get all variants for a specific shoe
     * Public endpoint - anyone can view variants
     */
    @GetMapping("/{shoeId}/variants")
    public APIResponse<List<fpl.sd.backend.dto.response.VariantResponse>> getVariantsByShoeId(@PathVariable int shoeId) {
        List<fpl.sd.backend.dto.response.VariantResponse> variants = sizeService.getVariantsByShoeId(shoeId);
        return APIResponse.<List<fpl.sd.backend.dto.response.VariantResponse>>builder()
                .flag(true)
                .code(200)
                .message("Successfully retrieved variants")
                .result(variants)
                .build();
    }

    /**
     * Get a specific variant by ID
     * Public endpoint - anyone can view variant details
     */
    @GetMapping("/variants/{variantId}")
    public APIResponse<fpl.sd.backend.dto.response.VariantResponse> getVariantById(@PathVariable String variantId) {
        fpl.sd.backend.dto.response.VariantResponse variant = sizeService.getVariantById(variantId);
        return APIResponse.<fpl.sd.backend.dto.response.VariantResponse>builder()
                .flag(true)
                .code(200)
                .message("Successfully retrieved variant")
                .result(variant)
                .build();
    }

    /**
     * Create a new variant for a shoe
     * Protected endpoint - ADMIN only
     */
    // @PreAuthorize("hasRole('ADMIN')")
    // @PostMapping("/{shoeId}/variants")
    // public APIResponse<fpl.sd.backend.dto.response.VariantResponse> createVariant(
    //         @PathVariable int shoeId,
    //         @RequestBody fpl.sd.backend.dto.request.VariantRequest request) {
    //     request.setShoeId(shoeId);
    //     fpl.sd.backend.dto.response.VariantResponse variant = sizeService.createVariant(request);
    //     return APIResponse.<fpl.sd.backend.dto.response.VariantResponse>builder()
    //             .flag(true)
    //             .code(201)
    //             .message("Successfully created variant")
    //             .result(variant)
    //             .build();
    // }

    /**
     * Update a variant (stock quantity, etc.)
     * Protected endpoint - ADMIN only
     */
    // @PreAuthorize("hasRole('ADMIN')")
    // @PutMapping("/variants/{variantId}")
    // public APIResponse<fpl.sd.backend.dto.response.VariantResponse> updateVariant(
    //         @PathVariable String variantId,
    //         @RequestBody fpl.sd.backend.dto.request.VariantRequest request) {
    //     request.setVariantId(variantId);
    //     fpl.sd.backend.dto.response.VariantResponse variant = sizeService.updateVariant(request);
    //     return APIResponse.<fpl.sd.backend.dto.response.VariantResponse>builder()
    //             .flag(true)
    //             .code(200)
    //             .message("Successfully updated variant")
    //             .result(variant)
    //             .build();
    // }

    /**
     * Delete a variant
     * Protected endpoint - ADMIN only
    //  */
    // @PreAuthorize("hasRole('ADMIN')")
    // @DeleteMapping("/variants/{variantId}")
    // public APIResponse<Void> deleteVariant(@PathVariable String variantId) {
    //     sizeService.deleteVariant(variantId);
    //     return APIResponse.<Void>builder()
    //             .flag(true)
    //             .code(200)
    //             .message("Successfully deleted variant")
    //             .build();
    // }
}
