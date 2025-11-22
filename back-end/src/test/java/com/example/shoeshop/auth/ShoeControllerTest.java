package com.example.shoeshop.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.BackEndApplication;
import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.dto.request.ImageRequest;
import fpl.sd.backend.dto.request.ShoeCreateRequest;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BackEndApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ShoeControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired ShoeRepository shoeRepository;
    @Autowired BrandRepository brandRepository;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;

    private String managerToken;
    private Brand brand;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Clean Data
        shoeRepository.deleteAll();
        brandRepository.deleteAll();
        userRepository.deleteAll();

        // 2. Setup Brand
        // FIX: Thêm logoUrl để tránh lỗi DataIntegrityViolation
        brand = Brand.builder()
                .brandName("Adidas")
                .description("Test Brand Description")
                .isActive(true)
                .logoUrl("http://example.com/logo.png") // <--- ĐÃ THÊM DÒNG NÀY
                .createdAt(Instant.now())
                .build();
        brand = brandRepository.save(brand);

        // 3. Setup Product
        createShoeInDb("Ultra Boost", true);
        createShoeInDb("Old Model", false);

        // 4. Setup Manager Account
        Role managerRole = roleRepository.findByRoles(RoleConstants.Role.MANAGER)
                .orElseGet(() -> roleRepository.save(Role.builder().roles(RoleConstants.Role.MANAGER).build()));

        User manager = User.builder()
                .username("manager")
                .password(new BCryptPasswordEncoder(10).encode("123456"))
                .email("manager@test.com")
                .isActive(true)
                .role(managerRole)
                .createdAt(Instant.now())
                .build();
        userRepository.save(manager);

        managerToken = obtainAccessToken("manager", "123456");
    }

    @Test
    void getAllShoes_Public_ShouldReturnOnlyActiveShoes() throws Exception {
        mockMvc.perform(get("/shoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(1)))
                .andExpect(jsonPath("$.result[0].name").value("Ultra Boost"));
    }

    @Test
    void getAllShoesForAdmin_Manager_ShouldReturnAll() throws Exception {
        mockMvc.perform(get("/shoes/admin/all")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(2)));
    }

    @Test
    void createShoe_Manager_ShouldSuccess() throws Exception {
        ShoeCreateRequest request = new ShoeCreateRequest();
        request.setName("Yeezy 350");
        request.setPrice(300.0);
        request.setBrandId(brand.getId());
        request.setGender("MAN");
        request.setCategory("SNEAKER");
        request.setStatus(true);

        // FIX: Thêm ảnh vào list và gán vào request
        List<ImageRequest> images = new ArrayList<>();
        ImageRequest imgReq = new ImageRequest();
        imgReq.setUrl("http://test-image.com/shoe.jpg");
        images.add(imgReq); // <--- ĐÃ THÊM DÒNG NÀY (Add item to list)

        request.setImages(images); // <--- ĐÃ THÊM DÒNG NÀY (Set list to request)

        mockMvc.perform(post("/shoes")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("Yeezy 350"));

        // Verify DB
        assertThat(shoeRepository.findAll()).hasSize(3);
    }

    @Test
    void createShoe_MissingImages_ShouldFail() throws Exception {
        ShoeCreateRequest request = new ShoeCreateRequest();
        request.setName("Fail Shoe");
        // Không set Images (Required)

        mockMvc.perform(post("/shoes")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteShoe_Manager_ShouldSoftDelete() throws Exception {
        Shoe activeShoe = shoeRepository.findByStatusTrue().get(0);

        mockMvc.perform(delete("/shoes/" + activeShoe.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());

        Shoe deletedShoe = shoeRepository.findById(activeShoe.getId()).orElseThrow();
        assertThat(deletedShoe.isStatus()).isFalse();
    }

    private void createShoeInDb(String name, boolean status) {
        Shoe shoe = Shoe.builder()
                .name(name)
                .price(100.0)
                .status(status)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .brand(brand)
                .gender(ShoeConstants.Gender.MAN)
                .category(ShoeConstants.Category.SNEAKER)
                .shoeImages(new ArrayList<>())
                .build();
        shoeRepository.save(shoe);
    }

    private String obtainAccessToken(String u, String p) throws Exception {
        String json = objectMapper.writeValueAsString(new LoginPayload(u, p));
        MvcResult result = mockMvc.perform(post("/auth/token").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("result").path("token").asText();
    }

    static class LoginPayload {
        public String username;
        public String password;
        public LoginPayload(String u, String p) { this.username=u; this.password=p;}
    }
}