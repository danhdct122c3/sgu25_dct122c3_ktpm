package com.example.shoeshop.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.BackEndApplication;
import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.dto.request.AddToCartRequest;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BackEndApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test") // Dùng profile test (thường là H2 Database)
@Transactional // Rollback dữ liệu sau mỗi @Test để không bị trùng lặp
public class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Inject các Repository để setup dữ liệu thật
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
    // Giả sử bạn có SizeChartRepository, nếu không thì dùng EntityManager để persist
    @Autowired(required = false)
    private SizeChartRepository sizeChartRepository;

    private final String TEST_USERNAME = "integration_user";
    private final String TEST_PASSWORD = "password123";
    private final String VARIANT_ID = "variant-int-test-01";
    private String accessToken; // Token sẽ được lấy tự động trước mỗi test

    @BeforeEach
    void setUp() throws Exception {
        // 1. Clean Data (Xóa con trước, xóa cha sau để tránh lỗi khóa ngoại)
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        shoeVariantRepository.deleteAll();
        shoeRepository.deleteAll();
        userRepository.deleteAll();

        // 2. Setup Role & User
        Role role = roleRepository.findByRoles(RoleConstants.Role.CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().roles(RoleConstants.Role.CUSTOMER).build()));

        User user = User.builder()
                .username(TEST_USERNAME)
                .password(new BCryptPasswordEncoder(10).encode(TEST_PASSWORD))
                .email("int_test@example.com")
                .fullName("Integration Tester")
                .isActive(true)
                .role(role)
                .build();
        userRepository.save(user);

        // 3. Setup Product (Shoe -> Size -> Variant)
        Shoe shoe = Shoe.builder()
                .id(1) // Id có thể tự sinh tùy cấu hình DB của bạn
                .name("Nike Air Test")
                .price(200.0)
                .shoeImages(new ArrayList<>())
                .build();
        shoeRepository.save(shoe);

        SizeChart size = SizeChart.builder().sizeNumber(42).build();
        if (sizeChartRepository != null) sizeChartRepository.save(size);

        ShoeVariant variant = ShoeVariant.builder()
                .id(VARIANT_ID)
                .shoe(shoe)
                .sizeChart(size)
                .stockQuantity(50) // Kho có 50 đôi
                .build();
        shoeVariantRepository.save(variant);

        // 4. Gọi API Login để lấy Token thật
        this.accessToken = obtainAccessToken(TEST_USERNAME, TEST_PASSWORD);
    }

    /**
     * Helper method: Giả lập request Login để lấy JWT Token
     */
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

    // ================= TEST CASES =================

    @Test
    void addToCart_Success_ShouldCreateNewItem() throws Exception {
        // Arrange: Request thêm 2 sản phẩm
        AddToCartRequest request = new AddToCartRequest(); // Dùng DTO của bạn
        request.setVariantId(VARIANT_ID);
        request.setQuantity(2);

        String jsonRequest = objectMapper.writeValueAsString(request);

        // Act: Gọi API POST /cart/add
        mockMvc.perform(post("/cart/add")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items", hasSize(1))) // Assert trong Response
                .andExpect(jsonPath("$.result.totalPrice").value(400.0)); // 2 * 200.0

        // Assert: Kiểm tra Database xem đã lưu thật chưa
        User user = userRepository.findByUsername(TEST_USERNAME).get();
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow();

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(cart.getItems().get(0).getVariant().getId()).isEqualTo(VARIANT_ID);
    }

    @Test
    void getCart_Success_ShouldReturnCurrentCart() throws Exception {
        // Arrange: Tự insert dữ liệu vào DB trước (Giỏ hàng có 1 item số lượng 3)
        createCartItemInDb(3);

        // Act: Gọi API GET /cart
        mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items", hasSize(1)))
                .andExpect(jsonPath("$.result.totalQuantity").value(3))
                .andExpect(jsonPath("$.result.totalPrice").value(600.0)); // 3 * 200.0
    }

    @Test
    void updateCartItem_Success_ShouldUpdateQuantity() throws Exception {
        // Arrange: Có sẵn 1 item số lượng 1
        createCartItemInDb(1);

        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setVariantId(VARIANT_ID);
        request.setQuantity(10); // Đổi thành 10

        // Act: Gọi API PUT /cart/update
        mockMvc.perform(put("/cart/update")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalPrice").value(2000.0)); // 10 * 200.0

        // Assert DB
        User user = userRepository.findByUsername(TEST_USERNAME).get();
        Cart cart = cartRepository.findByUserId(user.getId()).get();
        CartItem item = cartItemRepository.findByCartIdAndVariantId(cart.getId(), VARIANT_ID).get();
        assertThat(item.getQuantity()).isEqualTo(10);
    }

    @Test
    void removeFromCart_Success_ShouldDeleteItem() throws Exception {
        // Arrange: Có sẵn item
        createCartItemInDb(2);

        // Act: Gọi API DELETE /cart/remove/{variantId}
        mockMvc.perform(delete("/cart/remove/" + VARIANT_ID)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items", hasSize(0))); // Response báo list rỗng

        // Assert DB: Item phải biến mất
        User user = userRepository.findByUsername(TEST_USERNAME).get();
        Cart cart = cartRepository.findByUserId(user.getId()).get();
        Optional<CartItem> item = cartItemRepository.findByCartIdAndVariantId(cart.getId(), VARIANT_ID);
        assertThat(item).isEmpty();
    }

    @Test
    void clearCart_Success_ShouldRemoveAllItems() throws Exception {
        // Arrange: Có sẵn item
        createCartItemInDb(5);

        // Act: Gọi API DELETE /cart/clear
        mockMvc.perform(delete("/cart/clear")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart cleared successfully"));

        // Assert DB: Giỏ hàng vẫn còn (User vẫn sở hữu giỏ), nhưng items bên trong phải rỗng
        User user = userRepository.findByUsername(TEST_USERNAME).get();
        Cart cart = cartRepository.findByUserId(user.getId()).get();
        assertThat(cartItemRepository.findByCartId(cart.getId())).isEmpty();
    }

    // ================= HELPER METHODS =================

    /**
     * Helper để tạo nhanh dữ liệu giỏ hàng vào DB mà không cần gọi API
     */
    private void createCartItemInDb(int quantity) {
        User user = userRepository.findByUsername(TEST_USERNAME).orElseThrow();

        // Tìm hoặc tạo Cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).items(new ArrayList<>()).build()));

        ShoeVariant variant = shoeVariantRepository.findById(VARIANT_ID).orElseThrow();

        CartItem item = CartItem.builder()
                .cart(cart)
                .variant(variant)
                .quantity(quantity)
                .build();

        cartItemRepository.save(item);
    }

    // Inner class cho Login Payload (nếu bạn chưa có DTO này trong code main)
    static class LoginPayload {
        public String username;
        public String password;
        public LoginPayload(String u, String p) { this.username = u; this.password = p; }
    }
}