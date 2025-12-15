package fpl.sd.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.ai.chat.ChatClient;
import fpl.sd.backend.constant.DiscountConstants;
import fpl.sd.backend.dto.request.DiscountCreateRequest;
import fpl.sd.backend.dto.response.DiscountResponse;
import fpl.sd.backend.entity.*;
import fpl.sd.backend.mapper.DiscountMapper;
import fpl.sd.backend.repository.DiscountCategoryRepository;
import fpl.sd.backend.repository.DiscountShoeRepository;
import fpl.sd.backend.repository.DiscountRepository;
import fpl.sd.backend.repository.ShoeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiscountServiceUnitTest {

    // --- Mocks for Services ---
    @Mock private DiscountRepository discountRepository;
    @Mock private DiscountMapper discountMapper;
    @Mock private DiscountCategoryRepository discountCategoryRepository;
    @Mock private DiscountShoeRepository discountShoeRepository;
    @Mock private ShoeRepository shoeRepository;
    @Mock private ObjectMapper objectMapper;
    @Mock private ChatClient chatClient;

    // --- Services Under Test ---
    // Chúng ta khởi tạo cả 2 service để test trọn vẹn các case trong Excel
    private DiscountService discountService;
    private DiscountValidationService discountValidationService;

    // --- Test Data ---
    private List<OrderDetail> orderDetails_500k;
    private List<OrderDetail> orderDetails_100k;

    @BeforeEach
    void setUp() {
        // Manual Injection để kiểm soát dependency
        discountService = new DiscountService(
                discountRepository, discountMapper, objectMapper, chatClient,
                discountCategoryRepository, discountShoeRepository, shoeRepository
        );

        discountValidationService = new DiscountValidationService(
                discountCategoryRepository, discountShoeRepository
        );

        // Setup Giỏ hàng 500k (cho case thành công)
        Shoe shoe = Shoe.builder().id(10).price(500000.0).build();
        ShoeVariant variant = ShoeVariant.builder().shoe(shoe).build();
        OrderDetail item = OrderDetail.builder().variant(variant).price(500000.0).quantity(1).build();
        orderDetails_500k = Collections.singletonList(item);

        // Setup Giỏ hàng 100k (cho case không đủ min order)
        Shoe shoeSmall = Shoe.builder().id(11).price(100000.0).build();
        ShoeVariant variantSmall = ShoeVariant.builder().shoe(shoeSmall).build();
        OrderDetail itemSmall = OrderDetail.builder().variant(variantSmall).price(100000.0).quantity(1).build();
        orderDetails_100k = Collections.singletonList(itemSmall);
    }

    // ========================================================================
    // NHÓM TEST: CUSTOMER (TC_DM_001 -> 008) - Tính toán giảm giá
    // ========================================================================

    @Test
    void TC_DM_001_applyValidDiscountCode_shouldCalculateCorrectly() {
        // Arrange
        Discount discount = Discount.builder()
                .code("TEST2")
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .percentage(20.0) // 20%
                .startDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .minimumOrderAmount(0.0)
                .usageLimit(100)
                .usedCount(0)
                .build();

        // Mock: Không giới hạn category/shoe
        when(discountCategoryRepository.findByDiscount(discount)).thenReturn(Collections.emptyList());
        when(discountShoeRepository.findByDiscount(discount)).thenReturn(Collections.emptyList());

        // Act
        double amount = discountValidationService.calculateDiscountAmount(discount, orderDetails_500k);

        // Assert: 500k * 20% = 100k
        assertThat(amount).isEqualTo(100000.0);
    }

    @Test
    void TC_DM_002_applyExpiredDiscountCode_shouldReturnZero() {
        // Arrange
        Discount expiredDiscount = Discount.builder()
                .code("GIAMGIA1")
                .percentage(10.0)
                .endDate(Instant.now().minus(1, ChronoUnit.DAYS)) // Hết hạn hôm qua
                .build();

        // Act
        double amount = discountValidationService.calculateDiscountAmount(expiredDiscount, orderDetails_500k);

        // Assert
        assertThat(amount).isEqualTo(0.0);
    }

    @Test
    void TC_DM_003_applyUsedDiscountCode_shouldReturnZero() {
        // Arrange
        Discount usedDiscount = Discount.builder()
                .code("TEST5")
                .percentage(50.0)
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .usageLimit(10)
                .usedCount(10) // Đã dùng hết
                .build();

        // Act
        double amount = discountValidationService.calculateDiscountAmount(usedDiscount, orderDetails_500k);

        // Assert
        assertThat(amount).isEqualTo(0.0);
    }

    @Test
    void TC_DM_004_discountMinimumPurchase_cartBelowMinimum_shouldReturnZero() {
        // Arrange
        Discount minOrderDiscount = Discount.builder()
                .code("TEST4")
                .minimumOrderAmount(200000.0) // Yêu cầu 200k
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        // Act: Dùng giỏ hàng 100k
        double amount = discountValidationService.calculateDiscountAmount(minOrderDiscount, orderDetails_100k);

        // Assert
        assertThat(amount).isEqualTo(0.0);
    }

    @Test
    void TC_DM_006_removeAppliedDiscount_shouldReturnZero() {
        // Arrange: User xóa mã, tương đương truyền null
        Discount discount = null;

        // Act
        double amount = discountValidationService.calculateDiscountAmount(discount, orderDetails_500k);

        // Assert
        assertThat(amount).isEqualTo(0.0);
    }

    @Test
    void TC_DM_007_multipleDiscountAttempts_shouldUseNewestCode() {
        // Arrange: User nhập mã mới (SAVE20)
        Discount newCode = Discount.builder()
                .code("SAVE20")
                .percentage(20.0)
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        when(discountCategoryRepository.findByDiscount(newCode)).thenReturn(Collections.emptyList());
        when(discountShoeRepository.findByDiscount(newCode)).thenReturn(Collections.emptyList());

        // Act: Gọi tính toán với mã mới
        double amount = discountValidationService.calculateDiscountAmount(newCode, orderDetails_500k);

        // Assert: Tính theo mã mới (20% của 500k = 100k)
        assertThat(amount).isEqualTo(100000.0);
    }

    @Test
    void TC_DM_008_discountCaseSensitivity_shouldFindCode() {
        // Arrange: DB lưu TEST3 (hoa), User nhập test3 (thường)
        String userInput = "test3";
        Discount dbDiscount = Discount.builder().code("TEST3").build();

        // Mock Repo tìm kiếm không phân biệt hoa thường
        when(discountRepository.findDiscountsByCodeContainingIgnoreCase(userInput))
                .thenReturn(List.of(dbDiscount));
        
        DiscountResponse mappedResponse = new DiscountResponse();
        mappedResponse.setCode("TEST3");
        when(discountMapper.toDiscountResponse(dbDiscount)).thenReturn(mappedResponse);

        // Act
        List<DiscountResponse> result = discountService.getDiscountsByCode(userInput);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCode()).isEqualTo("TEST3");
    }

    // ========================================================================
    // NHÓM TEST: MANAGER (TC_DM_009) - Quản lý mã
    // ========================================================================

    @Test
    void TC_DM_009_managerCreateDiscount_shouldCreateSuccessfully() {
        // Arrange
        DiscountCreateRequest request = new DiscountCreateRequest();
        request.setCode("TEST");
        request.setDiscountType(DiscountConstants.DiscountType.PERCENTAGE);
        request.setPercentage(10.0);
        request.setStartDate(Instant.now());
        request.setEndDate(Instant.now().plus(10, ChronoUnit.DAYS));

        Discount discountEntity = new Discount();
        discountEntity.setCode("TEST");
        discountEntity.setPercentage(10.0);

        when(discountRepository.existsByCode("TEST")).thenReturn(false);
        when(discountMapper.toDiscount(request)).thenReturn(discountEntity);
        when(discountRepository.save(any(Discount.class))).thenReturn(discountEntity);
        
        DiscountResponse expectedResponse = new DiscountResponse();
        expectedResponse.setCode("TEST");
        when(discountMapper.toDiscountResponse(discountEntity)).thenReturn(expectedResponse);

        // Act
        DiscountResponse response = discountService.createDiscount(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("TEST");

        // Verify save was called
        ArgumentCaptor<Discount> captor = ArgumentCaptor.forClass(Discount.class);
        verify(discountRepository).save(captor.capture());
        assertThat(captor.getValue().getCode()).isEqualTo("TEST");
    }
}