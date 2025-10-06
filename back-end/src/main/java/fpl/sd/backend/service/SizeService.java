package fpl.sd.backend.service;

import fpl.sd.backend.dto.request.VariantRequest;
import fpl.sd.backend.dto.response.SizeResponse;
import fpl.sd.backend.dto.response.VariantResponse;
import fpl.sd.backend.entity.Shoe;
import fpl.sd.backend.entity.ShoeVariant;
import fpl.sd.backend.entity.SizeChart;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.mapper.ShoeVariantMapper;
import fpl.sd.backend.repository.ShoeRepository;
import fpl.sd.backend.repository.ShoeVariantRepository;
import fpl.sd.backend.repository.SizeChartRepository;
import fpl.sd.backend.utils.SKUGenerators;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SizeService {

    SizeChartRepository sizeChartRepository;
    ShoeVariantRepository shoeVariantRepository;
    ShoeRepository shoeRepository;
    ShoeVariantMapper shoeVariantMapper;
    SKUGenerators skuGenerators;


    public List<SizeResponse> getAllSizes() {
        List<SizeChart> sizes = sizeChartRepository.findAll();
        return sizes.stream()
                .map(size -> {
                    SizeResponse sizeResponse = new SizeResponse();
                    sizeResponse.setId(size.getId());
                    sizeResponse.setSizeNumber(size.getSizeNumber());
                    return sizeResponse;
                }).toList();
    }

    public void initializeDefaultSizes() {
        // Check if sizes already exist
        if (sizeChartRepository.count() > 0) {
            return; // Sizes already exist, don't duplicate
        }

        // Create standard shoe sizes (US sizes)
        List<SizeChart> defaultSizes = List.of(
            SizeChart.builder().sizeNumber(6.0).build(),
            SizeChart.builder().sizeNumber(6.5).build(),
            SizeChart.builder().sizeNumber(7.0).build(),
            SizeChart.builder().sizeNumber(7.5).build(),
            SizeChart.builder().sizeNumber(8.0).build(),
            SizeChart.builder().sizeNumber(8.5).build(),
            SizeChart.builder().sizeNumber(9.0).build(),
            SizeChart.builder().sizeNumber(9.5).build(),
            SizeChart.builder().sizeNumber(10.0).build(),
            SizeChart.builder().sizeNumber(10.5).build(),
            SizeChart.builder().sizeNumber(11.0).build(),
            SizeChart.builder().sizeNumber(11.5).build(),
            SizeChart.builder().sizeNumber(12.0).build()
        );

        sizeChartRepository.saveAll(defaultSizes);
    }

    /**
     * Get all variants for a specific shoe
     */
    public List<VariantResponse> getVariantsByShoeId(int shoeId) {
        List<ShoeVariant> variants = shoeVariantRepository.findShoeVariantByShoeId(shoeId);
        return variants.stream()
                .map(shoeVariantMapper::toShoeVariantResponse)
                .toList();
    }

    /**
     * Get a specific variant by ID
     */
    public VariantResponse getVariantById(String variantId) {
        ShoeVariant variant = shoeVariantRepository.findById(variantId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return shoeVariantMapper.toShoeVariantResponse(variant);
    }

    /**
     * Create a new variant
     */
    public VariantResponse createVariant(VariantRequest request) {
        Shoe shoe = shoeRepository.findById(request.getShoeId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        SizeChart size = sizeChartRepository.findById(request.getSizeId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Generate SKU
        String sku = skuGenerators.generateSKU(shoe.getBrand().getBrandName(), 
                                               shoe.getName(), 
                                               size.getSizeNumber());

        // Check if SKU already exists
        if (shoeVariantRepository.existsBySku(sku)) {
            throw new AppException(ErrorCode.SKU_ALREADY_EXISTS);
        }

        ShoeVariant variant = ShoeVariant.builder()
                .shoe(shoe)
                .sizeChart(size)
                .sku(sku)
                .stockQuantity(request.getStockQuantity())
                .createdAt(Instant.now())
                .build();

        ShoeVariant savedVariant = shoeVariantRepository.save(variant);
        return shoeVariantMapper.toShoeVariantResponse(savedVariant);
    }

    /**
     * Update a variant (mainly stock quantity)
     */
    public VariantResponse updateVariant(VariantRequest request) {
        ShoeVariant variant = shoeVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Update stock quantity
        variant.setStockQuantity(request.getStockQuantity());
        variant.setUpdatedAt(Instant.now());

        ShoeVariant updatedVariant = shoeVariantRepository.save(variant);
        return shoeVariantMapper.toShoeVariantResponse(updatedVariant);
    }

    /**
     * Delete a variant
     */
    public void deleteVariant(String variantId) {
        if (!shoeVariantRepository.existsById(variantId)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        shoeVariantRepository.deleteById(variantId);
    }

}
