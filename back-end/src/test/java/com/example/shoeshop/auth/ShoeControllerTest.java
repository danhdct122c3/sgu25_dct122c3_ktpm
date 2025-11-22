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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    private String adminToken;
    private Brand brand;

    @BeforeEach
    void setUp() throws Exception {
        shoeRepository.deleteAll();
        brandRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Setup Brand
        brand = Brand.builder()
                .brandName("Adidas")
                .description("Test Brand")
                .isActive(true)
                .logoUrl("http://logo.url")
                .createdAt(Instant.now())
                .build();
        brand = brandRepository.save(brand);

        // 2. Setup Product
        createShoeInDb("Ultra Boost", true);
        createShoeInDb("Old Model", false);

        // 3. Setup ADMIN Account
        // Service yêu cầu ADMIN cho các hàm quản lý, nên dùng quyền cao nhất để test
        Role adminRole = roleRepository.findByRoles(RoleConstants.Role.ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().roles(RoleConstants.Role.ADMIN).build()));

        User admin = User.builder()
                .username("admin_test")
                .password(new BCryptPasswordEncoder(10).encode("123456"))
                .email("admin@test.com")
                .isActive(true)
                .role(adminRole)
                .createdAt(Instant.now())
                .build();
        userRepository.save(admin);

        adminToken = obtainAccessToken("admin_test", "123456");
    }

    @Test
    void createShoe_Manager_ShouldSuccess() throws Exception {
        ShoeCreateRequest request = new ShoeCreateRequest();
        request.setName("Yeezy 350");
        request.setPrice(300.0);
        request.setBrandId(brand.getId());
        request.setStatus(true);

        // --- FIX QUAN TRỌNG: Lấy đúng tên Enum từ ShoeConstants ---
        // Sẽ lấy chuỗi "MAN" và "SNEAKER"
        request.setGender(ShoeConstants.Gender.MAN.name());
        request.setCategory(ShoeConstants.Category.SNEAKER.name());

        // Thêm ảnh để validate thành công
        List<ImageRequest> images = new ArrayList<>();
        ImageRequest img = new ImageRequest();
        img.setUrl("http://img.url");
        images.add(img);
        request.setImages(images);

        mockMvc.perform(post("/shoes")
                        .header("Authorization", "Bearer " + adminToken) // Dùng Token Admin
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print()) // In ra console để debug nếu lỗi
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("Yeezy 350"));

        assertThat(shoeRepository.findAll()).hasSize(3);
    }

    @Test
    void getAllShoesForAdmin_ShouldReturnAll() throws Exception {
        mockMvc.perform(get("/shoes/admin/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(2)));
    }

    @Test
    void deleteShoe_ShouldSoftDelete() throws Exception {
        Shoe activeShoe = shoeRepository.findByStatusTrue().get(0);

        mockMvc.perform(delete("/shoes/" + activeShoe.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk());

        Shoe deletedShoe = shoeRepository.findById(activeShoe.getId()).orElseThrow();
        assertThat(deletedShoe.isStatus()).isFalse();
    }

    // Helper methods
    private void createShoeInDb(String name, boolean status) {
        Shoe shoe = Shoe.builder()
                .name(name)
                .price(100.0)
                .status(status)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .brand(brand)
                // SỬA: Dùng đúng Enum MAN
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
        public String username; public String password;
        public LoginPayload(String u, String p) { this.username=u; this.password=p;}
    }
}