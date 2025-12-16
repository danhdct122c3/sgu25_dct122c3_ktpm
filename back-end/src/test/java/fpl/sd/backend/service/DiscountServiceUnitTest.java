package fpl.sd.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.ai.chat.ChatClient;
import fpl.sd.backend.constant.DiscountConstants;
import fpl.sd.backend.dto.request.DiscountCreateRequest;
import fpl.sd.backend.dto.response.DiscountResponse;
import fpl.sd.backend.entity.*;
import fpl.sd.backend.mapper.DiscountMapper;
import fpl.sd.backend.repository.DiscountCategoryRepository;
import fpl.sd.backend.repository.DiscountRepository;
import fpl.sd.backend.repository.DiscountShoeRepository;
import fpl.sd.backend.repository.ShoeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiscountServiceUnitTest {

    // ===== MOCKS =====
    @Mock private DiscountRepository discountRepository;
    @Mock private DiscountMapper discountMapper;
    @Mock private DiscountCategoryRepository discountCategoryRepository;
    @Mock private DiscountShoeRepository discountShoeRepository;
    @Mock private ShoeRepository shoeRepository;
    @Mock private ObjectMapper objectMapper;
    @Mock private ChatClient chatClient;

    // ===== SERVICES =====
    @InjectMocks
    private DiscountService discountService;

    private DiscountValidationService discountValidationService;

    // ===== DUMMY DATA =====
    private List<OrderDetail> orderDetails500k;
    private List<OrderDetail> orderDetails100k;

    @BeforeEach
    void setUp() {
        discountValidationService = new DiscountValidationService(
                discountCategoryRepository,
                discountShoeRepository
        );

        // Cart total = 500,000
        Shoe shoe500 = Shoe.builder().id(1).price(500000.0).build();
        ShoeVariant variant500 = ShoeVariant.builder().shoe(shoe500).build();
        OrderDetail od500 = OrderDetail.builder()
                .variant(variant500)
                .price(500000.0)
                .quantity(1)
                .build();
        orderDetails500k = Collections.singletonList(od500);

        // Cart total = 100,000
        Shoe shoe100 = Shoe.builder().id(2).price(100000.0).build();
        ShoeVariant variant100 = ShoeVariant.builder().shoe(shoe100).build();
        OrderDetail od100 = OrderDetail.builder()
                .variant(variant100)
                .price(100000.0)
                .quantity(1)
                .build();
        orderDetails100k = Collections.singletonList(od100);
    }

    // ======================================================================
    // CUSTOMER – CALCULATE DISCOUNT
    // ======================================================================

    @DisplayName("TC_DM_001 | Áp dụng mã hợp lệ | Cart=500k, discount=20% | Giảm đúng 100k")
    @Test
    void applyValidDiscount_shouldCalculateCorrectly() {
        // Arrange
        Discount discount = Discount.builder()
                .code("TEST2")
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .percentage(20.0)
                .startDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .usageLimit(100)
                .usedCount(0)
                .build();

        when(discountCategoryRepository.findByDiscount(discount)).thenReturn(Collections.emptyList());
        when(discountShoeRepository.findByDiscount(discount)).thenReturn(Collections.emptyList());

        // Act
        double amount = discountValidationService.calculateDiscountAmount(discount, orderDetails500k);

        // Assert
        assertThat(amount).isEqualTo(100000.0);
    }

    @DisplayName("TC_DM_002 | Mã hết hạn | endDate < now | Không được giảm")
    @Test
    void applyExpiredDiscount_shouldReturnZero() {
        // Arrange
        Discount discount = Discount.builder()
                .percentage(10.0)
                .endDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();

        // Act
        double amount = discountValidationService.calculateDiscountAmount(discount, orderDetails500k);

        // Assert
        assertThat(amount).isEqualTo(0.0);
    }

    @DisplayName("TC_DM_003 | Mã đã dùng hết | usedCount = usageLimit | Không được giảm")
    @Test
    void applyUsedUpDiscount_shouldReturnZero() {
        // Arrange
        Discount discount = Discount.builder()
                .percentage(50.0)
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .usageLimit(10)
                .usedCount(10)
                .build();

        // Act
        double amount = discountValidationService.calculateDiscountAmount(discount, orderDetails500k);

        // Assert
        assertThat(amount).isEqualTo(0.0);
    }

    @DisplayName("TC_DM_004 | Cart < minimumOrderAmount | Không được giảm")
    @Test
    void cartBelowMinimumOrder_shouldReturnZero() {
        // Arrange
        Discount discount = Discount.builder()
                .minimumOrderAmount(200000.0)
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        // Act
        double amount = discountValidationService.calculateDiscountAmount(discount, orderDetails100k);

        // Assert
        assertThat(amount).isEqualTo(0.0);
    }

    @DisplayName("TC_DM_006 | User xóa mã | discount=null | Trả về 0")
    @Test
    void removeDiscount_shouldReturnZero() {
        // Act
        double amount = discountValidationService.calculateDiscountAmount(null, orderDetails500k);

        // Assert
        assertThat(amount).isEqualTo(0.0);
    }

    @DisplayName("TC_DM_007 | Nhập nhiều mã | Chỉ áp dụng mã mới nhất")
    @Test
    void multipleDiscount_shouldUseNewestCode() {
        // Arrange
        Discount discount = Discount.builder()
                .code("SAVE20")
                .percentage(20.0)
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        when(discountCategoryRepository.findByDiscount(discount)).thenReturn(Collections.emptyList());
        when(discountShoeRepository.findByDiscount(discount)).thenReturn(Collections.emptyList());

        // Act
        double amount = discountValidationService.calculateDiscountAmount(discount, orderDetails500k);

        // Assert
        assertThat(amount).isEqualTo(100000.0);
    }

    // ======================================================================
    // SEARCH DISCOUNT
    // ======================================================================

    @DisplayName("TC_DM_008 | Tìm mã không phân biệt hoa thường | input=test3 | Trả về TEST3")
    @Test
    void findDiscount_ignoreCase_shouldReturnResult() {
        // Arrange
        String input = "test3";
        Discount discount = Discount.builder().code("TEST3").build();

        when(discountRepository.findDiscountsByCodeContainingIgnoreCase(input))
                .thenReturn(List.of(discount));

        DiscountResponse response = new DiscountResponse();
        response.setCode("TEST3");
        when(discountMapper.toDiscountResponse(discount)).thenReturn(response);

        // Act
        List<DiscountResponse> result = discountService.getDiscountsByCode(input);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCode()).isEqualTo("TEST3");
    }

    // ======================================================================
    // MANAGER – CREATE DISCOUNT
    // ======================================================================

    @DisplayName("TC_DM_009 | Manager tạo mã mới | Code chưa tồn tại | Tạo thành công")
    @Test
    void managerCreateDiscount_shouldSuccess() {
        // Arrange
        DiscountCreateRequest request = new DiscountCreateRequest();
        request.setCode("TEST");
        request.setDiscountType(DiscountConstants.DiscountType.PERCENTAGE);
        request.setPercentage(10.0);
        request.setStartDate(Instant.now());
        request.setEndDate(Instant.now().plus(10, ChronoUnit.DAYS));

        Discount entity = new Discount();
        entity.setCode("TEST");

        when(discountRepository.existsByCode("TEST")).thenReturn(false);
        when(discountMapper.toDiscount(request)).thenReturn(entity);
        when(discountRepository.save(any(Discount.class))).thenReturn(entity);

        DiscountResponse response = new DiscountResponse();
        response.setCode("TEST");
        when(discountMapper.toDiscountResponse(entity)).thenReturn(response);

        // Act
        DiscountResponse result = discountService.createDiscount(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("TEST");

        ArgumentCaptor<Discount> captor = ArgumentCaptor.forClass(Discount.class);
        verify(discountRepository).save(captor.capture());
        assertThat(captor.getValue().getCode()).isEqualTo("TEST");
    }
}
