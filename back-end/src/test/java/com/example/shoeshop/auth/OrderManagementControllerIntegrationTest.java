package com.example.shoeshop.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.BackEndApplication;
import fpl.sd.backend.constant.OrderConstants;
import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.dto.request.CartItemRequest;
import fpl.sd.backend.dto.request.OrderRequest;
import fpl.sd.backend.dto.request.OrderUpdateRequest;
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
public class OrderManagementControllerIntegrationTest {

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

    private final String STAFF_USERNAME = "staff_order_test";
    private final String MANAGER_USERNAME = "manager_order_test";
    private final String CUSTOMER_USERNAME = "customer_order_test";
    private final String STAFF_PASSWORD = "password123";
    private final String MANAGER_PASSWORD = "password123";
    private final String CUSTOMER_PASSWORD = "password123";

    private String variantId;
    private String staffAccessToken;
    private String managerAccessToken;
    private String customerAccessToken;
    private String orderId;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up data
        orderRepository.deleteAll();
        shoeVariantRepository.deleteAll();
        shoeRepository.deleteAll();
        userRepository.deleteAll();

        // Setup roles
        Role customerRole = roleRepository.findByRoles(RoleConstants.Role.CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().roles(RoleConstants.Role.CUSTOMER).build()));
        Role staffRole = roleRepository.findByRoles(RoleConstants.Role.STAFF)
                .orElseGet(() -> roleRepository.save(Role.builder().roles(RoleConstants.Role.STAFF).build()));
        Role managerRole = roleRepository.findByRoles(RoleConstants.Role.MANAGER)
                .orElseGet(() -> roleRepository.save(Role.builder().roles(RoleConstants.Role.MANAGER).build()));

        // Setup Staff User
        User staffUser = User.builder()
                .username(STAFF_USERNAME)
                .password(new BCryptPasswordEncoder(10).encode(STAFF_PASSWORD))
                .email("staff_order_test@example.com")
                .fullName("Staff Order Test")
                .isActive(true)
                .role(staffRole)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepository.save(staffUser);

        // Setup Manager User
        User managerUser = User.builder()
                .username(MANAGER_USERNAME)
                .password(new BCryptPasswordEncoder(10).encode(MANAGER_PASSWORD))
                .email("manager_order_test@example.com")
                .fullName("Manager Order Test")
                .isActive(true)
                .role(managerRole)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepository.save(managerUser);

        // Setup Customer User
        User customerUser = User.builder()
                .username(CUSTOMER_USERNAME)
                .password(new BCryptPasswordEncoder(10).encode(CUSTOMER_PASSWORD))
                .email("customer_order_test@example.com")
                .fullName("Customer Order Test")
                .isActive(true)
                .role(customerRole)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepository.save(customerUser);

        // Setup Shoe
        Shoe shoe = Shoe.builder()
                .name("Order Management Test Shoe")
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
                .sku("ORDER-MGMT-TEST-SKU-" + System.currentTimeMillis())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        variant = shoeVariantRepository.save(variant);
        this.variantId = variant.getId();

        // Login to get tokens
        this.staffAccessToken = obtainAccessToken(STAFF_USERNAME, STAFF_PASSWORD);
        this.managerAccessToken = obtainAccessToken(MANAGER_USERNAME, MANAGER_PASSWORD);
        this.customerAccessToken = obtainAccessToken(CUSTOMER_USERNAME, CUSTOMER_PASSWORD);

        // Create a test order
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
                .userId(userRepository.findByUsername(CUSTOMER_USERNAME).get().getId())
                .originalTotal(200.0)
                .finalTotal(200.0)
                .items(java.util.List.of(cartItem))
                .build();

        MvcResult createResult = mockMvc.perform(post("/orders/create")
                        .header("Authorization", "Bearer " + customerAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        JsonNode createNode = objectMapper.readTree(createResponse);
        return createNode.path("result").path("orderId").asText();
    }

    @Test
    void staffViewAllOrders_shouldReturnAllCustomerOrders() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/order-details")
                        .header("Authorization", "Bearer " + staffAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(1)))
                .andExpect(jsonPath("$.result[0].id").value(orderId))
                .andExpect(jsonPath("$.result[0].orderStatus").value("CREATED"));
    }

    @Test
    void managerViewAllOrders_shouldReturnAllCustomerOrders() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/order-details")
                        .header("Authorization", "Bearer " + managerAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(1)))
                .andExpect(jsonPath("$.result[0].id").value(orderId))
                .andExpect(jsonPath("$.result[0].orderStatus").value("CREATED"));
    }

    @Test
    void staffViewOrderDetail_shouldReturnOrderWithCustomerInfo() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/order-details/order/" + orderId)
                        .header("Authorization", "Bearer " + staffAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(orderId))
                .andExpect(jsonPath("$.result.username").value(CUSTOMER_USERNAME))
                .andExpect(jsonPath("$.result.fullName").value("Customer Order Test"))
                .andExpect(jsonPath("$.result.email").value("customer_order_test@example.com"))
                .andExpect(jsonPath("$.result.finalTotal").value(200.0))
                .andExpect(jsonPath("$.result.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.result.cartItems[0].quantity").value(2));
    }

    @Test
    void managerViewOrderDetail_shouldReturnOrderWithCustomerInfo() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/order-details/order/" + orderId)
                        .header("Authorization", "Bearer " + managerAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(orderId))
                .andExpect(jsonPath("$.result.username").value(CUSTOMER_USERNAME))
                .andExpect(jsonPath("$.result.fullName").value("Customer Order Test"))
                .andExpect(jsonPath("$.result.finalTotal").value(200.0));
    }

    @Test
    void staffUpdateOrderStatus_toConfirmed_shouldSucceed() throws Exception {
        // Arrange
        OrderUpdateRequest updateRequest = new OrderUpdateRequest();
        updateRequest.setOrderStatus(OrderConstants.OrderStatus.CONFIRMED);

        // Act & Assert
        mockMvc.perform(put("/order-details/order/" + orderId)
                        .header("Authorization", "Bearer " + staffAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(orderId))
                .andExpect(jsonPath("$.result.orderStatus").value("CONFIRMED"))
                .andExpect(jsonPath("$.message").value("Order updated successfully"));

        // Verify status was updated in database
        CustomerOrder updatedOrder = orderRepository.findById(orderId).get();
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.CONFIRMED);
    }

    @Test
    void managerUpdateOrderStatus_toConfirmed_shouldSucceed() throws Exception {
        // Arrange
        OrderUpdateRequest updateRequest = new OrderUpdateRequest();
        updateRequest.setOrderStatus(OrderConstants.OrderStatus.CONFIRMED);

        // Act & Assert
        mockMvc.perform(put("/order-details/order/" + orderId)
                        .header("Authorization", "Bearer " + managerAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(orderId))
                .andExpect(jsonPath("$.result.orderStatus").value("CONFIRMED"));

        // Verify status was updated in database
        CustomerOrder updatedOrder = orderRepository.findById(orderId).get();
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.CONFIRMED);
    }

    @Test
    void staffUpdateOrderStatus_invalidTransition_shouldFail() throws Exception {
        // Arrange - Try to jump from CREATED directly to DELIVERED (invalid)
        OrderUpdateRequest updateRequest = new OrderUpdateRequest();
        updateRequest.setOrderStatus(OrderConstants.OrderStatus.DELIVERED);

        // Act & Assert
        mockMvc.perform(put("/order-details/order/" + orderId)
                        .header("Authorization", "Bearer " + staffAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void staffUpdateOrderStatus_toCancelled_shouldRestoreInventory() throws Exception {
        // Arrange
        OrderUpdateRequest updateRequest = new OrderUpdateRequest();
        updateRequest.setOrderStatus(OrderConstants.OrderStatus.CANCELLED);

        // Act & Assert
        mockMvc.perform(put("/order-details/order/" + orderId)
                        .header("Authorization", "Bearer " + staffAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.orderStatus").value("CANCELLED"));

        // Verify inventory was restored (50 + 2 = 52)
        ShoeVariant variant = shoeVariantRepository.findById(variantId).get();
        assertThat(variant.getStockQuantity()).isEqualTo(52);
    }

    @Test
    void customerAccessStaffEndpoints_shouldBeForbidden() throws Exception {
        // Act & Assert - Customer trying to view all orders
        mockMvc.perform(get("/order-details")
                        .header("Authorization", "Bearer " + customerAccessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void customerUpdateOrderStatus_shouldBeForbidden() throws Exception {
        // Arrange
        OrderUpdateRequest updateRequest = new OrderUpdateRequest();
        updateRequest.setOrderStatus(OrderConstants.OrderStatus.CONFIRMED);

        // Act & Assert
        mockMvc.perform(put("/order-details/order/" + orderId)
                        .header("Authorization", "Bearer " + customerAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrderPaging_withStatusFilter_shouldReturnFilteredResults() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/order-details/list-order")
                        .param("orderStatus", "CREATED")
                        .param("page", "1")
                        .param("size", "8")
                        .param("sortOrder", "date")
                        .header("Authorization", "Bearer " + staffAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data", hasSize(1)))
                .andExpect(jsonPath("$.result.data[0].id").value(orderId));
    }

    @Test
    void getOrderPaging_withoutStatusFilter_shouldReturnAllOrders() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/order-details/list-order")
                        .param("page", "1")
                        .param("size", "8")
                        .param("sortOrder", "date")
                        .header("Authorization", "Bearer " + managerAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data", hasSize(1)));
    }

    static class LoginPayload {
        public String username;
        public String password;
        public LoginPayload(String u, String p) { this.username = u; this.password = p; }
    }
}