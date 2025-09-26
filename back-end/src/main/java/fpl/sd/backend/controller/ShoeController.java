package fpl.sd.backend.controller;

import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.dto.APIResponse;
import fpl.sd.backend.dto.PageResponse;
import fpl.sd.backend.dto.request.ShoeCreateRequest;
import fpl.sd.backend.dto.request.ShoeUpdateRequest;
import fpl.sd.backend.dto.response.EnumResponse;
import fpl.sd.backend.dto.response.ShoeResponse;
import fpl.sd.backend.service.ShoeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/shoes")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ShoeController {
    ShoeService shoeService;

    @GetMapping
    public APIResponse<List<ShoeResponse>> getAllShoes() {
        return APIResponse.<List<ShoeResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(shoeService.getAllShoes())
                .build();
    }

    @GetMapping("/by-gender")
    public APIResponse<List<ShoeResponse>> getShoesByGender(@RequestParam(value = "gender", required = false) String gender) {
        List<ShoeResponse> shoes = shoeService.getShoesByGender(gender);
        return APIResponse.<List<ShoeResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(shoes)
                .build();
    }

    @GetMapping("/by-brand")
    public APIResponse<List<ShoeResponse>> getShoesByBrand(@RequestParam(value = "brand", required = false) Integer brand) {
        return APIResponse.<List<ShoeResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(shoeService.getShoesByBrand(brand))
                .build();
    }

    @GetMapping("/by-category")
    public APIResponse<List<ShoeResponse>> getShoesByCategory(@RequestParam(value = "category", required = false) String category) {
        return APIResponse.<List<ShoeResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(shoeService.getShoesByCategory(category))
                .build();
    }

    @PostMapping
    public APIResponse<ShoeResponse> createShoe(@RequestBody @Valid ShoeCreateRequest request) {
        return APIResponse.<ShoeResponse>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(shoeService.createShoe(request))
                .build();
    }

    @GetMapping("/{id}")
    public APIResponse<ShoeResponse> getShoeById(@PathVariable("id") int id) {
        return APIResponse.<ShoeResponse>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(shoeService.getShoeById(id))
                .build();

    }

    @PutMapping("/{id}")
    public APIResponse<ShoeResponse> updateShoe(@PathVariable("id") int id, @RequestBody ShoeUpdateRequest request) {
        return APIResponse.<ShoeResponse>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(shoeService.updateShoe(request, id))
                .build();
    }

    @GetMapping("/shop")
    public APIResponse<List<ShoeResponse>> getShoesByName(@RequestParam(value = "name", required = false) String name) {
        return APIResponse.<List<ShoeResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(shoeService.getShoesByName(name))
                .build();
    }

    @GetMapping("/categories")
    public APIResponse<List<EnumResponse>> getShoeCategories() {
        return APIResponse.<List<EnumResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(ShoeConstants.getAllCategoryResponses())
                .build();
    }

    @GetMapping("/genders")
    public APIResponse<List<EnumResponse>> getShoeGenders() {
        return APIResponse.<List<EnumResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(ShoeConstants.getAllGenderResponses())
                .build();
    }

    @GetMapping("/list-shoes")
    public APIResponse<PageResponse<ShoeResponse>> getShoePaging(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Integer brandId,  // Changed default to 0
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean status,
            @RequestParam(defaultValue = "date") String sortOrder,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size
            ) {
        return APIResponse.<PageResponse<ShoeResponse>>builder()
                .flag(true)
                .message("OK")
                .result(shoeService.getShoePaging(name, minPrice, maxPrice, brandId, gender, category, page, size, sortOrder, status))
                .build();
    }


}
