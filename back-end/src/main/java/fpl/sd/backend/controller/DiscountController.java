package fpl.sd.backend.controller;

import fpl.sd.backend.constant.DiscountConstants;
import fpl.sd.backend.dto.APIResponse;
import fpl.sd.backend.dto.PageResponse;
import fpl.sd.backend.dto.request.DiscountCreateRequest;
import fpl.sd.backend.dto.request.DiscountUpdateRequest;
import fpl.sd.backend.dto.response.DiscountResponse;
import fpl.sd.backend.dto.response.EnumResponse;
import fpl.sd.backend.service.DiscountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor

//@CrossOrigin(origins = "http://localhost:5173")

@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class DiscountController {
    DiscountService discountService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public APIResponse<DiscountResponse> addDiscount(@RequestBody @Valid DiscountCreateRequest discount) {
        return APIResponse.<DiscountResponse>builder()
                .flag(true)
                .code(200)
                .message("Successfully added discount")
                .result(discountService.createDiscount(discount))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public APIResponse<List<DiscountResponse>> GetAllDiscounts() {
        return APIResponse.<List<DiscountResponse>>builder()
                .flag(true)
                .code(200)
                .message("Successfully loaded")
                .result(discountService.getAllDiscounts())
                .build();
    }

    @GetMapping("/{id}")
    public APIResponse<DiscountResponse> GetDiscountByID(@PathVariable Integer id) {
        return APIResponse.<DiscountResponse>builder()
                .flag(true)
                .code(200)
                .message("Successfully")
                .result(discountService.getDiscountById(id))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public APIResponse<DiscountResponse> updateDiscount(@PathVariable Integer id, @RequestBody @Valid DiscountUpdateRequest request) {
        DiscountResponse discountResponse = discountService.updateDiscount(id, request);
        return APIResponse.<DiscountResponse>builder()
                .flag(true)
                .code(200)
                .message("Discount updated successfully")
                .result(discountResponse)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/isActive")
    public APIResponse<List<DiscountResponse>> getDiscountByIsActive(@RequestParam(value = "isActive", required = false) boolean isActive) {
        return APIResponse.<List<DiscountResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(discountService.getDiscountByIsActive(isActive))
                .build();
    }

    @GetMapping("/discountType")
    public APIResponse<List<EnumResponse>> getDiscountByDiscountType() {
        return APIResponse.<List<EnumResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(DiscountConstants.getAllDiscountTypeResponses())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list-discount")
    public APIResponse<PageResponse<DiscountResponse>> getDiscountPaging(
            @RequestParam(required = false) String discountType,
            @RequestParam(required = false) String code,
//            @RequestParam(required = false) boolean isActive,
            @RequestParam(required = false) Boolean isActive,  // Thay đổi từ boolean sang Boolean
            @RequestParam(defaultValue = "date") String sortOrder,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        return APIResponse.<PageResponse<DiscountResponse>>builder()
                .flag(true)
                .message("OK")
                .result(discountService.getDiscountPaging( discountType, code, isActive, page, size, sortOrder))
                .build();
    }

    /**
     * Delete a discount
     * Protected endpoint - ADMIN only
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public APIResponse<Void> deleteDiscount(@PathVariable Integer id) {
        discountService.deleteDiscount(id);
        return APIResponse.<Void>builder()
                .flag(true)
                .code(200)
                .message("Successfully deleted discount")
                .build();
    }
}
