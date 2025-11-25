package com.example.shoeshop.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import fpl.sd.backend.BackEndApplication;
import fpl.sd.backend.constant.DiscountConstants;
import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.dto.request.DiscountCreateRequest;
import fpl.sd.backend.dto.request.DiscountUpdateRequest;
import fpl.sd.backend.entity.Discount;
import fpl.sd.backend.entity.Role;
import fpl.sd.backend.entity.User;
import fpl.sd.backend.repository.DiscountRepository;
import fpl.sd.backend.repository.RoleRepository;
import fpl.sd.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BackEndApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    private final String CUSTOMER_USERNAME = "customer_test";
    private final String MANAGER_USERNAME = "manager_test";
    private final String RAW_PASSWORD = "password123";

    @BeforeEach
    public void setUp() {
        // ... (Clean up code omitted for brevity) ...
        discountRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Setup Roles & Users
        Role customerRole = roleRepository.findByRoles(RoleConstants.Role.CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().roles(RoleConstants.Role.CUSTOMER).build()));
        
        Role managerRole = roleRepository.findByRoles(RoleConstants.Role.MANAGER)
                .orElseGet(() -> roleRepository.save(Role.builder().roles(RoleConstants.Role.MANAGER).build()));

        String encodedPassword = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);
        userRepository.save(User.builder()
                .username(CUSTOMER_USERNAME)
                .password(encodedPassword)
                .role(customerRole)
                .isActive(true)
                .email("customer@example.com") // Added email to satisfy constraint if any
                .createdAt(Instant.now())      // Fix: Set createdAt
                .build());
        userRepository.save(User.builder()
                .username(MANAGER_USERNAME)
                .password(encodedPassword)
                .role(managerRole)
                .isActive(true)
                .email("manager@example.com") // Added email to satisfy constraint if any
                .createdAt(Instant.now())     // Fix: Set createdAt
                .build());
    }

    // ========================================================================
    // NHÓM TEST 1: CUSTOMER APPLY DISCOUNT (TC_DM_001 -> TC_DM_008)
    // ========================================================================

    @Test
    public void TC_DM_001_applyValidDiscountCode_shouldSuccess() throws Exception {
        // Given: Valid Discount (Sửa LocalDate -> Instant)
        Discount discount = Discount.builder()
                .code("TEST2")
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .percentage(20.0)
                .startDate(Instant.now().minus(1, ChronoUnit.DAYS)) // FIX: Instant
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))    // FIX: Instant
                .isActive(true)
                .usageLimit(100)
                .usedCount(0)
                .minimumOrderAmount(0.0)
                .build();
        discountRepository.save(discount);

        String token = generateToken(CUSTOMER_USERNAME, "ROLE_CUSTOMER");
        String jsonRequest = objectMapper.writeValueAsString(new ApplyDiscountPayload("TEST2"));

        // When & Then
        mockMvc.perform(post("/cart/apply-discount")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.discountAmount").exists());
    }

    @Test
    public void TC_DM_002_applyExpiredDiscountCode_shouldReturnError() throws Exception {
        // Given: Expired discount (Sửa LocalDate -> Instant)
        Discount discount = Discount.builder()
                .code("GIAMGIA1")
                .percentage(10.0)
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .startDate(Instant.now().minus(10, ChronoUnit.DAYS))
                .endDate(Instant.now().minus(1, ChronoUnit.DAYS)) // Expired
                .isActive(true)
                .build();
        discountRepository.save(discount);

        String token = generateToken(CUSTOMER_USERNAME, "ROLE_CUSTOMER");
        String jsonRequest = objectMapper.writeValueAsString(new ApplyDiscountPayload("GIAMGIA1"));

        // When & Then
        mockMvc.perform(post("/cart/apply-discount")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest()); 
    }

    @Test
    public void TC_DM_003_applyUsedDiscountCode_shouldReturnError() throws Exception {
        // Given: Used Discount
        Discount discount = Discount.builder()
                .code("TEST5")
                .percentage(50.0)
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .startDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .isActive(true)
                .usageLimit(10)
                .usedCount(10) // Full
                .build();
        discountRepository.save(discount);

        String token = generateToken(CUSTOMER_USERNAME, "ROLE_CUSTOMER");
        String jsonRequest = objectMapper.writeValueAsString(new ApplyDiscountPayload("TEST5"));

        // When & Then
        mockMvc.perform(post("/cart/apply-discount")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void TC_DM_004_discountMinimumPurchase_shouldReturnError() throws Exception {
        // Given: High Minimum Order Amount
        Discount discount = Discount.builder()
                .code("TEST4")
                .minimumOrderAmount(1000000.0)
                .percentage(10.0)
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .startDate(Instant.now())
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .isActive(true)
                .build();
        discountRepository.save(discount);

        String token = generateToken(CUSTOMER_USERNAME, "ROLE_CUSTOMER");
        String jsonRequest = objectMapper.writeValueAsString(new ApplyDiscountPayload("TEST4"));

        // When & Then
        mockMvc.perform(post("/cart/apply-discount")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void TC_DM_006_removeAppliedDiscount_shouldSuccess() throws Exception {
        String token = generateToken(CUSTOMER_USERNAME, "ROLE_CUSTOMER");
        String jsonRequest = objectMapper.writeValueAsString(new ApplyDiscountPayload(null));

        mockMvc.perform(post("/cart/apply-discount")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.discountAmount").value(0.0));
    }

    @Test
    public void TC_DM_007_multipleDiscountAttempts_shouldOverride() throws Exception {
        Discount discount = Discount.builder()
                .code("SAVE20")
                .percentage(20.0)
                .isActive(true)
                .startDate(Instant.now())
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();
        discountRepository.save(discount);

        String token = generateToken(CUSTOMER_USERNAME, "ROLE_CUSTOMER");
        String jsonRequest = objectMapper.writeValueAsString(new ApplyDiscountPayload("SAVE20"));

        mockMvc.perform(post("/cart/apply-discount")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void TC_DM_008_discountCaseSensitivity_shouldSuccess() throws Exception {
        Discount discount = Discount.builder()
                .code("TEST3")
                .percentage(10.0)
                .isActive(true)
                .startDate(Instant.now())
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();
        discountRepository.save(discount);

        String token = generateToken(CUSTOMER_USERNAME, "ROLE_CUSTOMER");
        String jsonRequest = objectMapper.writeValueAsString(new ApplyDiscountPayload("test3"));

        mockMvc.perform(post("/cart/apply-discount")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    // ========================================================================
    // NHÓM TEST 2: MANAGER MANAGE DISCOUNT (CRUD)
    // ========================================================================

    @Test
    public void createDiscount_validRequest_shouldSuccess() throws Exception {
        Instant now = Instant.now();
        DiscountCreateRequest request = DiscountCreateRequest.builder()
                .code("NEWCODE")
                .percentage(15.0)
                .startDate(now)
                .endDate(now.plus(10, ChronoUnit.DAYS))
                .description("New Discount")
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .usageLimit(50)
                .minimumOrderAmount(10.0) // Changed from 0.0 to 10.0 to see if 0.0 is the issue
                .build();

        String token = generateToken(MANAGER_USERNAME, "ROLE_MANAGER");
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/discounts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.code").value("NEWCODE"))
                .andExpect(jsonPath("$.result.minimumOrderAmount").value(10.0));
    }

    @Test
    public void getAllDiscounts_shouldReturnList() throws Exception {
        Discount discount = Discount.builder()
                .code("LISTTEST")
                .percentage(10.0)
                .startDate(Instant.now())
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .isActive(true)
                .build();
        discountRepository.save(discount);

        String token = generateToken(MANAGER_USERNAME, "ROLE_MANAGER");

        mockMvc.perform(get("/discounts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result[?(@.code == 'LISTTEST')]").exists());
    }

    @Test
    public void getDiscountById_validId_shouldReturnDiscount() throws Exception {
        Discount discount = Discount.builder()
                .code("IDTEST")
                .percentage(10.0)
                .startDate(Instant.now())
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .isActive(true)
                .build();
        discount = discountRepository.save(discount);

        String token = generateToken(MANAGER_USERNAME, "ROLE_MANAGER");

        mockMvc.perform(get("/discounts/" + discount.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.code").value("IDTEST"));
    }

    @Test
    public void updateDiscount_validRequest_shouldSuccess() throws Exception {
        Discount discount = Discount.builder()
                .code("UPDATETEST")
                .percentage(10.0)
                .startDate(Instant.now())
                .endDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .discountType(DiscountConstants.DiscountType.PERCENTAGE)
                .isActive(true)
                .build();
        discount = discountRepository.save(discount);

        DiscountUpdateRequest request = new DiscountUpdateRequest();
        request.setCode("UPDATED");
        request.setPercentage(20.0);
        request.setStartDate(Instant.now());
        request.setEndDate(Instant.now().plus(10, ChronoUnit.DAYS));
        request.setDiscountType(DiscountConstants.DiscountType.PERCENTAGE);
        request.setDescription("Updated Description");

        String token = generateToken(MANAGER_USERNAME, "ROLE_MANAGER");
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/discounts/" + discount.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.code").value("UPDATED"))
                .andExpect(jsonPath("$.result.percentage").value(20.0));
    }

    // --- Helper Methods ---

    private String generateToken(String username, String role) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("superteam.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(3600, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", role)
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        SignedJWT signedJWT = new SignedJWT(header, claims);
        signedJWT.sign(new MACSigner(SIGNER_KEY.getBytes()));

        return signedJWT.serialize();
    }
    
    static class ApplyDiscountPayload {
        public String code;
        public ApplyDiscountPayload(String code) { this.code = code; }
    }
}