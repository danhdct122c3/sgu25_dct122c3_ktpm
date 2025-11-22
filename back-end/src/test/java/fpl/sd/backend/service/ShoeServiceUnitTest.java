package fpl.sd.backend.service;

import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.dto.request.ImageRequest; // Import đúng class ImageRequest
import fpl.sd.backend.dto.request.ShoeCreateRequest;
import fpl.sd.backend.dto.response.ShoeResponse;
import fpl.sd.backend.entity.*;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.mapper.ShoeImageMapper;
import fpl.sd.backend.mapper.ShoeVariantMapper;
import fpl.sd.backend.repository.*;
import fpl.sd.backend.utils.SKUGenerators;
import fpl.sd.backend.utils.ShoeHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoeServiceUnitTest {

    // --- MOCK CÁC REPOSITORY ---
    @Mock private ShoeRepository shoeRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private ShoeImageRepository shoeImageRepository;
    @Mock private ShoeVariantRepository shoeVariantRepository;
    @Mock private SizeChartRepository sizeChartRepository;

    // --- MOCK CÁC UTILS/MAPPERS ---
    @Mock private ShoeHelper shoeHelper;
    @Mock private ShoeImageMapper imageMapper;
    @Mock private ShoeVariantMapper shoeVariantMapper;
    @Mock private SKUGenerators skuGenerators;

    @InjectMocks
    private ShoeService shoeService;

    private Shoe shoe;
    private Brand brand;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(1);
        brand.setBrandName("Nike");

        shoe = Shoe.builder()
                .id(1)
                .name("Air Jordan")
                .status(true)
                .brand(brand)
                // Giả định Enum là MAN. Nếu code của bạn dùng MALE, hãy sửa thành Gender.MALE
                .gender(ShoeConstants.Gender.MAN)
                .category(ShoeConstants.Category.RUNNING)
                .shoeImages(new ArrayList<>())
                .build();
    }

    @Test
    void getAllShoes_ShouldReturnActiveShoesOnly() {
        when(shoeRepository.findByStatusTrue()).thenReturn(List.of(shoe));
        when(shoeHelper.getShoeResponse(any())).thenReturn(new ShoeResponse());

        List<ShoeResponse> result = shoeService.getAllShoes();

        assertThat(result).hasSize(1);
        verify(shoeRepository).findByStatusTrue();
    }

    @Test
    void getShoeById_WhenExists_ShouldReturnShoe() {
        when(shoeRepository.findById(1)).thenReturn(Optional.of(shoe));
        ShoeResponse resp = new ShoeResponse();
        resp.setName("Air Jordan");
        when(shoeHelper.getShoeResponse(shoe)).thenReturn(resp);

        ShoeResponse response = shoeService.getShoeById(1);

        assertThat(response.getName()).isEqualTo("Air Jordan");
    }

    @Test
    void getShoeById_WhenNotExists_ShouldThrowException() {
        when(shoeRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shoeService.getShoeById(999))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void createShoe_ValidRequest_ShouldSave() {
        // Arrange
        ShoeCreateRequest request = new ShoeCreateRequest();
        request.setName("New Shoe");
        request.setPrice(100.0);
        request.setBrandId(1);

        // String này phải khớp với tên Enum trong ShoeConstants (ví dụ "MAN" hoặc "MALE")
        request.setGender("MAN");
        request.setCategory("RUNNING");
        request.setStatus(true);

        // --- SỬA: Tạo List<ImageRequest> không rỗng ---
        // Dùng ImageRequest thay vì ShoeImageRequest để khớp với DTO của bạn
        ImageRequest imgReq = new ImageRequest();
        imgReq.setUrl("http://test-image.com/shoe.jpg");

        List<ImageRequest> images = new ArrayList<>();
        images.add(imgReq);
        request.setImages(images);
        // ---------------------------------------------

        // Mock behavior
        when(brandRepository.findById(1)).thenReturn(Optional.of(brand));
        when(shoeRepository.save(any(Shoe.class))).thenAnswer(i -> i.getArguments()[0]);

        // Mock mapper: Trả về object ShoeImage rỗng khi map từ ImageRequest
        when(imageMapper.toShoeImage(any(ImageRequest.class))).thenReturn(new ShoeImage());

        when(shoeHelper.getShoeResponse(any())).thenReturn(new ShoeResponse());

        // Act
        shoeService.createShoe(request);

        // Assert
        verify(shoeRepository).save(any(Shoe.class));
        verify(shoeImageRepository).saveAll(any());
    }

    @Test
    void deleteShoe_ShouldMarkStatusFalse() {
        // Arrange
        when(shoeRepository.findById(1)).thenReturn(Optional.of(shoe));

        // Mock list variant rỗng để tránh NullPointerException khi service gọi findShoeVariantByShoeId
        when(shoeVariantRepository.findShoeVariantByShoeId(1)).thenReturn(Collections.emptyList());

        // Act
        shoeService.deleteShoe(1);

        // Assert
        assertThat(shoe.isStatus()).isFalse(); // Kiểm tra soft delete
        verify(shoeRepository).save(shoe);
        verify(shoeVariantRepository).saveAll(any());
    }
}