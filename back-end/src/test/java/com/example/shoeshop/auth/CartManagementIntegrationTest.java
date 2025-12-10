package com.example.shoeshop.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.BackEndApplication;
import fpl.sd.backend.constant.DiscountConstants;
import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.dto.request.AddToCartRequest;
import fpl.sd.backend.dto.request.ApplyDiscountRequest;
import fpl.sd.backend.dto.request.UpdateCartItemRequest;
import fpl.sd.backend.entity.*;
import fpl.sd.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BackEndApplication.class)
@AutoConfigureMockMvc
@Transactional
public class CartManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ShoeRepository shoeRepository;
    @Autowired
    private ShoeVariantRepository shoeVariantRepository;
    @Autowired
    private ShoeImageRepository shoeImageRepository;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private CustomerOrderRepository customerOrderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired(required = false)
    private SizeChartRepository sizeChartRepository;

    private final String TEST_USERNAME = "cart_user";
    private final String TEST_PASSWORD = "password123";
    private String variantId;
    private String outOfStockVariantId;
    private String validDiscountCode = "VALID10";
    private String invalidDiscountCode = "INVALID";
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up - IMPORTANT: Delete in correct order to respect foreign key constraints
        // Only delete test data, not all data from database

        // Find and delete only test user's related data
        User existingTestUser = userRepository.findByUsername(TEST_USERNAME).orElse(null);
        if (existingTestUser != null) {
            // 1. Delete cart items for this test user
            Cart userCart = cartRepository.findByUserId(existingTestUser.getId()).orElse(null);
            if (userCart != null) {
                cartItemRepository.deleteAll(userCart.getItems());
                cartRepository.delete(userCart);
            }

            // 2. Delete orders and order details for this test user
            List<CustomerOrder> userOrders = customerOrderRepository.findAll().stream()
                .filter(o -> o.getUser() != null && o.getUser().getId().equals(existingTestUser.getId()))
                .toList();
            for (CustomerOrder order : userOrders) {
                orderDetailRepository.deleteAll(order.getOrderDetails());
            }
            customerOrderRepository.deleteAll(userOrders);

            // 3. Delete the test user
            userRepository.delete(existingTestUser);
        }

        // Delete test discount if exists
        discountRepository.findByCode(validDiscountCode).ifPresent(discountRepository::delete);

        // Delete test shoe variants and shoes
        List<ShoeVariant> testVariants = shoeVariantRepository.findAll().stream()
            .filter(v -> v.getSku() != null && v.getSku().startsWith("TEST-SKU-"))
            .toList();
        shoeVariantRepository.deleteAll(testVariants);

        List<Shoe> testShoes = shoeRepository.findAll().stream()
            .filter(s -> s.getName() != null && s.getName().equals("Test Shoe"))
            .toList();
        for (Shoe shoe : testShoes) {
            shoeImageRepository.deleteAll(shoe.getShoeImages());
        }
        shoeRepository.deleteAll(testShoes);


        // Setup Role
        Role role = roleRepository.findByRoles(RoleConstants.Role.CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().roles(RoleConstants.Role.CUSTOMER).build()));

        // Setup User
        User user = User.builder()
                .username(TEST_USERNAME)
                .password(new BCryptPasswordEncoder(10).encode(TEST_PASSWORD))
                .email("cart_test@example.com")
                .fullName("Cart Tester")
                .isActive(true)
                .role(role)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepository.save(user);

        // Setup Shoe
        Shoe shoe = Shoe.builder()
                .name("Test Shoe")
                .price(100.0)
                .status(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .gender(ShoeConstants.Gender.UNISEX)
                .category(ShoeConstants.Category.RUNNING)
                .shoeImages(new ArrayList<>())
                .build();
        shoe = shoeRepository.save(shoe);

        // Setup SizeChart
        SizeChart size = SizeChart.builder().sizeNumber(42).build();
        if (sizeChartRepository != null) {
            size = sizeChartRepository.save(size);
        }

        // Setup In-stock Variant
        ShoeVariant variant = ShoeVariant.builder()
                .shoe(shoe)
                .sizeChart(size)
                .stockQuantity(10)
                .sku("TEST-SKU-INSTOCK-" + System.currentTimeMillis())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        variant = shoeVariantRepository.save(variant);
        this.variantId = variant.getId();

        // Setup Out-of-stock Variant
        ShoeVariant outOfStockVariant = ShoeVariant.builder()
                .shoe(shoe)
                .sizeChart(size)
                .stockQuantity(0)
                .sku("TEST-SKU-OUTOFSTOCK-" + System.currentTimeMillis())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        outOfStockVariant = shoeVariantRepository.save(outOfStockVariant);
        this.outOfStockVariantId = outOfStockVariant.getId();

        // Setup Valid Discount
        Discount discount = Discount.builder()
                .code(validDiscountCode)
                .description("10% discount")
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .percentage(10.0)
                .minimumOrderAmount(50.0)
                .startDate(Instant.now().minusSeconds(3600))
                .endDate(Instant.now().plusSeconds(3600))
                .isActive(true)
                .usageLimit(100)
                .usedCount(0)
                .build();
        discountRepository.save(discount);

        // Login
        this.accessToken = obtainAccessToken(TEST_USERNAME, TEST_PASSWORD);
    }

    private String obtainAccessToken(String username, String password) throws Exception {
        LoginPayload loginPayload = new LoginPayload(username, password);
        String json = objectMapper.writeValueAsString(loginPayload);

        MvcResult result = mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(responseContent);
        return rootNode.path("result").path("token").asText();
    }

    @Test
    void testSuccessfulCartManagementAndCheckout() throws Exception {
        // Mapping: [Step 1] Khách hàng xem trang chi tiết sản phẩm.
        // (Simulated by having product available)

        // Mapping: [Step 2] Khách hàng chọn nút "Thêm vào Giỏ hàng"
        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setVariantId(variantId);
        addRequest.setQuantity(2);

        mockMvc.perform(post("/cart/add")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items", hasSize(1)))
                .andExpect(jsonPath("$.result.totalPrice").value(200.0));

        // Mapping: [Step 3] Hệ thống thêm sản phẩm vào giỏ hàng và hiển thị thông báo thành công.
        // (Verified above)

        // Mapping: [Step 4] Khách hàng chọn xem Giỏ hàng.
        // Mapping: [Step 5] Hệ thống hiển thị trang Giỏ hàng với danh sách sản phẩm và bảng tóm tắt chi phí.
        mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items", hasSize(1)))
                .andExpect(jsonPath("$.result.totalPrice").value(200.0));

        // Mapping: [Step 6] Khách hàng thay đổi số lượng sản phẩm.
        UpdateCartItemRequest updateRequest = new UpdateCartItemRequest();
        updateRequest.setVariantId(variantId);
        updateRequest.setQuantity(3);

        // Mapping: [Step 7] Hệ thống cập nhật số lượng, tái tính toán tổng tiền.
        mockMvc.perform(put("/cart/update")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalPrice").value(300.0));

        // Mapping: [Step 8] Khách hàng nhập Mã giảm giá.
        ApplyDiscountRequest discountRequest = new ApplyDiscountRequest();
        discountRequest.setDiscount(validDiscountCode);
        discountRequest.setOrderAmount(300.0);

        // Mapping: [Step 9] Hệ thống kiểm tra tính hợp lệ của Mã giảm giá.
        // Mapping: [Step 10] Nếu Mã hợp lệ, áp dụng ưu đãi, cập nhật bảng tóm tắt.
        mockMvc.perform(post("/orders/apply-discount")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(discountRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Apply successfully"));

        // Mapping: [Step 11] Khách hàng xem xét thông tin cuối cùng.
        // Mapping: [Step 12] Khách hàng chọn nút "Tiến hành Thanh toán".
        // Mapping: [Step 13] Hệ thống chuyển Khách hàng sang bước thanh toán.
        // (For integration test, verify cart is ready for checkout)
        // Note: Discount is validated but not applied to cart total - it will be applied during order creation
        mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items", hasSize(1)))
                .andExpect(jsonPath("$.result.totalPrice").value(300.0)); // Cart shows original price, discount applied at checkout
    }

    @Test
    void testAddOutOfStockProduct() throws Exception {
        // Mapping: [Step 2] + [Exception E1] Sản phẩm đã hết hàng
        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setVariantId(outOfStockVariantId);
        addRequest.setQuantity(1);

        // Mapping: [Exception E1] Hiển thị thông báo lỗi và không thêm/cập nhật giỏ hàng
        mockMvc.perform(post("/cart/add")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

        // Verify cart is empty
        mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items", hasSize(0)));
    }
//
    @Test
    void testUpdateQuantityExceedingStock() throws Exception {
        // First add valid item
        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setVariantId(variantId);
        addRequest.setQuantity(2);
        mockMvc.perform(post("/cart/add")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        // Mapping: [Step 6] + [Exception E1] Thay đổi số lượng vượt quá tồn kho
        UpdateCartItemRequest updateRequest = new UpdateCartItemRequest();
        updateRequest.setVariantId(variantId);
        updateRequest.setQuantity(15); // Exceeds stock of 10

        // Mapping: [Exception E1] Hiển thị thông báo lỗi và không cập nhật
        mockMvc.perform(put("/cart/update")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

        // Verify quantity not updated
        mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items[0].quantity").value(2));
    }

    @Test
    void testApplyInvalidDiscountCode() throws Exception {
        // First add item to cart
        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setVariantId(variantId);
        addRequest.setQuantity(2);
        mockMvc.perform(post("/cart/add")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        // Mapping: [Step 8] + [Exception E2] Mã giảm giá không hợp lệ
        ApplyDiscountRequest discountRequest = new ApplyDiscountRequest();
        discountRequest.setDiscount(invalidDiscountCode);
        discountRequest.setOrderAmount(200.0);

        // Mapping: [Exception E2] Hiển thị thông báo lỗi cụ thể
        mockMvc.perform(post("/orders/apply-discount")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(discountRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Invalid Coupon"));

        // Verify discount not applied
        mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalPrice").value(200.0)); // No discount
    }

    // Note: For A1 (save/sync cart), this would require session management testing
    // which is complex in integration tests. In a real scenario, test logout/login
    // and verify cart persistence, but for this demo, we'll skip as it's not
    // directly testable with current endpoints.

    static class LoginPayload {
        public String username;
        public String password;
        public LoginPayload(String u, String p) { this.username = u; this.password = p; }
    }
}
