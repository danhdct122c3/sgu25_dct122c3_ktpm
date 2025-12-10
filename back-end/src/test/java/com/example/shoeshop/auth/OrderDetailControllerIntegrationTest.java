package com.example.shoeshop.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.BackEndApplication;
import fpl.sd.backend.constant.OrderConstants;
import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.dto.request.CartItemRequest;
import fpl.sd.backend.dto.request.OrderRequest;
import fpl.sd.backend.dto.response.EnumResponse;
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
@Transactional
public class OrderDetailControllerIntegrationTest {

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
    private CustomerOrderRepository orderRepository;
    @Autowired
    private SizeChartRepository sizeChartRepository;

    private final String TEST_USERNAME = "order_detail_test_user";
    private final String TEST_PASSWORD = "password123";
    private String variantId;
    private String accessToken;
    private String orderId;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up data
        orderRepository.deleteAll();
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
                .email("order_detail_test@example.com")
                .fullName("Order Detail Test User")
                .isActive(true)
                .role(role)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepository.save(user);

        // Setup Shoe
        Shoe shoe = Shoe.builder()
                .name("Order Detail Test Shoe")
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
                .sku("ORDER-DETAIL-TEST-SKU-" + System.currentTimeMillis())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        variant = shoeVariantRepository.save(variant);
        this.variantId = variant.getId();

        // Login to get token
        this.accessToken = obtainAccessToken(TEST_USERNAME, TEST_PASSWORD);

        // Create an order for testing
        this.orderId = createTestOrder();
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

    private String createTestOrder() throws Exception {
        CartItemRequest cartItem = CartItemRequest.builder()
                .variantId(variantId)
                .quantity(2)
                .price(100.0)
                .productId(1)
                .build();

        OrderRequest orderRequest = OrderRequest.builder()
                .userId(userRepository.findByUsername(TEST_USERNAME).get().getId())
                .originalTotal(200.0)
                .finalTotal(200.0)
                .items(java.util.List.of(cartItem))
                .build();

        MvcResult createResult = mockMvc.perform(post("/orders/create")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        JsonNode createNode = objectMapper.readTree(createResponse);
        return createNode.path("result").path("orderId").asText();
    }

    @Test
    void getOrderDetailsByUserId_success_shouldReturnUserOrders() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/order-details/user/" + TEST_USERNAME)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(1)))
                .andExpect(jsonPath("$.result[0].id").value(orderId))
                .andExpect(jsonPath("$.result[0].username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.result[0].finalTotal").value(200.0));
    }

    @Test
    void getOrderDetailsByUserId_noOrders_shouldReturnEmptyList() throws Exception {
        // Arrange - Delete the created order
        orderRepository.deleteAll();

        // Act & Assert
        mockMvc.perform(get("/order-details/user/" + TEST_USERNAME)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(0)));
    }

    @Test
    void getOrderDetailByOrderIdAndUserId_success_shouldReturnOrderDetail() throws Exception {
        // Arrange
        String userId = userRepository.findByUsername(TEST_USERNAME).get().getId();

        // Act & Assert
        mockMvc.perform(get("/order-details/order/" + orderId + "/user/" + userId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(orderId))
                .andExpect(jsonPath("$.result.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.result.finalTotal").value(200.0))
                .andExpect(jsonPath("$.result.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.result.cartItems[0].quantity").value(2))
                .andExpect(jsonPath("$.result.cartItems[0].price").value(100.0));
    }

    @Test
    void getOrderDetailByOrderIdAndUserId_orderNotFound_shouldReturnError() throws Exception {
        // Arrange
        String userId = userRepository.findByUsername(TEST_USERNAME).get().getId();
        String nonExistentOrderId = "non-existent-order";

        // Act & Assert
        mockMvc.perform(get("/order-details/order/" + nonExistentOrderId + "/user/" + userId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrderDetailByOrderIdAndUserId_wrongUser_shouldReturnError() throws Exception {
        // Arrange - Create another user
        Role role = roleRepository.findByRoles(RoleConstants.Role.CUSTOMER).get();
        User otherUser = User.builder()
                .username("other_user")
                .password(new BCryptPasswordEncoder(10).encode("password123"))
                .email("other@example.com")
                .fullName("Other User")
                .isActive(true)
                .role(role)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepository.save(otherUser);

        // Act & Assert
        mockMvc.perform(get("/order-details/order/" + orderId + "/user/" + otherUser.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByOrderStatus_shouldReturnOrderStatusEnums() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/order-details/orderStatus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result[0].name").exists())
                .andExpect(jsonPath("$.result[0].value").exists());
    }

    @Test
    void getOrderDetailsByUserId_unauthorizedUser_shouldReturnError() throws Exception {
        // Arrange - Create another user and try to access their orders
        Role role = roleRepository.findByRoles(RoleConstants.Role.CUSTOMER).get();
        User otherUser = User.builder()
                .username("unauthorized_user")
                .password(new BCryptPasswordEncoder(10).encode("password123"))
                .email("unauthorized@example.com")
                .fullName("Unauthorized User")
                .isActive(true)
                .role(role)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepository.save(otherUser);

        // Act & Assert - Try to access other user's orders
        mockMvc.perform(get("/order-details/user/" + otherUser.getUsername())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrderDetailByOrderIdAndUserId_withDifferentOrderStatuses_shouldReturnCorrectStatus() throws Exception {
        // Arrange - Update order status to DELIVERED
        CustomerOrder order = orderRepository.findById(orderId).get();
        order.setOrderStatus(OrderConstants.OrderStatus.DELIVERED);
        orderRepository.save(order);

        String userId = userRepository.findByUsername(TEST_USERNAME).get().getId();

        // Act & Assert
        mockMvc.perform(get("/order-details/order/" + orderId + "/user/" + userId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.orderStatus").value("DELIVERED"));
    }

    static class LoginPayload {
        public String username;
        public String password;
        public LoginPayload(String u, String p) { this.username = u; this.password = p; }
    }
}