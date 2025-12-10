package com.example.shoeshop.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.BackEndApplication;
import fpl.sd.backend.constant.OrderConstants;
import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.dto.request.CartItemRequest;
import fpl.sd.backend.dto.request.CreatePaymentOrderRequest;
import fpl.sd.backend.dto.request.OrderRequest;
import fpl.sd.backend.dto.request.PaymentRequest;
import fpl.sd.backend.entity.*;
import fpl.sd.backend.repository.*;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BackEndApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class VNPayControllerIntegrationTest {

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

    private final String TEST_USERNAME = "vn_pay_test_user";
    private final String TEST_PASSWORD = "password123";
    private String variantId;
    private String accessToken;

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
                .email("vn_pay_test@example.com")
                .fullName("VNPay Test User")
                .isActive(true)
                .role(role)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepository.save(user);

        // Setup Shoe
        Shoe shoe = Shoe.builder()
                .name("VNPay Test Shoe")
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

        // Setup ShoeVariant
        ShoeVariant variant = ShoeVariant.builder()
                .shoe(shoe)
                .sizeChart(size)
                .stockQuantity(50)
                .sku("VN-PAY-TEST-SKU-" + System.currentTimeMillis())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        variant = shoeVariantRepository.save(variant);
        this.variantId = variant.getId();

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
    void createPaymentOrder_withValidData_shouldCreateOrderAndReturnPaymentUrl() throws Exception {
        // Arrange
        CartItemRequest cartItem = CartItemRequest.builder()
                .variantId(variantId)
                .quantity(2)
                .price(100.0)
                .productId(1)
                .build();

        CreatePaymentOrderRequest request = CreatePaymentOrderRequest.builder()
                .userId(userRepository.findByUsername(TEST_USERNAME).get().getId())
                .originalTotal(200.0)
                .discountAmount(0.0)
                .finalTotal(200.0)
                .items(java.util.List.of(cartItem))
                .ipAddress("192.168.1.1")
                .build();

        // Act & Assert
        mockMvc.perform(post("/payment/create-payment-order")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());

        // Verify order was created without deducting inventory
        User user = userRepository.findByUsername(TEST_USERNAME).get();
        Optional<CustomerOrder> createdOrder = orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .findFirst();

        assertThat(createdOrder).isPresent();
        assertThat(createdOrder.get().getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.CREATED);
        assertThat(createdOrder.get().getFinalTotal()).isEqualTo(200.0);

        // Verify inventory was NOT deducted (VNPay flow)
        ShoeVariant variant = shoeVariantRepository.findById(variantId).get();
        assertThat(variant.getStockQuantity()).isEqualTo(50);
    }

    @Test
    void createPayment_withExistingOrder_shouldReturnPaymentUrl() throws Exception {
        // Arrange - Create an order first
        CartItemRequest cartItem = CartItemRequest.builder()
                .variantId(variantId)
                .quantity(1)
                .price(100.0)
                .productId(1)
                .build();

        OrderRequest orderRequest = OrderRequest.builder()
                .userId(userRepository.findByUsername(TEST_USERNAME).get().getId())
                .originalTotal(100.0)
                .finalTotal(100.0)
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
        String orderId = createNode.path("result").path("orderId").asText();

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(orderId);
        paymentRequest.setIpAddress("192.168.1.1");

        // Act & Assert
        mockMvc.perform(post("/payment/create-payment")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void createPayment_withNonExistentOrder_shouldReturnError() throws Exception {
        // Arrange
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId("non-existent-order");
        paymentRequest.setIpAddress("192.168.1.1");

        // Act & Assert
        mockMvc.perform(post("/payment/create-payment")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void paymentCallback_withSuccessfulPayment_shouldUpdateOrderStatus() throws Exception {
        // Arrange - Create an order first
        CartItemRequest cartItem = CartItemRequest.builder()
                .variantId(variantId)
                .quantity(1)
                .price(100.0)
                .productId(1)
                .build();

        CreatePaymentOrderRequest createRequest = CreatePaymentOrderRequest.builder()
                .userId(userRepository.findByUsername(TEST_USERNAME).get().getId())
                .originalTotal(100.0)
                .discountAmount(0.0)
                .finalTotal(100.0)
                .items(java.util.List.of(cartItem))
                .ipAddress("192.168.1.1")
                .build();

        MvcResult createResult = mockMvc.perform(post("/payment/create-payment-order")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        JsonNode createNode = objectMapper.readTree(createResponse);
        String paymentUrl = createNode.path("result").asText();

        // Extract order ID from payment URL (simplified - in real scenario would be more complex)
        // For this test, we'll manually get the order ID
        User user = userRepository.findByUsername(TEST_USERNAME).get();
        CustomerOrder order = orderRepository.findAll().stream()
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .findFirst().get();
        String orderId = order.getId();

        // Simulate successful VNPay callback
        String callbackParams = "vnp_OrderInfo=" + orderId +
                "&vnp_TransactionNo=123456" +
                "&vnp_Amount=100000" + // 100.00 * 100
                "&vnp_ResponseCode=00" +
                "&vnp_TransactionStatus=00" +
                "&vnp_BankCode=NCB" +
                "&vnp_CardType=ATM" +
                "&vnp_PayDate=" + System.currentTimeMillis();

        // Act
        mockMvc.perform(get("/payment/payment-callback?" + callbackParams))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.orderStatus").value("PAID"));

        // Verify order status was updated and inventory was deducted
        CustomerOrder updatedOrder = orderRepository.findById(orderId).get();
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.PAID);

        // Verify inventory was deducted after successful payment
        ShoeVariant variant = shoeVariantRepository.findById(variantId).get();
        assertThat(variant.getStockQuantity()).isEqualTo(49); // 50 - 1
    }

    @Test
    void paymentCallback_withFailedPayment_shouldUpdateOrderStatusToFailed() throws Exception {
        // Arrange - Create an order first
        CartItemRequest cartItem = CartItemRequest.builder()
                .variantId(variantId)
                .quantity(1)
                .price(100.0)
                .productId(1)
                .build();

        CreatePaymentOrderRequest createRequest = CreatePaymentOrderRequest.builder()
                .userId(userRepository.findByUsername(TEST_USERNAME).get().getId())
                .originalTotal(100.0)
                .discountAmount(0.0)
                .finalTotal(100.0)
                .items(java.util.List.of(cartItem))
                .ipAddress("192.168.1.1")
                .build();

        mockMvc.perform(post("/payment/create-payment-order")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        User user = userRepository.findByUsername(TEST_USERNAME).get();
        CustomerOrder order = orderRepository.findAll().stream()
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .findFirst().get();
        String orderId = order.getId();

        // Simulate failed VNPay callback
        String callbackParams = "vnp_OrderInfo=" + orderId +
                "&vnp_TransactionNo=123457" +
                "&vnp_Amount=100000" +
                "&vnp_ResponseCode=10" + // Failed response code
                "&vnp_TransactionStatus=02" + // Failed transaction status
                "&vnp_BankCode=NCB" +
                "&vnp_CardType=ATM" +
                "&vnp_PayDate=" + System.currentTimeMillis();

        // Act
        mockMvc.perform(get("/payment/payment-callback?" + callbackParams))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.orderStatus").value("PAYMENT_FAILED"));

        // Verify order status was updated to failed
        CustomerOrder updatedOrder = orderRepository.findById(orderId).get();
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderConstants.OrderStatus.PAYMENT_FAILED);

        // Verify inventory was NOT deducted (payment failed)
        ShoeVariant variant = shoeVariantRepository.findById(variantId).get();
        assertThat(variant.getStockQuantity()).isEqualTo(50); // Still 50
    }

    static class LoginPayload {
        public String username;
        public String password;
        public LoginPayload(String u, String p) { this.username = u; this.password = p; }
    }
}