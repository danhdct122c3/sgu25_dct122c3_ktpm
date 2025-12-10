package com.example.shoeshop.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.BackEndApplication;
import fpl.sd.backend.constant.DiscountConstants;
import fpl.sd.backend.constant.OrderConstants;
import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.dto.request.ApplyDiscountRequest;
import fpl.sd.backend.dto.request.CartItemRequest;
import fpl.sd.backend.dto.request.OrderRequest;
import fpl.sd.backend.entity.*;
import fpl.sd.backend.repository.*;
import fpl.sd.backend.repository.SizeChartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BackEndApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ShoeRepository shoeRepository;
    @Autowired
    private ShoeVariantRepository shoeVariantRepository;
    @Autowired
    private SizeChartRepository sizeChartRepository;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private CustomerOrderRepository orderRepository;

    private final String TEST_USERNAME = "order_test_user";
    private final String TEST_PASSWORD = "password123";
    private String variantId;
    private String discountCode;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up data
        orderRepository.deleteAll();
        discountRepository.deleteAll();
        shoeVariantRepository.deleteAll();
        shoeRepository.deleteAll();
        userRepository.deleteAll();

        // Setup Role
        Role role = roleRepository.findByRoles(RoleConstants.Role.CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().roles(RoleConstants.Role.CUSTOMER).build()));

        // Setup User
        User user = User.builder()
                .username(TEST_USERNAME)
                .password(new BCryptPasswordEncoder(10).encode(TEST_PASSWORD))
                .email("order_test@example.com")
                .fullName("Order Test User")
                .isActive(true)
                .role(role)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepository.save(user);

        // Setup Shoe
        Shoe shoe = Shoe.builder()
                .name("Order Test Shoe")
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
        size = sizeChartRepository.save(size);

        // Setup ShoeVariant
        ShoeVariant variant = ShoeVariant.builder()
                .shoe(shoe)
                .sizeChart(size)
                .stockQuantity(50)
                .sku("ORDER-TEST-SKU-" + System.currentTimeMillis())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        variant = shoeVariantRepository.save(variant);
        this.variantId = variant.getId();

        // Setup Discount
        Discount discount = Discount.builder()
                .code("SAVE10")
                .description("10% discount for orders over $50")
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .percentage(10.0)
                .minimumOrderAmount(50.0)
                .usageLimit(100)
                .usedCount(0)
                .isActive(true)
                .startDate(Instant.now().minusSeconds(3600))
                .endDate(Instant.now().plusSeconds(3600))
                .build();
        discountRepository.save(discount);
        this.discountCode = discount.getCode();

        // Login to get token
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
    void createOrder_withValidData_shouldCreateOrderSuccessfully() throws Exception {
        // Arrange
        CartItemRequest cartItem = CartItemRequest.builder()
                .variantId(variantId)
                .quantity(2)
                .price(100.0)
                .productId(1)
                .build();

        OrderRequest orderRequest = OrderRequest.builder()
                .userId(userRepository.findByUsername(TEST_USERNAME).get().getId())
                .originalTotal(200.0)
                .discountAmount(0.0)
                .finalTotal(200.0)
                .items(java.util.List.of(cartItem))
                .build();

        // Act & Assert
        mockMvc.perform(post("/orders/create")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.orderId").exists())
                .andExpect(jsonPath("$.result.finalTotal").value(200.0));

        // Verify order was created in database
        User user = userRepository.findByUsername(TEST_USERNAME).get();
        Optional<CustomerOrder> createdOrder = orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .findFirst();

        assertThat(createdOrder).isPresent();
        assertThat(createdOrder.get().getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.CREATED);
        assertThat(createdOrder.get().getFinalTotal()).isEqualTo(200.0);
    }

    @Test
    void createOrder_withInvalidData_shouldReturnValidationError() throws Exception {
        // Arrange - Empty items list
        OrderRequest orderRequest = OrderRequest.builder()
                .userId(userRepository.findByUsername(TEST_USERNAME).get().getId())
                .originalTotal(0.0)
                .finalTotal(0.0)
                .items(java.util.List.of()) // Empty items
                .build();

        // Act & Assert
        mockMvc.perform(post("/orders/create")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void applyDiscount_withValidCode_shouldApplyDiscountSuccessfully() throws Exception {
        // Arrange
        ApplyDiscountRequest request = new ApplyDiscountRequest();
        request.setDiscount(discountCode);
        request.setOrderAmount(100.0);

        // Act & Assert
        mockMvc.perform(post("/orders/apply-discount")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.coupon").value(discountCode))
                .andExpect(jsonPath("$.result.percentage").value(10.0))
                .andExpect(jsonPath("$.result.discountType").value("PERCENTAGE"));
    }

    @Test
    void applyDiscount_withInvalidCode_shouldReturnError() throws Exception {
        // Arrange
        ApplyDiscountRequest request = new ApplyDiscountRequest();
        request.setDiscount("INVALID_CODE");
        request.setOrderAmount(100.0);

        // Act & Assert
        mockMvc.perform(post("/orders/apply-discount")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Invalid Coupon"));
    }

    @Test
    void applyDiscount_withInsufficientOrderAmount_shouldReturnError() throws Exception {
        // Arrange
        ApplyDiscountRequest request = new ApplyDiscountRequest();
        request.setDiscount(discountCode);
        request.setOrderAmount(30.0); // Below minimum 50.0

        // Act & Assert
        mockMvc.perform(post("/orders/apply-discount")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("The order total does not meet the minimum amount required for this discount."));
    }


    static class LoginPayload {
        public String username;
        public String password;
        public LoginPayload(String u, String p) { this.username = u; this.password = p; }
    }
}