package fpl.sd.backend.service;

import fpl.sd.backend.dto.request.ImageRequest;
import fpl.sd.backend.dto.response.ImageResponse;
import fpl.sd.backend.entity.Shoe;
import fpl.sd.backend.entity.ShoeImage;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.mapper.ShoeImageMapper;
import fpl.sd.backend.repository.ShoeImageRepository;
import fpl.sd.backend.repository.ShoeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShoeImageService {
    ShoeImageRepository shoeImageRepository;
    ShoeImageMapper shoeImageMapper;
    ShoeRepository shoeRepository;

    public List<ImageResponse> getShoeImagesByShoeId(int shoeId) {
        if (!shoeImageRepository.existsByShoeId(shoeId)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        List<ShoeImage> images = shoeImageRepository.findAllByShoeId(shoeId);
        return images.stream()
                .map(shoeImageMapper::toImageResponse)
                .toList();
    }

    public ImageResponse getImageById(int id) {
        ShoeImage image = shoeImageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return shoeImageMapper.toImageResponse(image);
    }

    public ImageResponse addImageToShoe(int shoeId, ImageRequest request) {
        Shoe shoe = shoeRepository.findById(shoeId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        ShoeImage image = ShoeImage.builder()
                .url(request.getUrl())
                .publicId(request.getPublicId())
                .shoe(shoe)
                .createdAt(Instant.now())
                .build();

        ShoeImage savedImage = shoeImageRepository.save(image);
        return shoeImageMapper.toImageResponse(savedImage);
    }

    public ImageResponse updateImage(int id, ImageRequest request) {
        ShoeImage image = shoeImageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        image.setUrl(request.getUrl());
        image.setPublicId(request.getPublicId());
        image.setUpdatedAt(Instant.now());

        ShoeImage updatedImage = shoeImageRepository.save(image);
        return shoeImageMapper.toImageResponse(updatedImage);
    }

    public void deleteImage(int id) {
        if (!shoeImageRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        shoeImageRepository.deleteById(id);
    }

    public void deleteAllImagesByShoeId(int shoeId) {
        if (!shoeRepository.existsById(shoeId)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        List<ShoeImage> images = shoeImageRepository.findAllByShoeId(shoeId);
        shoeImageRepository.deleteAll(images);
    }
}
