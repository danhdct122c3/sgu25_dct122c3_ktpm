package fpl.sd.backend.service;

import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.dto.request.ShoeCreateRequest;
import fpl.sd.backend.dto.response.ShoeResponse;
import fpl.sd.backend.entity.*;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.mapper.ShoeImageMapper;
import fpl.sd.backend.repository.*;
import fpl.sd.backend.utils.ShoeHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoeServiceUnitTest {

    @Mock private ShoeRepository shoeRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private ShoeImageRepository shoeImageRepository;
    @Mock private ShoeHelper shoeHelper; // Mock Helper vì nó dùng để map response
    @Mock private ShoeImageMapper imageMapper;

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
                .gender(ShoeConstants.Gender.MAN)
                .category(ShoeConstants.Category.SNEAKER)
                .shoeImages(new ArrayList<>())
                .build();
    }

    // --- TC_PC_001: View Product List (Customer View) ---
    @Test
    void getAllShoes_ShouldReturnActiveShoesOnly() {
        // Arrange
        when(shoeRepository.findByStatusTrue()).thenReturn(List.of(shoe));
        when(shoeHelper.getShoeResponse(any())).thenReturn(new ShoeResponse());

        // Act
        List<ShoeResponse> result = shoeService.getAllShoes();

        // Assert
        assertThat(result).hasSize(1);
        verify(shoeRepository).findByStatusTrue(); // Đảm bảo chỉ gọi hàm lấy SP active
    }

    // --- TC_PC_002: View Product Details (Success) ---
    @Test
    void getShoeById_WhenExists_ShouldReturnShoe() {
        // Arrange
        when(shoeRepository.findById(1)).thenReturn(Optional.of(shoe));
        ShoeResponse resp = new ShoeResponse();
        resp.setName("Air Jordan");
        when(shoeHelper.getShoeResponse(shoe)).thenReturn(resp);

        // Act
        ShoeResponse response = shoeService.getShoeById(1);

        // Assert
        assertThat(response.getName()).isEqualTo("Air Jordan");
    }

    // --- TC_PC_003: View Product Details (Not Found) ---
    @Test
    void getShoeById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(shoeRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> shoeService.getShoeById(999))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }

    // --- TC_PC_004: Add New Product (Success) ---
    @Test
    void createShoe_ValidRequest_ShouldSave() {
        // Arrange
        ShoeCreateRequest request = new ShoeCreateRequest();
        request.setName("New Shoe");
        request.setPrice(100.0);
        request.setBrandId(1);
        request.setGender("MAN");
        request.setCategory("SNEAKER");
        when(brandRepository.findById(1)).thenReturn(Optional.of(brand));
        when(shoeRepository.save(any(Shoe.class))).thenAnswer(i -> i.getArguments()[0]);
        when(imageMapper.toShoeImage(any())).thenReturn(new ShoeImage());
        when(shoeHelper.getShoeResponse(any())).thenReturn(new ShoeResponse());

        // Act
        shoeService.createShoe(request);

        // Assert
        verify(shoeRepository).save(any(Shoe.class)); // Kiểm tra hàm save được gọi
        verify(shoeImageRepository).saveAll(any());   // Kiểm tra ảnh được lưu
    }

    // --- TC_PC_007: Delete Product (Soft Delete) ---
    @Test
    void deleteShoe_ShouldMarkStatusFalse() {
        // Arrange
        when(shoeRepository.findById(1)).thenReturn(Optional.of(shoe));

        // Act
        shoeService.deleteShoe(1);

        // Assert
        assertThat(shoe.isStatus()).isFalse(); // Quan trọng: Kiểm tra soft delete
        verify(shoeRepository).save(shoe);     // Phải gọi save để update trạng thái
    }
}