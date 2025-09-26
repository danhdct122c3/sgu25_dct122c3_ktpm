package fpl.sd.backend.controller;

import fpl.sd.backend.dto.APIResponse;
import fpl.sd.backend.dto.response.SizeResponse;
import fpl.sd.backend.service.SizeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    @PostMapping("/sizes/init")
    public APIResponse<String> initializeSizes() {
        sizeService.initializeDefaultSizes();
        return APIResponse.<String>builder()
                .flag(true)
                .code(200)
                .message("Sizes initialized successfully")
                .result("Default shoe sizes have been created")
                .build();
    }
}
