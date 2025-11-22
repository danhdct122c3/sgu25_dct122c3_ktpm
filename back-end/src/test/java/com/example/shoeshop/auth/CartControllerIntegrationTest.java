package com.example.shoeshop.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.BackEndApplication;
import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.constant.ShoeConstants; // Import Enum của bạn
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BackEndApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // Tự động rollback DB sau mỗi test case
public class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Inject Repositories
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

    // Nếu bạn có Repo cho SizeChart và Brand thì Autowired vào, nếu không thì cẩn thận null
    @Autowired(required = false)
    private SizeChartRepository sizeChartRepository;
    @Autowired(required = false)
    private BrandRepository brandRepository;

    private final String TEST_USERNAME = "integration_user";
    private final String TEST_PASSWORD = "password123";
    private String variantId; // ID sẽ được lấy động sau khi save
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Clean Data (Xóa con trước -> cha sau)
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        shoeVariantRepository.deleteAll();
        shoeRepository.deleteAll();
        userRepository.deleteAll();

        // 2. Setup Role user
        Role role = roleRepository.findByRoles(RoleConstants.Role.CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().roles(RoleConstants.Role.CUSTOMER).build()));

        // 3. Setup User (FIXED: Thêm createdAt)
        User user = User.builder()
                .username(TEST_USERNAME)
                .password(new BCryptPasswordEncoder(10).encode(TEST_PASSWORD))
                .email("int_test@example.com")
                .fullName("Integration Tester")
                .isActive(true)
                .role(role)
                .createdAt(Instant.now()) // BẮT BUỘC: Theo Entity User
                .updatedAt(Instant.now())
                .build();
        userRepository.save(user);

        // 4. Setup Shoe (FIXED: Thêm createdAt, gender, category)
        // Lưu ý: Bạn cần chắc chắn Enum Brand/SizeChart tồn tại nếu có ràng buộc
        Shoe shoe = Shoe.builder()
                .name("Nike Air Integration")
                .price(200.0)
                .status(true)
                .createdAt(Instant.now()) // BẮT BUỘC: Theo Entity Shoe
                .updatedAt(Instant.now())
                // Giả định Enum Gender và Category của bạn. Hãy sửa lại nếu tên Enum khác.
                .gender(ShoeConstants.Gender.WOMEN)
                .category(ShoeConstants.Category.RUNNING)
                .shoeImages(new ArrayList<>())
                .build();

        // Nếu Brand không bắt buộc (nullable=true trong code bạn gửi) thì bỏ qua
        // Nếu bắt buộc thì phải tạo Brand trước rồi set vào đây

        shoe = shoeRepository.save(shoe); // Save để lấy ID tự sinh

        // 5. Setup SizeChart (Tạo dummy)
        SizeChart size = SizeChart.builder().sizeNumber(42).build();
        if (sizeChartRepository != null) {
            size = sizeChartRepository.save(size);
        }

        // 6. Setup ShoeVariant (FIXED: Thêm sku, createdAt)
        ShoeVariant variant = ShoeVariant.builder()
                .shoe(shoe)
                .sizeChart(size)
                .stockQuantity(50)
                .sku("TEST-SKU-" + System.currentTimeMillis()) // BẮT BUỘC: Unique & Not Null
                .createdAt(Instant.now()) // BẮT BUỘC: Theo Entity ShoeVariant
                .updatedAt(Instant.now())
                .build();
        variant = shoeVariantRepository.save(variant);
        this.variantId = variant.getId(); // Lấy UUID thật từ DB

        // 7. Login lấy Token
        this.accessToken = obtainAccessToken(TEST_USERNAME, TEST_PASSWORD);
    }

    /**
     * Helper: Login thực tế để lấy JWT
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
        // Arrange
        AddToCartRequest request = new AddToCartRequest();
        request.setVariantId(variantId);
        request.setQuantity(2);

        // Act
        mockMvc.perform(post("/cart/add")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items", hasSize(1)))
                .andExpect(jsonPath("$.result.totalPrice").value(400.0)); // 2 * 200.0

        // Assert DB
        User user = userRepository.findByUsername(TEST_USERNAME).get();
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow();

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(cart.getItems().get(0).getVariant().getId()).isEqualTo(variantId);
    }

    @Test
    void getCart_Success_ShouldReturnCurrentCart() throws Exception {
        // Arrange: Insert tay vào DB
        createCartItemInDb(3);

        // Act
        mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items", hasSize(1)))
                .andExpect(jsonPath("$.result.totalPrice").value(600.0));
    }

    @Test
    void updateCartItem_Success_ShouldUpdateQuantity() throws Exception {
        // Arrange
        createCartItemInDb(1);

        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setVariantId(variantId);
        request.setQuantity(10);

        // Act
        mockMvc.perform(put("/cart/update")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalPrice").value(2000.0));
    }

    @Test
    void removeFromCart_Success_ShouldDeleteItem() throws Exception {
        // Arrange
        createCartItemInDb(2);

        // Act
        mockMvc.perform(delete("/cart/remove/" + variantId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items", hasSize(0)));

        // Assert DB
        User user = userRepository.findByUsername(TEST_USERNAME).get();
        Cart cart = cartRepository.findByUserId(user.getId()).get();
        Optional<CartItem> item = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId);
        assertThat(item).isEmpty();
    }

    @Test
    void clearCart_Success_ShouldRemoveAllItems() throws Exception {
        // Arrange
        createCartItemInDb(5);

        // Act
        mockMvc.perform(delete("/cart/clear")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // Assert DB
        User user = userRepository.findByUsername(TEST_USERNAME).get();
        Cart cart = cartRepository.findByUserId(user.getId()).get();
        assertThat(cartItemRepository.findByCartId(cart.getId())).isEmpty();
    }

    // ================= HELPER METHODS =================

    // Thay thế hàm cũ bằng hàm này ở cuối file CartControllerIntegrationTest.java
    private void createCartItemInDb(int quantity) {
        User user = userRepository.findByUsername(TEST_USERNAME).orElseThrow();

        // Tìm hoặc tạo Cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .items(new ArrayList<>()) // Khởi tạo list rỗng
                            .build();
                    return cartRepository.save(newCart);
                });

        ShoeVariant variant = shoeVariantRepository.findById(variantId).orElseThrow();

        CartItem item = CartItem.builder()
                .cart(cart)
                .variant(variant)
                .quantity(quantity)
                .build();

        // QUAN TRỌNG: Lưu Item xong phải add vào list của Cart để đồng bộ bộ nhớ
        item = cartItemRepository.save(item);

        // Nếu list đang null thì khởi tạo, sau đó add item vào
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        cart.getItems().add(item);

        // Save ngược lại Cart để Hibernate nhận biết sự thay đổi trong list
        cartRepository.save(cart);
    }

    static class LoginPayload {
        public String username;
        public String password;
        public LoginPayload(String u, String p) { this.username = u; this.password = p; }
    }
}