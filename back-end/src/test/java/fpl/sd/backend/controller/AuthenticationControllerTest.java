package fpl.sd.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpl.sd.backend.dto.request.AuthenticationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authenticate_WithValidCredentials_ShouldReturn200AndToken() throws Exception {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.token").exists())
                .andExpect(jsonPath("$.result.authenticated").value(true));
    }

    @Test
    void authenticate_WithInvalidPassword_ShouldReturn401() throws Exception {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("admin");
        request.setPassword("wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Unauthenticated"));
    }

    @Test
    void authenticate_WithNonExistentUser_ShouldReturn404() throws Exception {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("nonexistent");
        request.setPassword("anypassword");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User Not Found"));
    }
}
