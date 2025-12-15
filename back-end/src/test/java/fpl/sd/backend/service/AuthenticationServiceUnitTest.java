package fpl.sd.backend.service;

import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;

import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.dto.request.AuthenticationRequest;
import fpl.sd.backend.dto.request.LogoutRequest;
import fpl.sd.backend.dto.request.UserCreateRequest;
import fpl.sd.backend.dto.response.UserResponse;
import fpl.sd.backend.entity.InvalidatedToken;
import fpl.sd.backend.entity.Role;
import fpl.sd.backend.entity.User;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.mapper.UserMapper;
import fpl.sd.backend.repository.InvalidatedTokenRepository;
import fpl.sd.backend.repository.RoleRepository;
import fpl.sd.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.text.ParseException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthenticationService (login/logout) using Mockito to mock repositories.
 * These tests are unit tests (no Spring context started).
 */
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private final String TEST_USERNAME = "customer";
    private final String RAW_PASSWORD = "password123";
    private final String SIGNER_KEY = "0123456789012345678901234567890123456789012345678901234567890123"; // 64 chars


    @BeforeEach
    void setUp() {
        // Set signerKey and durations on the service instance
        ReflectionTestUtils.setField(authenticationService, "signerKey", SIGNER_KEY);
        ReflectionTestUtils.setField(authenticationService, "validDuration", 3600L);
        ReflectionTestUtils.setField(authenticationService, "refreshableDuration", 7200L);
    }

    @Test
    void TC_AUTH_001_loginSuccessWithValidCredentials_shouldReturnToken() throws Exception {
        // Arrange: create a User with password encoded using same BCrypt strength used in service (10)
        String encoded = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);
        User user = User.builder()
                .username(TEST_USERNAME)
                .password(encoded)
                .isActive(true)
                .build();
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        // Act
        var resp = authenticationService.authenticate(new AuthenticationRequest(TEST_USERNAME, RAW_PASSWORD));
        // Assert
        assertThat(resp).isNotNull();
        assertThat(resp.isAuthenticated()).isTrue();
        assertThat(resp.getToken()).isNotBlank();

        // Parse token and validate subject
        SignedJWT signedJWT = SignedJWT.parse(resp.getToken());
        assertThat(signedJWT.getJWTClaimsSet().getSubject()).isEqualTo(TEST_USERNAME);

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    void TC_AUTH_002_loginFailedWithInvalidUsername_shouldThrowUnauthenticated() throws Exception {
        // Arrange
        // User DOES NOT exist in database
        String invalidUsername = "invalid";
        when(userRepository.findByUsername(invalidUsername))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(Exception.class, () ->
                authenticationService.authenticate(
                        new AuthenticationRequest(invalidUsername, RAW_PASSWORD)
                )
        );

        // Verify repository was called exactly once
        verify(userRepository, times(1)).findByUsername(invalidUsername);
    }

    @Test
    void TC_AUTH_004_signupSuccessWithValidData_shouldCreateUser() {
        // Arrange
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newcustomer")
                .email("newcustomer@example.com")
                .password("password123")
                .build();

        // Username & Email chưa tồn tại
        when(userRepository.existsByUsername("newcustomer")).thenReturn(false);
        when(userRepository.existsByEmail("newcustomer@example.com")).thenReturn(false);

        // Role CUSTOMER tồn tại
        Role customerRole = Role.builder()
                .roles(RoleConstants.Role.CUSTOMER)
                .build();
        when(roleRepository.findByRoles(RoleConstants.Role.CUSTOMER))
                .thenReturn(Optional.of(customerRole));

        // Mapper → User (không được null)
        User mappedUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .build();
        when(userMapper.toUser(request)).thenReturn(mappedUser);

        // Saved user → có UUID
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setId(UUID.randomUUID().toString());
                    return u;
                });

        // Mapper → UserResponse (không được null)
        UserResponse mappedResponse = UserResponse.builder()
                .id("uuid-123456")
                .username(request.getUsername())
                .email(request.getEmail())
                .build();
        when(userMapper.toUserResponse(any(User.class)))
                .thenReturn(mappedResponse);

        // Act
        UserResponse response = userService.createUser(request);

        // Assert – response không được null
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("newcustomer");
        assertThat(response.getEmail()).isEqualTo("newcustomer@example.com");
        assertThat(response.getId()).isEqualTo("uuid-123456");

        verify(userRepository).existsByUsername("newcustomer");
        verify(userRepository).existsByEmail("newcustomer@example.com");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserResponse(any(User.class));
    }

    @Test
    void TC_AUTH_005_signupFailedWithInvalidEmail_shouldThrowException() {

        // Arrange
        UserCreateRequest request = UserCreateRequest.builder()
                .username("customer123")
                .email("invalidemail")  //  email không hợp lệ
                .password("password123")
                .build();

        // Service logic: chỉ cần email tồn tại → throw
        when(userRepository.existsByUsername("customer123")).thenReturn(false);
        when(userRepository.existsByEmail("invalidemail")).thenReturn(true);  // giả lập email không hợp lệ

        // Act + Assert
        AppException exception = assertThrows(AppException.class, () ->
                userService.createUser(request)
        );

        // Assert error code
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);

        // verify được gọi đúng
        verify(userRepository).existsByEmail("invalidemail");
    }

    @Test
    void TC_AUTH_006_signupFailedWithExistingEmail_shouldThrowException() {

        // Arrange
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .email("customer@example.com")  //  email đã tồn tại
                .password("password123")
                .build();

        // Username chưa tồn tại
        when(userRepository.existsByUsername("newuser"))
                .thenReturn(false);

        // Email đã tồn tại
        when(userRepository.existsByEmail("customer@example.com"))
                .thenReturn(true);

        // Act + Assert
        AppException exception = assertThrows(AppException.class, () ->
                userService.createUser(request)
        );

        // Assert error code
        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);

        // verify repository calls
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("customer@example.com");
    }

    @Test
    void TC_AUTH_007_logoutSuccess_shouldInvalidateToken() throws Exception {

        // Set fixed signerKey for test
        ReflectionTestUtils.setField(authenticationService, "signerKey", SIGNER_KEY);
        ReflectionTestUtils.setField(authenticationService, "validDuration", 3600L);
        ReflectionTestUtils.setField(authenticationService, "refreshableDuration", 7200L);

        // -------------------------------
        // 1. Tạo JWT thật bằng Nimbus
        // -------------------------------
        String jti = "logout-jti-123";

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(TEST_USERNAME)
                .jwtID(jti)
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plusSeconds(3600))) // hết hạn sau 1 giờ
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claims
        );

        signedJWT.sign(new MACSigner(SIGNER_KEY));
        String realToken = signedJWT.serialize();

        // invalidatedTokenRepository phải KHÔNG chứa jti này
        when(invalidatedTokenRepository.existsById(jti)).thenReturn(false);

        // -------------------------------
        // 2. Gọi logout()
        // -------------------------------
        LogoutRequest req = new LogoutRequest(realToken);

        authenticationService.logout(req);

        // -------------------------------
        // 3. Kiểm tra jti đã được lưu vào DB
        // -------------------------------
        verify(invalidatedTokenRepository, times(1))
                .save(argThat(entity -> entity.getId().equals(jti)));
    }

    @Test
    void TC_AUTH_012_staffLoginSuccessWithValidCredentials_shouldReturnToken() throws Exception {
        // Arrange
        String encoded = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);

        Role staffRole = Role.builder()
                .roles(RoleConstants.Role.STAFF)
                .build();

        User staffUser = User.builder()
                .username("staff")
                .password(encoded)
                .role(staffRole)
                .isActive(true)
                .build();

        // Mock DB lookup
        when(userRepository.findByUsername("staff")).thenReturn(Optional.of(staffUser));

        // Act
        var resp = authenticationService.authenticate(
                new AuthenticationRequest("staff", RAW_PASSWORD)
        );

        // Assert
        assertThat(resp).isNotNull();
        assertThat(resp.isAuthenticated()).isTrue();
        assertThat(resp.getToken()).isNotBlank();

        // Parse and validate JWT
        SignedJWT jwt = SignedJWT.parse(resp.getToken());

        assertThat(jwt.getJWTClaimsSet().getSubject()).isEqualTo("staff");
        assertThat(jwt.getJWTClaimsSet().getClaim("scope")).isEqualTo("ROLE_STAFF");

        // Verify repository called exactly once
        verify(userRepository, times(1)).findByUsername("staff");
    }



    @Test
    void TC_AUTH_013_staffLoginSuccessWithValidCredentials_shouldReturnToken() throws Exception {
        // Arrange
        String encoded = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);

        Role staffRole = Role.builder()
                .roles(RoleConstants.Role.STAFF)
                .build();

        User staffUser = User.builder()
                .username("staff")
                .password(encoded)
                .role(staffRole)
                .isActive(true)
                .build();

        // Mock DB lookup
        when(userRepository.findByUsername("staff")).thenReturn(Optional.of(staffUser));

        // Act
        var resp = authenticationService.authenticate(
                new AuthenticationRequest("staff", RAW_PASSWORD)
        );

        // Assert
        assertThat(resp).isNotNull();
        assertThat(resp.isAuthenticated()).isTrue();
        assertThat(resp.getToken()).isNotBlank();

        // Parse and validate JWT
        SignedJWT jwt = SignedJWT.parse(resp.getToken());

        assertThat(jwt.getJWTClaimsSet().getSubject()).isEqualTo("staff");
        assertThat(jwt.getJWTClaimsSet().getClaim("scope")).isEqualTo("ROLE_STAFF");

        // Verify repository called exactly once
        verify(userRepository, times(1)).findByUsername("staff");
    }


    @Test
    void TC_AUTH_013_staffLoginFailedWithInvalidUsername_shouldThrowUnauthenticated() {
        // Arrange: Staff username không tồn tại trong DB
        when(userRepository.findByUsername("invalid_staff"))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(AppException.class, () ->
                authenticationService.authenticate(
                        new AuthenticationRequest("invalid_staff", "wrongpass")
                )
        );

        // DB lookup được gọi 1 lần
        verify(userRepository, times(1)).findByUsername("invalid_staff");
    }



        @Test
        void TC_AUTH_014_staffLogoutSuccess_shouldInvalidateToken() throws Exception {

            // Arrange: Staff user
            String encoded = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);

            Role staffRole = Role.builder()
                    .roles(RoleConstants.Role.STAFF)
                    .build();

            User staffUser = User.builder()
                    .username("staff")
                    .password(encoded)
                    .role(staffRole)
                    .isActive(true)
                    .build();

            when(userRepository.findByUsername("staff"))
                    .thenReturn(Optional.of(staffUser));

            // Step 1: Login to generate token
            var resp = authenticationService.authenticate(
                    new AuthenticationRequest("staff", RAW_PASSWORD)
            );

            String token = resp.getToken();
            assertThat(token).isNotBlank();

            // Parse JTI
            SignedJWT signedJWT = SignedJWT.parse(token);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            assertThat(jti).isNotBlank();

            // Token chưa invalidate
            when(invalidatedTokenRepository.existsById(jti)).thenReturn(false);

            // Create request
            LogoutRequest logoutRequest = new LogoutRequest();
            logoutRequest.setToken(token);

            // Act
            authenticationService.logout(logoutRequest);

            // Assert: jti must be saved to DB
            verify(invalidatedTokenRepository, times(1))
                    .save(argThat(t -> t.getId().equals(jti)));
        }

    @Test
    void TC_AUTH_018_managerLoginSuccessWithValidCredentials_shouldReturnToken() throws Exception {

        // Arrange: Encode password
        String encoded = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);

        // Create Manager role
        Role managerRole = Role.builder()
                .roles(RoleConstants.Role.MANAGER)
                .build();

        // Create Manager user
        User managerUser = User.builder()
                .username("manager")
                .password(encoded)
                .role(managerRole)
                .isActive(true)
                .build();

        // Mock repository
        when(userRepository.findByUsername("manager"))
                .thenReturn(Optional.of(managerUser));

        // Act
        var resp = authenticationService.authenticate(
                new AuthenticationRequest("manager", RAW_PASSWORD)
        );

        // Assert
        assertThat(resp).isNotNull();
        assertThat(resp.isAuthenticated()).isTrue();
        assertThat(resp.getToken()).isNotBlank();

        // Parse and validate JWT
        SignedJWT jwt = SignedJWT.parse(resp.getToken());

        assertThat(jwt.getJWTClaimsSet().getSubject())
                .isEqualTo("manager");

        assertThat(jwt.getJWTClaimsSet().getClaim("scope"))
                .isEqualTo("ROLE_MANAGER");

        verify(userRepository, times(1)).findByUsername("manager");
    }

    @Test
    void TC_AUTH_019_managerLoginFailed_invalidUsername_shouldThrowUnauthenticated() {

        // Arrange: username không tồn tại
        when(userRepository.findByUsername("invalid_manager"))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(AppException.class, () ->
                authenticationService.authenticate(
                        new AuthenticationRequest("invalid_manager", "wrongpass")
                )
        );

        verify(userRepository, times(1)).findByUsername("invalid_manager");
    }

    @Test
    void TC_AUTH_020_managerLogoutSuccess_shouldInvalidateToken() throws Exception {

        // Arrange — create encoded password
        String encoded = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);

        // Manager role
        Role managerRole = Role.builder()
                .roles(RoleConstants.Role.MANAGER)
                .build();

        // Manager user
        User managerUser = User.builder()
                .username("manager")
                .password(encoded)
                .role(managerRole)
                .isActive(true)
                .build();

        // Mock: username exists
        when(userRepository.findByUsername("manager"))
                .thenReturn(Optional.of(managerUser));

        // Step 1 — Manager login → generate JWT
        var resp = authenticationService.authenticate(
                new AuthenticationRequest("manager", RAW_PASSWORD)
        );

        String token = resp.getToken();
        assertThat(token).isNotBlank();

        // Parse token to get jti
        SignedJWT signedJWT = SignedJWT.parse(token);
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        assertThat(jti).isNotBlank();

        // Token chưa bị logout trước đó
        when(invalidatedTokenRepository.existsById(jti)).thenReturn(false);

        // Build LogoutRequest
        LogoutRequest request = new LogoutRequest();
        request.setToken(token);

        // Act — logout manager
        authenticationService.logout(request);

        // Assert — jti must be saved in DB
        verify(invalidatedTokenRepository, times(1))
                .save(argThat(obj -> obj.getId().equals(jti)));
    }

    @Test
    void TC_AUTH_024_adminLoginSuccessWithValidCredentials_shouldReturnToken() throws Exception {

        // Arrange — encode password
        String encoded = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);

        // Create Admin role
        Role adminRole = Role.builder()
                .roles(RoleConstants.Role.ADMIN)
                .build();

        // Create Admin user
        User adminUser = User.builder()
                .username("admin")
                .password(encoded)
                .role(adminRole)
                .isActive(true)
                .build();

        // Mock DB lookup
        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(adminUser));

        // Act — authentication
        var resp = authenticationService.authenticate(
                new AuthenticationRequest("admin", RAW_PASSWORD)
        );

        // Assert — authentication ok
        assertThat(resp).isNotNull();
        assertThat(resp.isAuthenticated()).isTrue();
        assertThat(resp.getToken()).isNotBlank();

        // Parse JWT
        SignedJWT jwt = SignedJWT.parse(resp.getToken());

        assertThat(jwt.getJWTClaimsSet().getSubject()).isEqualTo("admin");
        assertThat(jwt.getJWTClaimsSet().getClaim("scope")).isEqualTo("ROLE_ADMIN");

        verify(userRepository, times(1)).findByUsername("admin");
    }

    @Test
    void TC_AUTH_026_managerLogoutSuccess_shouldInvalidateToken() throws Exception {

        // Arrange — create encoded password
        String encoded = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);

        // Manager role
        Role managerRole = Role.builder()
                .roles(RoleConstants.Role.MANAGER)
                .build();

        // Manager user
        User managerUser = User.builder()
                .username("manager")
                .password(encoded)
                .role(managerRole)
                .isActive(true)
                .build();

        // Mock: username exists
        when(userRepository.findByUsername("manager"))
                .thenReturn(Optional.of(managerUser));

        // Step 1 — Manager login → generate JWT
        var resp = authenticationService.authenticate(
                new AuthenticationRequest("manager", RAW_PASSWORD)
        );

        String token = resp.getToken();
        assertThat(token).isNotBlank();

        // Parse token to get jti
        SignedJWT signedJWT = SignedJWT.parse(token);
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        assertThat(jti).isNotBlank();

        // Token chưa bị logout trước đó
        when(invalidatedTokenRepository.existsById(jti)).thenReturn(false);

        // Build LogoutRequest
        LogoutRequest request = new LogoutRequest();
        request.setToken(token);

        // Act — logout manager
        authenticationService.logout(request);

        // Assert — jti must be saved in DB
        verify(invalidatedTokenRepository, times(1))
                .save(argThat(obj -> obj.getId().equals(jti)));
    }



    @Test
    void login_withValidCredentials_shouldReturnToken() throws Exception {
        // Arrange: create a User with password encoded using same BCrypt strength used in service (10)
        String encoded = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);
        User user = User.builder()
                .username(TEST_USERNAME)
                .password(encoded)
                .email("ut@example.com")
                .isActive(true)
                .build();

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

        // Act
        var resp = authenticationService.authenticate(new AuthenticationRequest(TEST_USERNAME, RAW_PASSWORD));

        // Assert
        assertThat(resp).isNotNull();
        assertThat(resp.isAuthenticated()).isTrue();
        assertThat(resp.getToken()).isNotBlank();

        // Parse token and validate subject
        SignedJWT signedJWT = SignedJWT.parse(resp.getToken());
        assertThat(signedJWT.getJWTClaimsSet().getSubject()).isEqualTo(TEST_USERNAME);

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    void login_withInvalidPassword_shouldThrowUnauthenticated() {
        String encoded = new BCryptPasswordEncoder(10).encode("otherpassword");
        User user = User.builder().username(TEST_USERNAME).password(encoded).email("ut@example.com").isActive(true).build();

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authenticationService.authenticate(new AuthenticationRequest(TEST_USERNAME, RAW_PASSWORD)))
                .isInstanceOf(AppException.class)
                .extracting(throwable -> ((AppException) throwable).getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHENTICATED);

        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    void login_userNotFound_shouldThrow() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticate(new AuthenticationRequest("unknown", RAW_PASSWORD)))
                .isInstanceOf(AppException.class)
                .extracting(throwable -> ((AppException) throwable).getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    void logout_withValidToken_shouldSaveInvalidatedToken() throws Exception {
        // First, prepare a valid token by authenticating a user
        String encoded = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);
        User user = User.builder().username(TEST_USERNAME).password(encoded).email("ut@example.com").isActive(true).build();
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

        var authResp = authenticationService.authenticate(new AuthenticationRequest(TEST_USERNAME, RAW_PASSWORD));
        String token = authResp.getToken();

        // Act: call logout
        authenticationService.logout(new LogoutRequest(token));

        // Assert: invalidatedTokenRepository.save called with InvalidatedToken containing jti
        ArgumentCaptor<InvalidatedToken> captor = ArgumentCaptor.forClass(InvalidatedToken.class);
        verify(invalidatedTokenRepository, times(1)).save(captor.capture());

        InvalidatedToken saved = captor.getValue();
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotBlank();
        assertThat(saved.getExpiryTime()).isNotNull();
        // expiryTime should be in future (after now)
        assertThat(saved.getExpiryTime().after(new Date())).isTrue();
    }

    @Test
    void logout_withInvalidToken_shouldThrowParseException_and_notSave() throws Exception {
        String badToken = "not.a.valid.token";

        // Act & Assert: because logout throws ParseException for malformed token, assert it is thrown
        assertThatThrownBy(() -> authenticationService.logout(new LogoutRequest(badToken)))
                .isInstanceOf(ParseException.class);

        // Ensure no invalidated token saved
        verify(invalidatedTokenRepository, never()).save(any());
    }
}
