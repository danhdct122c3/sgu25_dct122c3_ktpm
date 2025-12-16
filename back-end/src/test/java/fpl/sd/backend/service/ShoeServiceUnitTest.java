package fpl.sd.backend.service;

import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.dto.request.ImageRequest;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoeServiceUnitTest {

    @Mock private ShoeRepository shoeRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private ShoeImageRepository shoeImageRepository;
    @Mock private ShoeVariantRepository shoeVariantRepository;
    @Mock private SizeChartRepository sizeChartRepository;

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
                .category(ShoeConstants.Category.RUNNING)
                .shoeImages(new ArrayList<>())
                .build();
    }

    @DisplayName("getAllShoes_ShouldReturnActiveShoesOnly | Lấy tất cả giày đang active | status=true | Trả về danh sách ShoeResponse chỉ chứa giày có status=true")
    @Test
    void getAllShoes_ShouldReturnActiveShoesOnly() {
        // Arrange
        when(shoeRepository.findByStatusTrue()).thenReturn(List.of(shoe));
        when(shoeHelper.getShoeResponse(any())).thenReturn(new ShoeResponse());

        // Act
        List<ShoeResponse> result = shoeService.getAllShoes();

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Result list size: " + result.size());

        // Assert
        assertThat(result).hasSize(1);
        verify(shoeRepository).findByStatusTrue();
    }

    @DisplayName("getShoeById_WhenExists_ShouldReturnShoe | Lấy giày theo ID tồn tại | shoeId=1, name='Air Jordan' | Trả về ShoeResponse với tên 'Air Jordan'")
    @Test
    void getShoeById_WhenExists_ShouldReturnShoe() {
        // Arrange
        when(shoeRepository.findById(1)).thenReturn(Optional.of(shoe));
        ShoeResponse resp = new ShoeResponse();
        resp.setName("Air Jordan");
        when(shoeHelper.getShoeResponse(shoe)).thenReturn(resp);

        // Act
        ShoeResponse response = shoeService.getShoeById(1);

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Retrieved Shoe Name: " + response.getName());

        // Assert
        assertThat(response.getName()).isEqualTo("Air Jordan");
    }

    @DisplayName("getShoeById_WhenNotExists_ShouldThrowException | Lấy giày theo ID không tồn tại | shoeId=999 | Ném AppException với ErrorCode.PRODUCT_NOT_FOUND")
    @Test
    void getShoeById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(shoeRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Capture Exception
        AppException exception = assertThrows(AppException.class, () -> {
            shoeService.getShoeById(999);
        });

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Exception: " + exception.getErrorCode().getMessage() + " (" + exception.getErrorCode() + ")");

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }

    @DisplayName("createShoe_ValidRequest_ShouldSave | Tạo giày mới hợp lệ | name='New Shoe', price=100.0, brandId=1 | Lưu Shoe và ShoeImage thành công")
    @Test
    void createShoe_ValidRequest_ShouldSave() {
        // Arrange
        ShoeCreateRequest request = new ShoeCreateRequest();
        request.setName("New Shoe");
        request.setPrice(100.0);
        request.setBrandId(1);
        request.setGender("MAN");
        request.setCategory("RUNNING");
        request.setStatus(true);

        ImageRequest imgReq = new ImageRequest();
        imgReq.setUrl("http://test-image.com/shoe.jpg");
        request.setImages(List.of(imgReq));

        when(brandRepository.findById(1)).thenReturn(Optional.of(brand));
        // Mock save để trả về object được truyền vào
        when(shoeRepository.save(any(Shoe.class))).thenAnswer(i -> i.getArguments()[0]);
        when(imageMapper.toShoeImage(any(ImageRequest.class))).thenReturn(new ShoeImage());
        when(shoeHelper.getShoeResponse(any())).thenReturn(new ShoeResponse());

        // Act
        shoeService.createShoe(request);

        // Capture saved item
        ArgumentCaptor<Shoe> shoeCaptor = ArgumentCaptor.forClass(Shoe.class);
        verify(shoeRepository).save(shoeCaptor.capture());
        Shoe savedShoe = shoeCaptor.getValue();

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Created Shoe: " + savedShoe.getName() + ", Price: " + savedShoe.getPrice() + ", Brand: " + savedShoe.getBrand().getBrandName());

        // Assert
        assertThat(savedShoe.getName()).isEqualTo("New Shoe");
        verify(shoeImageRepository).saveAll(any());
    }

    @DisplayName("deleteShoe_ShouldMarkStatusFalse | Xóa giày (soft delete) | shoeId=1 | Đặt status=false cho Shoe và tất cả ShoeVariant")
    @Test
    void deleteShoe_ShouldMarkStatusFalse() {
        // Arrange
        when(shoeRepository.findById(1)).thenReturn(Optional.of(shoe));
        when(shoeVariantRepository.findShoeVariantByShoeId(1)).thenReturn(Collections.emptyList());

        // Act
        shoeService.deleteShoe(1);

        // ==> IN KẾT QUẢ THỰC TẾ
        System.out.println("[ACTUAL] Shoe ID " + shoe.getId() + " status after delete: " + shoe.isStatus());

        // Assert
        assertThat(shoe.isStatus()).isFalse();
        verify(shoeRepository).save(shoe);
        verify(shoeVariantRepository).saveAll(any());
    }
}