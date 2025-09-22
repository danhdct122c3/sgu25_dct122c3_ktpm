package fpl.sd.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fpl.sd.backend.ai.chat.dto.ContentMessageRequest;
import fpl.sd.backend.dto.ApiResponse;
import fpl.sd.backend.dto.request.BrandCreateRequest;
import fpl.sd.backend.dto.response.BrandResponse;
import fpl.sd.backend.service.BrandService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BrandController {
    BrandService brandService;

    private String toAbsoluteUrl(HttpServletRequest req, String logoUrl) {
        if (logoUrl == null || logoUrl.isBlank()) return logoUrl;
        if (logoUrl.startsWith("http://") || logoUrl.startsWith("https://")) return logoUrl;
        // build base URL
        String scheme = req.getScheme();
        String host = req.getServerName();
        int port = req.getServerPort();
        String context = req.getContextPath();
        String base = scheme + "://" + host + (port == 80 || port == 443 ? "" : ":" + port) + context;
        if (!logoUrl.startsWith("/")) logoUrl = "/" + logoUrl;
        return base + logoUrl;
    }

    @PostMapping
    public ApiResponse<BrandResponse> createBrand(@RequestBody @Valid BrandCreateRequest request, HttpServletRequest req) {
        BrandResponse created = brandService.createBrand(request);
        created.setLogoUrl(toAbsoluteUrl(req, created.getLogoUrl()));
        return ApiResponse.<BrandResponse>builder()
                .flag(true)
                .code(200)
                .message("Successfully created brand")
                .result(created)
                .build();
    }

    @GetMapping
    public ApiResponse<List<BrandResponse>> getAllBrands(HttpServletRequest req) {
        List<BrandResponse> list = brandService.getBrands();
        list.forEach(b -> b.setLogoUrl(toAbsoluteUrl(req, b.getLogoUrl())));
        return ApiResponse.<List<BrandResponse>>builder()
                .flag(true)
                .code(200)
                .message("Successfully retrieved all brands")
                .result(list)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> getBrandById(@PathVariable int id, HttpServletRequest req) {
        BrandResponse resp = brandService.getBrandById(id);
        resp.setLogoUrl(toAbsoluteUrl(req, resp.getLogoUrl()));
        return ApiResponse.<BrandResponse>builder()
                .flag(true)
                .code(200)
                .message("Successfully retrieved brand")
                .result(resp)
                .build();
    }

    @PostMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BrandResponse> updateBrandLogo(
            @PathVariable("id") int id,
            @RequestPart("logoFile") MultipartFile logoFile,
            HttpServletRequest req
    ) throws IOException {
        BrandResponse updated = brandService.updateBrandLogo(id, logoFile);
        updated.setLogoUrl(toAbsoluteUrl(req, updated.getLogoUrl()));
        return ApiResponse.<BrandResponse>builder()
                .flag(true)
                .code(200)
                .message("Successfully updated brand logo")
                .result(updated)
                .build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BrandResponse> createBrandWithLogo(
            @RequestParam("brandName") String brandName,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart("logoFile") MultipartFile logoFile,
            HttpServletRequest req
    ) throws IOException {
        BrandCreateRequest request = new BrandCreateRequest();
        request.setBrandName(brandName);
        request.setDescription(description);

        BrandResponse created = brandService.createBrandWithLogo(request, logoFile);
        created.setLogoUrl(toAbsoluteUrl(req, created.getLogoUrl()));
        return ApiResponse.<BrandResponse>builder()
                .flag(true)
                .code(200)
                .message("Successfully created brand with uploaded logo")
                .result(created)
                .build();
    }

    @GetMapping("/summary")
    public ApiResponse<String> summarizeBrands(@RequestBody ContentMessageRequest contentMessageRequest) throws JsonProcessingException {
        List<BrandResponse> brands = brandService.getBrands();
        String brandsSummary = this.brandService.summarize(brands, contentMessageRequest.getContent());
        return ApiResponse.<String>builder()
                .flag(true)
                .message("OK")
                .result(brandsSummary)
                .build();

    }

    @PostMapping("/init")
    public ApiResponse<String> initializeBrands() {
        brandService.initializeDefaultBrands();
        return ApiResponse.<String>builder()
                .flag(true)
                .code(200)
                .message("Brands initialized successfully")
                .result("Default shoe brands have been created")
                .build();
    }

    @PostMapping("/fix-logos")
    public ApiResponse<String> fixBrandLogos() {
        brandService.fixBrandLogos();
        return ApiResponse.<String>builder()
                .flag(true)
                .code(200)
                .message("Brand logos fixed (normalized)")
                .result("OK")
                .build();
    }
}
