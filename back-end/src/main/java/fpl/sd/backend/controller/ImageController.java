package fpl.sd.backend.controller;

import fpl.sd.backend.dto.APIResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ImageController {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<List<String>> uploadImages(
            @RequestPart("files") List<MultipartFile> files) throws IOException {
        
        List<String> uploadedUrls = new ArrayList<>();
        
        // Prepare upload directory
        Path uploadDir = Path.of("uploads", "shoes");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            
            // Generate unique filename
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }
            String filename = UUID.randomUUID().toString() + ext;
            Path target = uploadDir.resolve(filename);
            
            // Save file
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            
            // Return URL path
            String imageUrl = "/uploads/shoes/" + filename;
            uploadedUrls.add(imageUrl);
        }
        
        return APIResponse.<List<String>>builder()
                .result(uploadedUrls)
                .build();
    }
    
    @PostMapping(value = "/upload-single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<String> uploadSingleImage(
            @RequestPart("file") MultipartFile file) throws IOException {
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Prepare upload directory
        Path uploadDir = Path.of("uploads", "shoes");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // Generate unique filename
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID().toString() + ext;
        Path target = uploadDir.resolve(filename);
        
        // Save file
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        
        // Return URL path
        String imageUrl = "/uploads/shoes/" + filename;
        
        return APIResponse.<String>builder()
                .result(imageUrl)
                .build();
    }
}
