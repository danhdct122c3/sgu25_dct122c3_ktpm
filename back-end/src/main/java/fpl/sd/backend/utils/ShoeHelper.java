package fpl.sd.backend.utils;

import fpl.sd.backend.dto.response.ImageResponse;
import fpl.sd.backend.dto.response.ShoeResponse;
import fpl.sd.backend.entity.Shoe;
import fpl.sd.backend.entity.ShoeImage;
import fpl.sd.backend.entity.ShoeVariant;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.mapper.ShoeImageMapper;
// import fpl.sd.backend.mapper.ShoeMapper;  // Comment out
import fpl.sd.backend.mapper.ShoeVariantMapper;
import fpl.sd.backend.repository.ShoeImageRepository;
import fpl.sd.backend.repository.ShoeVariantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShoeHelper {
    ShoeImageRepository shoeImageRepository;
    ShoeVariantRepository shoeVariantRepository;
    ShoeImageMapper shoeImageMapper;
    ShoeVariantMapper shoeVariantMapper;
    // ShoeMapper shoeMapper;  // Comment out

    public ShoeResponse getShoeResponse(Shoe shoe) {
        if (shoe == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        // Manual mapping instead of using ShoeMapper
        ShoeResponse response = new ShoeResponse();
        response.setId(String.valueOf(shoe.getId())); // Convert int to String
        response.setName(shoe.getName());
        response.setPrice(shoe.getPrice());
        response.setDescription(shoe.getDescription());
        response.setStatus(shoe.isStatus());
        response.setFakePrice(shoe.getFakePrice());
        response.setCreatedAt(shoe.getCreatedAt());
        response.setUpdatedAt(shoe.getUpdatedAt());
        response.setGender(shoe.getGender() != null ? shoe.getGender().name() : null);
        response.setCategory(shoe.getCategory() != null ? shoe.getCategory().name() : null);
        
        // ⭐ Set brandId from Brand entity
        if (shoe.getBrand() != null) {
            response.setBrandId(shoe.getBrand().getId());
        }

        if (shoeVariantRepository != null) {
            List<ShoeImage> images = shoeImageRepository.findAllByShoeId(shoe.getId());
            response.setImages(images.stream()
                    .map(shoeImageMapper::toImageResponse)
                    .toList());

        }

        if (shoeVariantRepository != null) {
            List<ShoeVariant> variants = shoeVariantRepository.findShoeVariantByShoeId(shoe.getId());
            response.setVariants(variants.stream()
                    .map(shoeVariantMapper::toShoeVariantResponse)
                    .toList());
        }

        return response;
    }
}
