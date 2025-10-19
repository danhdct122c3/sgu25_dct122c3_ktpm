package fpl.sd.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.ai.chat.ChatClient;
import fpl.sd.backend.ai.chat.dto.ChatRequest;
import fpl.sd.backend.ai.chat.dto.ChatResponse;
import fpl.sd.backend.ai.chat.dto.Message;
import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.dto.PageResponse;
import fpl.sd.backend.dto.request.ShoeCreateRequest;
import fpl.sd.backend.dto.request.ShoeUpdateRequest;
import fpl.sd.backend.dto.request.VariantUpdateRequest;
import fpl.sd.backend.dto.response.ShoeResponse;

import fpl.sd.backend.entity.*;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.mapper.ShoeImageMapper;
// import fpl.sd.backend.mapper.ShoeMapper; // Commented out - not using MapStruct
import fpl.sd.backend.mapper.ShoeVariantMapper;
import fpl.sd.backend.repository.*;
import fpl.sd.backend.utils.MessageUtil;
import fpl.sd.backend.utils.SKUGenerators;
import fpl.sd.backend.utils.ShoeHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ShoeService {
    private static final Logger log = LoggerFactory.getLogger(ShoeService.class);
    ShoeRepository shoeRepository;
    // ShoeMapper shoeMapper; // Commented out - not using MapStruct
    BrandRepository brandRepository;
    ShoeImageRepository shoeImageRepository;
    SKUGenerators skuGenerators;
    SizeChartRepository sizeChartRepository;
    ShoeVariantRepository shoeVariantRepository;
    ShoeImageMapper imageMapper;
    ShoeVariantMapper shoeVariantMapper;
    ShoeHelper shoeHelper;
    ChatClient chatClient;
    ObjectMapper objectMapper;

    /**
     * Get all shoes for ADMIN (includes hidden/deleted shoes)
     * Used in admin dashboard to manage all shoes
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<ShoeResponse> getAllShoesForAdmin() {
        List<Shoe> shoes = shoeRepository.findAll();
        return shoes.stream()
                .map(shoeHelper::getShoeResponse)
                .toList();
    }
    
    /**
     * Get all ACTIVE shoes for customers (status = true)
     * This is the public API for shop pages
     */
    public List<ShoeResponse> getAllShoes() {
        List<Shoe> shoes = shoeRepository.findByStatusTrue();
        return shoes.stream()
                .map(shoeHelper::getShoeResponse)
                .toList();
    }

    public ShoeResponse getShoeById(int id) {
        Shoe shoe = shoeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return shoeHelper.getShoeResponse(shoe);
    }

    public List<ShoeResponse> getShoesByGender(String gender) {
        // Only return active shoes (status = true)
        List<Shoe> shoes = shoeRepository.findByStatusTrueAndGender(
                ShoeConstants.Gender.valueOf(gender.toUpperCase())
        );
        return shoes.stream()
                .map(shoeHelper::getShoeResponse)
                .toList();
    }

    public List<ShoeResponse> getShoesByBrand(Integer brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        // Only return active shoes (status = true)
        List<Shoe> shoes = shoeRepository.findByStatusTrueAndBrand(brand);
        return shoes.stream()
                .map(shoeHelper::getShoeResponse)
                .toList();
    }

    public List<ShoeResponse> getShoesByCategory(String categoryName) {
        ShoeConstants.Category categoryEnum = ShoeConstants.getCategoryFromString(categoryName);
        if (categoryEnum == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        // Only return active shoes (status = true)
        List<Shoe> shoes = shoeRepository.findByStatusTrueAndCategory(categoryEnum);

        return shoes.stream()
                .map(shoeHelper::getShoeResponse)
                .toList();
    }


    public ShoeResponse createShoe(ShoeCreateRequest request) {
        // Validate images
        if (request.getImages() == null || request.getImages().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        // Create and map Shoe
        Shoe newShoe = new Shoe();
        newShoe.setName(request.getName());
        newShoe.setPrice(request.getPrice());
        newShoe.setDescription(request.getDescription());
        newShoe.setGender(ShoeConstants.Gender.valueOf(request.getGender().toUpperCase()));
        newShoe.setStatus(request.isStatus());
        newShoe.setCreatedAt(Instant.now());

        // Retrieve Brand
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        newShoe.setBrand(brand);

        // Persist Shoe entity
        shoeRepository.save(newShoe);

        // Map and save Shoe Images
        List<ShoeImage> images = request.getImages().stream()
                .map(imgRequest -> {
                    ShoeImage shoeImage = imageMapper.toShoeImage(imgRequest);
                    shoeImage.setShoe(newShoe);
                    shoeImage.setCreatedAt(Instant.now());
                    return shoeImage;
                }).toList();
        shoeImageRepository.saveAll(images);

        // Map and save Shoe Variants (only if variants exist)
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            List<ShoeVariant> variants = request.getVariants().stream()
                    .map(variant -> {
                        ShoeVariant shoeVariant = shoeVariantMapper.toShoeVariant(variant);

                        int sizeId = variant.getSizeId();
                        SizeChart size = sizeChartRepository.findById(sizeId)
                                .orElseThrow(() -> new AppException(ErrorCode.INVALID_KEY));
                        shoeVariant.setCreatedAt(Instant.now());
                        shoeVariant.setSizeChart(size);

                        String sku = skuGenerators.generateSKU(brand.getBrandName(),
                                request.getName(),
                                size.getSizeNumber());

                        if (shoeVariantRepository.existsBySku(sku)) {
                            throw new AppException(ErrorCode.SKU_ALREADY_EXISTS);
                        }
                        shoeVariant.setShoe(newShoe);
                        shoeVariant.setSku(sku);
                        return shoeVariant;
                    }).toList();
            shoeVariantRepository.saveAll(variants);
        }

        // Return ShoeResponse
        return shoeHelper.getShoeResponse(newShoe);
    }


    public List<ShoeResponse> getShoesByName(String name) {
        // Only return active shoes (status = true)
        List<Shoe> shoes = shoeRepository.findByStatusTrueAndNameContainingIgnoreCase(name);
        return shoes.stream()
                .map(shoeHelper::getShoeResponse)
                .toList();
    }

    public ShoeResponse updateShoe(ShoeUpdateRequest request, int shoeId) {
        Shoe selectedShoe = shoeRepository.findById(shoeId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        selectedShoe.setUpdatedAt(Instant.now());
        selectedShoe.setName(request.getName());
        selectedShoe.setPrice(request.getPrice());
        selectedShoe.setDescription(request.getDescription());
        selectedShoe.setFakePrice(request.getFakePrice());
        selectedShoe.setGender(ShoeConstants.getGenderFromString(request.getGender()));
        selectedShoe.setCategory(ShoeConstants.getCategoryFromString(request.getCategory()));
        selectedShoe.setStatus(request.isStatus());

        // Update images if provided
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            // Delete old images from database explicitly
            List<ShoeImage> oldImages = new ArrayList<>(selectedShoe.getShoeImages());
            for (ShoeImage oldImage : oldImages) {
                shoeImageRepository.delete(oldImage);
            }
            selectedShoe.getShoeImages().clear();
            
            // Add new images
            List<ShoeImage> newImages = request.getImages().stream()
                    .map(imageReq -> {
                        ShoeImage shoeImage = new ShoeImage();
                        // Generate unique public_id from URL filename
                        String publicId = extractPublicIdFromUrl(imageReq.getUrl());
                        shoeImage.setPublicId(publicId);
                        shoeImage.setUrl(imageReq.getUrl());
                        shoeImage.setShoe(selectedShoe);
                        shoeImage.setCreatedAt(Instant.now());
                        shoeImage.setUpdatedAt(Instant.now());
                        return shoeImage;
                    })
                    .toList();
            
            selectedShoe.getShoeImages().addAll(newImages);
        }

        List<ShoeVariant> updatedVariants = new ArrayList<>();
        for (VariantUpdateRequest variantRequest : request.getVariants()) {
            ShoeVariant existingVariant = selectedShoe.getShoeVariants().stream()
                    .filter(v -> v.getId().equals(variantRequest.getVariantId()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_KEY));

            existingVariant.setUpdatedAt(Instant.now());
            existingVariant.setStockQuantity(variantRequest.getStockQuantity());
            updatedVariants.add(existingVariant);
        }

        selectedShoe.setShoeVariants(updatedVariants);
        this.shoeRepository.save(selectedShoe);
        return shoeHelper.getShoeResponse(selectedShoe);
    }

    public PageResponse<ShoeResponse> getShoePaging(String name,
                                                    Long minPrice,
                                                    Long maxPrice,
                                                    Integer brandId,
                                                    String genderString,
                                                    String categoryString,
                                                    int page,
                                                    int size,
                                                    String sortOrder,
                                                    Boolean status
                                                    ) {

        Sort sort = createSort(sortOrder);


        Pageable pageable = PageRequest.of(page - 1, size, sort);

        ShoeConstants.Gender genderEnum = ShoeConstants.getGenderFromString(genderString);
        ShoeConstants.Category categoryEnum = ShoeConstants.getCategoryFromString(categoryString);

        Page<Shoe> shoeData = shoeRepository.findShoesByFilters(name, minPrice, maxPrice, brandId, genderEnum, categoryEnum, status, pageable);

        var shoeList = shoeData.getContent()
                .stream()
                .map(shoeHelper::getShoeResponse)
                .toList();

        return PageResponse.<ShoeResponse>builder()
                .currentPage(page)
                .pageSize(shoeData.getSize())
                .totalPages(shoeData.getTotalPages())
                .totalElements(shoeData.getTotalElements())
                .data(shoeList)
                .build();


    }

    private Sort createSort(String sortOrder) {

        String date = "createdAt";
        if (sortOrder == null) {
            return Sort.by(Sort.Direction.ASC, date);
        }

        return switch (sortOrder.toLowerCase()) {
            case "desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "date_desc" -> Sort.by(Sort.Direction.DESC, date);
            default -> Sort.by(Sort.Direction.ASC, date);
        };
    }

    public String shoeData(String messageContent) throws JsonProcessingException {

        String jsonArray = objectMapper.writeValueAsString(this.getAllShoes());

        List<Message> messages = MessageUtil.createMessages("You are a helpful assistant " + messageContent, jsonArray);

        ChatRequest chatRequest = new ChatRequest("gpt-4o-mini", messages);
        ChatResponse chatResponse = this.chatClient.generate(chatRequest);

        return chatResponse.getChoices().getFirst().getMessage().getContent();
    }

    /**
     * Delete a shoe
     * Uses soft delete by marking the shoe as inactive/deleted
     * This maintains referential integrity with orders and other related data
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteShoe(int id) {
        Shoe shoe = shoeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        
        // Soft delete: just mark as inactive/deleted
        shoe.setStatus(false); // Set status to false (inactive)
        shoe.setUpdatedAt(Instant.now());
        shoeRepository.save(shoe);
        
        // Optionally, also soft delete all variants
        List<ShoeVariant> variants = shoeVariantRepository.findShoeVariantByShoeId(id);
        variants.forEach(variant -> {
            variant.setUpdatedAt(Instant.now());
            // If ShoeVariant has status field, set it to false here
        });
        shoeVariantRepository.saveAll(variants);
    }
    
    /**
     * Extract public_id from image URL
     * For URL like "/uploads/shoes/abc123.jpg", return "abc123.jpg"
     * For Cloudinary URL, extract the public_id part
     */
    private String extractPublicIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return java.util.UUID.randomUUID().toString();
        }
        
        // For local uploads like "/uploads/shoes/filename.jpg"
        if (url.startsWith("/uploads/")) {
            String[] parts = url.split("/");
            return parts[parts.length - 1]; // Return filename
        }
        
        // For Cloudinary or other CDN URLs
        // Extract filename from URL
        try {
            String[] urlParts = url.split("/");
            String filenameWithExt = urlParts[urlParts.length - 1];
            return filenameWithExt.split("\\?")[0]; // Remove query params if any
        } catch (Exception e) {
            return java.util.UUID.randomUUID().toString();
        }
    }

}
