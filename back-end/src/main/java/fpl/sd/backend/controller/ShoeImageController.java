package fpl.sd.backend.controller;

import fpl.sd.backend.dto.APIResponse;
import fpl.sd.backend.dto.request.ImageRequest;
import fpl.sd.backend.dto.response.ImageResponse;
import fpl.sd.backend.service.ShoeImageService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing shoe images
 * Public endpoints: GET operations for browsing images
 * Protected endpoints: POST, DELETE operations (ADMIN only)
 */
@RestController
@RequestMapping("/shoe-images")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ShoeImageController {
    ShoeImageService shoeImageService;

    /**
     * Get all images for a specific shoe
     * Public endpoint - anyone can view shoe images
     */
    @GetMapping("/shoe/{shoeId}")
    public APIResponse<List<ImageResponse>> getImagesByShoeId(@PathVariable int shoeId) {
        List<ImageResponse> images = shoeImageService.getShoeImagesByShoeId(shoeId);
        return APIResponse.<List<ImageResponse>>builder()
                .flag(true)
                .code(200)
                .message("Successfully retrieved images for shoe ID " + shoeId)
                .result(images)
                .build();
    }

    /**
     * Get a specific image by ID
     * Public endpoint - anyone can view shoe images
     */
    @GetMapping("/{id}")
    public APIResponse<ImageResponse> getImageById(@PathVariable int id) {
        ImageResponse image = shoeImageService.getImageById(id);
        return APIResponse.<ImageResponse>builder()
                .flag(true)
                .code(200)
                .message("Successfully retrieved image")
                .result(image)
                .build();
    }

    /**
     * Add a new image to a shoe
     * Protected endpoint - ADMIN only
     */
    // @PreAuthorize("hasRole('ADMIN')")
    // @PostMapping("/shoe/{shoeId}")
    // public APIResponse<ImageResponse> addImageToShoe(
    //         @PathVariable int shoeId,
    //         @RequestBody @Valid ImageRequest request) {
    //     ImageResponse image = shoeImageService.addImageToShoe(shoeId, request);
    //     return APIResponse.<ImageResponse>builder()
    //             .flag(true)
    //             .code(201)
    //             .message("Successfully added image to shoe")
    //             .result(image)
    //             .build();
    // }

    /**
     * Update an existing image
     * Protected endpoint - ADMIN only
     */
    // @PreAuthorize("hasRole('ADMIN')")
    // @PutMapping("/{id}")
    // public APIResponse<ImageResponse> updateImage(
    //         @PathVariable int id,
    //         @RequestBody @Valid ImageRequest request) {
    //     ImageResponse image = shoeImageService.updateImage(id, request);
    //     return APIResponse.<ImageResponse>builder()
    //             .flag(true)
    //             .code(200)
    //             .message("Successfully updated image")
    //             .result(image)
    //             .build();
    // }

    /**
     * Delete an image
     * Protected endpoint - ADMIN only
     */
    // @PreAuthorize("hasRole('ADMIN')")
    // @DeleteMapping("/{id}")
    // public APIResponse<Void> deleteImage(@PathVariable int id) {
    //     shoeImageService.deleteImage(id);
    //     return APIResponse.<Void>builder()
    //             .flag(true)
    //             .code(200)
    //             .message("Successfully deleted image")
    //             .build();
    // }

    /**
     * Delete all images for a specific shoe
     * Protected endpoint - ADMIN only
     */
    // @PreAuthorize("hasRole('ADMIN')")
    // @DeleteMapping("/shoe/{shoeId}")
    // public APIResponse<Void> deleteAllImagesByShoeId(@PathVariable int shoeId) {
    //     shoeImageService.deleteAllImagesByShoeId(shoeId);
    //     return APIResponse.<Void>builder()
    //             .flag(true)
    //             .code(200)
    //             .message("Successfully deleted all images for shoe ID " + shoeId)
    //             .build();
    // }
}
