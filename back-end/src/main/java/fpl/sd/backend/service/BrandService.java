package fpl.sd.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.ai.chat.ChatClient;
import fpl.sd.backend.ai.chat.dto.ChatRequest;
import fpl.sd.backend.ai.chat.dto.ChatResponse;
import fpl.sd.backend.ai.chat.dto.Message;
import fpl.sd.backend.dto.request.BrandCreateRequest;
import fpl.sd.backend.dto.response.BrandResponse;
import fpl.sd.backend.entity.Brand;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.mapper.BrandMapper;
import fpl.sd.backend.repository.BrandRepository;
import fpl.sd.backend.utils.MessageUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BrandService {
    BrandRepository brandRepository;
    BrandMapper brandMapper;
    ChatClient chatClient;

    public BrandResponse createBrand(BrandCreateRequest request) {
        Brand newBrand = brandMapper.toBrand(request);
        if (brandRepository.existsByBrandName(request.getBrandName())) {
            throw new AppException(ErrorCode.BRAND_ALREADY_EXISTS);
        }
        newBrand.setCreatedAt(Instant.now());
        brandRepository.save(newBrand);
        return brandMapper.toBrandResponse(newBrand);
    }

    public List<BrandResponse> getBrands() {
        List<Brand> brands = brandRepository.findAll();
        return brands.stream()
                .map(brandMapper::toBrandResponse)
                .toList();
    }


    public BrandResponse getBrandById(int id) {
        return brandMapper.toBrandResponse(brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find brand")));

    }

    public String summarize(List<BrandResponse> brandResponses, String messageContent) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonArray = objectMapper.writeValueAsString(brandResponses);

        //Prepare the messages for summarizing
//        List<Message> messages = List.of(
//                new Message("system", messageContent),
//                new Message("user", jsonArray)
//        );

        List<Message> messages = MessageUtil.createMessages(messageContent, jsonArray);

        ChatRequest chatRequest = new ChatRequest("gpt-4o-mini", messages);
        ChatResponse chatResponse = this.chatClient.generate(chatRequest); // Tell chat client to generate
                                                                            // summary base on the text request

        return chatResponse.getChoices().getFirst().getMessage().getContent();
    }

    public void initializeDefaultBrands() {
        // Check if brands already exist
        if (brandRepository.count() > 0) {
            return; // Brands already exist, don't duplicate
        }

        // Create popular shoe brands with logoUrl
        List<Brand> defaultBrands = List.of(
            Brand.builder().brandName("Nike").description("Just Do It").logoUrl("https://logoeps.com/wp-content/uploads/2013/03/nike-vector-logo.png").build(),
            Brand.builder().brandName("Adidas").description("Impossible is Nothing").logoUrl("https://logoeps.com/wp-content/uploads/2012/12/adidas-vector-logo.png").build(),
            Brand.builder().brandName("Puma").description("Forever Faster").logoUrl("https://logoeps.com/wp-content/uploads/2013/03/puma-vector-logo.png").build(),
            Brand.builder().brandName("Converse").description("All Star").logoUrl("https://logoeps.com/wp-content/uploads/2013/03/converse-vector-logo.png").build(),
            Brand.builder().brandName("Vans").description("Off The Wall").logoUrl("https://logoeps.com/wp-content/uploads/2013/03/vans-vector-logo.png").build(),
            Brand.builder().brandName("New Balance").description("Endorsed by No One").logoUrl("https://logoeps.com/wp-content/uploads/2013/03/new-balance-vector-logo.png").build(),
            Brand.builder().brandName("Reebok").description("Be More Human").logoUrl("https://logoeps.com/wp-content/uploads/2013/03/reebok-vector-logo.png").build(),
            Brand.builder().brandName("Fila").description("Style Meets Performance").logoUrl("https://logoeps.com/wp-content/uploads/2013/03/fila-vector-logo.png").build(),
            Brand.builder().brandName("Skechers").description("The Comfort Technology Company").logoUrl("https://logoeps.com/wp-content/uploads/2013/03/skechers-vector-logo.png").build(),
            Brand.builder().brandName("Under Armour").description("I Will").logoUrl("https://logoeps.com/wp-content/uploads/2013/03/under-armour-vector-logo.png").build()
        );

        // Set created timestamp for all brands
        defaultBrands.forEach(brand -> brand.setCreatedAt(Instant.now()));

        brandRepository.saveAll(defaultBrands);
    }

    // New method: update logo for existing brand
    public BrandResponse updateBrandLogo(int brandId, MultipartFile logoFile) throws IOException {
        if (logoFile == null || logoFile.isEmpty()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        // prepare upload directory
        Path uploadDir = Path.of("uploads", "brands");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // delete old local file if it exists and is served from /uploads/brands/
        String oldLogo = brand.getLogoUrl();
        if (oldLogo != null && oldLogo.startsWith("/uploads/brands/")) {
            String oldFilename = oldLogo.substring("/uploads/brands/".length());
            try {
                Path oldPath = uploadDir.resolve(oldFilename);
                Files.deleteIfExists(oldPath);
            } catch (Exception e) {
                // ignore delete failure
            }
        }

        // save new file
        String original = logoFile.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID().toString() + ext;
        Path target = uploadDir.resolve(filename);
        try {
            Files.copy(logoFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        String logoUrl = "/uploads/brands/" + filename;
        brand.setLogoUrl(logoUrl);
        brandRepository.save(brand);
        return brandMapper.toBrandResponse(brand);
    }

    // New method: create brand with logo upload
    public BrandResponse createBrandWithLogo(BrandCreateRequest request, MultipartFile logoFile) throws IOException {
        if (brandRepository.existsByBrandName(request.getBrandName())) {
            throw new AppException(ErrorCode.BRAND_ALREADY_EXISTS);
        }

        Brand newBrand = brandMapper.toBrand(request);
        newBrand.setCreatedAt(Instant.now());

        if (logoFile != null && !logoFile.isEmpty()) {
            // prepare upload directory
            Path uploadDir = Path.of("uploads", "brands");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // save file
            String original = logoFile.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }
            String filename = UUID.randomUUID().toString() + ext;
            Path target = uploadDir.resolve(filename);
            try {
                Files.copy(logoFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
            }

            String logoUrl = "/uploads/brands/" + filename;
            newBrand.setLogoUrl(logoUrl);
        }

        brandRepository.save(newBrand);
        return brandMapper.toBrandResponse(newBrand);
    }

    // Method to fix/normalize brand logos
    public void fixBrandLogos() {
        List<Brand> brands = brandRepository.findAll();
        for (Brand brand : brands) {
            String logoUrl = brand.getLogoUrl();
            if (logoUrl != null && !logoUrl.isBlank()) {
                // Normalize the logo URL (remove any double slashes, etc.)
                logoUrl = logoUrl.replaceAll("//+", "/");
                if (!logoUrl.startsWith("http") && !logoUrl.startsWith("/")) {
                    logoUrl = "/" + logoUrl;
                }
                brand.setLogoUrl(logoUrl);
            }
        }
        brandRepository.saveAll(brands);
    }
}
