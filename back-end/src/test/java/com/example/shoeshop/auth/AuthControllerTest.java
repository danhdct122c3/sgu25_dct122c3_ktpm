package com.example.shoeshop.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import fpl.sd.backend.BackEndApplication;
import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.entity.InvalidatedToken;
import fpl.sd.backend.entity.Role;
import fpl.sd.backend.entity.User;
import fpl.sd.backend.repository.InvalidatedTokenRepository;
import fpl.sd.backend.repository.RoleRepository;
import fpl.sd.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BackEndApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;


    private final String TEST_EMAIL = "testuser@example.com";
    private final String RAW_PASSWORD = "12345678";

    @BeforeEach
    public void setUp() {
        // Clean up
        invalidatedTokenRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Ensure role CUSTOMER exists
        Role role = roleRepository.findByRoles(RoleConstants.Role.CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roles(RoleConstants.Role.CUSTOMER)
                        .build()));

        // Create user with bcrypt encoded password matching AuthenticationService (BCrypt 10)
        String encoded = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);

        User u = User.builder()
                .username(TEST_EMAIL) // AuthenticationService uses username field for login
                .password(encoded)
                .email(TEST_EMAIL)
                .address(null)
                .phone(null)
                .fullName("Test User")
                .isActive(true)
                .createdAt(Instant.now())
                .role(role)
                .build();

        userRepository.save(u);
    }

    @Test
    public void login_success_shouldReturnToken() throws Exception {
        // Given
        String json = objectMapper.writeValueAsString(
                new LoginPayload(TEST_EMAIL, RAW_PASSWORD)
        );

        // When
        var mvcResult = mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        String resp = mvcResult.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(resp);
        // APIResponse structure: { flag: true, code:200, message:..., result: { authenticated:true, token: "..." } }
        JsonNode result = root.path("result");
        assertThat(result.isMissingNode()).isFalse();
        assertThat(result.path("authenticated").asBoolean()).isTrue();
        String token = result.path("token").asText();
        assertThat(token).isNotBlank();

        // Validate token structure parseable
        SignedJWT signedJWT = SignedJWT.parse(token);
        assertThat(signedJWT.getJWTClaimsSet().getSubject()).isEqualTo(TEST_EMAIL);
    }

    @Test
    public void login_fail_wrongPassword() throws Exception {
        String json = objectMapper.writeValueAsString(
                new LoginPayload(TEST_EMAIL, "wrongpassword")
        );

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void login_fail_userNotFound() throws Exception {
        String json = objectMapper.writeValueAsString(
                new LoginPayload("unknown@example.com", "whatever")
        );

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void logout_success_shouldInvalidateToken() throws Exception {
        // First login to obtain token
        String loginJson = objectMapper.writeValueAsString(new LoginPayload(TEST_EMAIL, RAW_PASSWORD));
        var mvcResult = mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String resp = mvcResult.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(resp);
        String token = root.path("result").path("token").asText();
        assertThat(token).isNotBlank();

        // Extract jti from token
        SignedJWT signedJWT = SignedJWT.parse(token);
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        assertThat(jti).isNotBlank();

        // Call logout with Authorization header (must be authenticated) and body containing token
        String logoutJson = objectMapper.writeValueAsString(new LogoutPayload(token));

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(logoutJson))
                .andExpect(status().isOk());

        // Verify invalidated token saved in DB
        boolean exists = invalidatedTokenRepository.existsById(jti);
        assertThat(exists).isTrue();
    }

    @Test
    public void logout_fail_invalidToken() throws Exception {
        String badToken = "invalid.token.here";
        String logoutJson = objectMapper.writeValueAsString(new LogoutPayload(badToken));

        // Expect the security filter/decoder to reject the token and throw an exception during processing.
        // We assert that performing the request results in an exception (JwtException / AuthenticationServiceException).
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + badToken)
                            .content(logoutJson))
                    .andReturn();
        });
    }

    // small helper DTOs for request payloads
    static class LoginPayload {
        public String username;
        public String password;

        public LoginPayload(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    static class LogoutPayload {
        public String token;

        public LogoutPayload(String token) {
            this.token = token;
        }
    }
}
