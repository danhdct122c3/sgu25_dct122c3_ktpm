package fpl.sd.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ShoeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // Login to get token before each test
        String loginRequest = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        MvcResult result = mockMvc.perform(post("/api/v1/auth/token")
                        .contentType("application/json")
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(responseBody)
                .get("result")
                .get("token")
                .asText();
    }

    @Test
    void getAllShoes_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/shoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray());
    }

    @Test
    void getShoeById_WithValidId_ShouldReturn200() throws Exception {
        // Assuming shoe with ID 1 exists
        mockMvc.perform(get("/api/v1/shoes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1));
    }

    @Test
    void getShoeById_WithInvalidId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/shoes/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Product Not Found"));
    }

    @Test
    void createShoe_WithoutAuth_ShouldReturn401() throws Exception {
        String newShoe = """
            {
                "name": "Test Shoe",
                "price": 100.00,
                "brandId": 1,
                "gender": "UNISEX",
                "category": "RUNNING"
            }
        """;

        mockMvc.perform(post("/api/v1/shoes")
                        .contentType("application/json")
                        .content(newShoe))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createShoe_WithAuth_ShouldReturn200() throws Exception {
        String newShoe = """
            {
                "name": "Test Shoe Automated",
                "price": 150.00,
                "brandId": 1,
                "gender": "UNISEX",
                "category": "RUNNING",
                "description": "Test description"
            }
        """;

        mockMvc.perform(post("/api/v1/shoes")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType("application/json")
                        .content(newShoe))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("Test Shoe Automated"));
    }

    @Test
    void getShoesByGender_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/shoes/gender/MAN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray());
    }

    @Test
    void getShoesByBrand_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/shoes/brand/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray());
    }
}
