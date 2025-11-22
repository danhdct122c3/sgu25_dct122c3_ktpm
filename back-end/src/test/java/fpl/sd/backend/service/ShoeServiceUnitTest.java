        package fpl.sd.backend.service;

import fpl.sd.backend.constant.ShoeConstants;
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

    // --- MOCK CÁC REPOSITORY CẦN THIẾT ---
    @Mock private ShoeRepository shoeRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private ShoeImageRepository shoeImageRepository;
    // Thêm các Mock bị thiếu gây ra lỗi NullPointer
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
                .gender(ShoeConstants.Gender.MAN)
                .category(ShoeConstants.Category.SNEAKER)
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

    // --- ĐÃ SỬA LỖI: Mock thêm imageMapper và truyền đúng Enum String ---
    @Test
    void createShoe_ValidRequest_ShouldSave() {
        // Arrange
        ShoeCreateRequest request = new ShoeCreateRequest();
        request.setName("New Shoe");
        request.setPrice(100.0);
        request.setBrandId(1);
        // Đảm bảo chuỗi này khớp với Enum trong ShoeConstants (thường là chữ in hoa)
        request.setGender("MALE");
        request.setCategory("SNEAKER");
        request.setStatus(true);
        // Use empty list to avoid null validation while not referencing ShoeImageRequest type
        request.setImages(new ArrayList<>());

        when(brandRepository.findById(1)).thenReturn(Optional.of(brand));
        // Mock hành vi save shoe trả về chính nó
        when(shoeRepository.save(any(Shoe.class))).thenAnswer(i -> i.getArguments()[0]);
        // Mock mapper ảnh để không bị lỗi null khi map
        when(imageMapper.toShoeImage(any())).thenReturn(new ShoeImage());
        when(shoeHelper.getShoeResponse(any())).thenReturn(new ShoeResponse());

        // Act
        shoeService.createShoe(request);

        // Assert
        verify(shoeRepository).save(any(Shoe.class));
        verify(shoeImageRepository).saveAll(any());
    }

    // --- ĐÃ SỬA LỖI: Mock shoeVariantRepository ---
    @Test
    void deleteShoe_ShouldMarkStatusFalse() {
        // Arrange
        when(shoeRepository.findById(1)).thenReturn(Optional.of(shoe));

        // QUAN TRỌNG: Mock hàm tìm variant để không bị NullPointerException
        when(shoeVariantRepository.findShoeVariantByShoeId(1)).thenReturn(Collections.emptyList());

        // Act
        shoeService.deleteShoe(1);

        // Assert
        assertThat(shoe.isStatus()).isFalse(); // Kiểm tra soft delete
        verify(shoeRepository).save(shoe);
        // Kiểm tra xem có gọi hàm xóa variants không
        verify(shoeVariantRepository).saveAll(any());
    }
}
