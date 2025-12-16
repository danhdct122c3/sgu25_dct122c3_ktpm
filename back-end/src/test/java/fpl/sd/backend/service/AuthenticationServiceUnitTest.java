package fpl.sd.backend.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceUnitTest {

    // ===== MOCKS =====
    @Mock private UserRepository userRepository;
    @Mock private InvalidatedTokenRepository invalidatedTokenRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserMapper userMapper;

    // ===== SERVICES =====
    @InjectMocks
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserService userService;

    // ===== CONSTANT =====
    private final String RAW_PASSWORD = "password123";
    private final String SIGNER_KEY =
            "0123456789012345678901234567890123456789012345678901234567890123";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "signerKey", SIGNER_KEY);
        ReflectionTestUtils.setField(authenticationService, "validDuration", 3600L);
        ReflectionTestUtils.setField(authenticationService, "refreshableDuration", 7200L);
    }

    // =====================================================================
    // LOGIN
    // =====================================================================

    @DisplayName("TC_AUTH_001 | Login thành công | Username hợp lệ + password đúng | Trả JWT")
    @Test
    void loginSuccess_shouldReturnToken() throws Exception {
        // Arrange
        String encoded = new BCryptPasswordEncoder(10).encode(RAW_PASSWORD);
        User user = User.builder()
                .username("customer")
                .password(encoded)
                .isActive(true)
                .build();

        when(userRepository.findByUsername("customer"))
                .thenReturn(Optional.of(user));

        // Act
        var response = authenticationService.authenticate(
                new AuthenticationRequest("customer", RAW_PASSWORD)
        );

        // Assert
        assertThat(response.isAuthenticated()).isTrue();
        assertThat(response.getToken()).isNotBlank();

        SignedJWT jwt = SignedJWT.parse(response.getToken());
        assertThat(jwt.getJWTClaimsSet().getSubject()).isEqualTo("customer");

        verify(userRepository).findByUsername("customer");
    }

    @DisplayName("TC_AUTH_002 | Login thất bại | Username không tồn tại | Throw USER_NOT_FOUND")
    @Test
    void login_userNotFound_shouldThrow() {
        // Arrange
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        // Act + Assert
        AppException ex = assertThrows(AppException.class, () ->
                authenticationService.authenticate(
                        new AuthenticationRequest("unknown", RAW_PASSWORD)
                )
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @DisplayName("TC_AUTH_003 | Login thất bại | Password sai | Throw UNAUTHENTICATED")
    @Test
    void login_wrongPassword_shouldThrow() {
        // Arrange
        String encoded = new BCryptPasswordEncoder(10).encode("other");
        User user = User.builder()
                .username("customer")
                .password(encoded)
                .isActive(true)
                .build();

        when(userRepository.findByUsername("customer"))
                .thenReturn(Optional.of(user));

        // Act + Assert
        AppException ex = assertThrows(AppException.class, () ->
                authenticationService.authenticate(
                        new AuthenticationRequest("customer", RAW_PASSWORD)
                )
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.UNAUTHENTICATED);
    }

    // =====================================================================
    // SIGN UP
    // =====================================================================

    @DisplayName("TC_AUTH_004 | Signup thành công | Username & Email chưa tồn tại")
    @Test
    void signupSuccess_shouldCreateUser() {
        // Arrange
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .email("newuser@mail.com")
                .password("123456")
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@mail.com")).thenReturn(false);

        Role role = Role.builder()
                .roles(RoleConstants.Role.CUSTOMER)
                .build();

        when(roleRepository.findByRoles(RoleConstants.Role.CUSTOMER))
                .thenReturn(Optional.of(role));

        User mappedUser = User.builder()
                .username("newuser")
                .email("newuser@mail.com")
                .build();

        when(userMapper.toUser(request)).thenReturn(mappedUser);

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> {
                    User u = i.getArgument(0);
                    u.setId(UUID.randomUUID().toString());
                    return u;
                });

        UserResponse response = UserResponse.builder()
                .id("id-123")
                .username("newuser")
                .email("newuser@mail.com")
                .build();

        when(userMapper.toUserResponse(any(User.class)))
                .thenReturn(response);

        // Act
        UserResponse result = userService.createUser(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getEmail()).isEqualTo("newuser@mail.com");
    }

    @DisplayName("TC_AUTH_005 | Signup thất bại | Email đã tồn tại | Throw EMAIL_ALREADY_EXISTS")
    @Test
    void signup_existingEmail_shouldThrow() {
        // Arrange
        UserCreateRequest request = UserCreateRequest.builder()
                .username("user")
                .email("exist@mail.com")
                .password("123")
                .build();

        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("exist@mail.com")).thenReturn(true);

        // Act + Assert
        AppException ex = assertThrows(AppException.class, () ->
                userService.createUser(request)
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    // =====================================================================
    // LOGOUT
    // =====================================================================

    @DisplayName("TC_AUTH_007 | Logout thành công | Token hợp lệ | Lưu InvalidatedToken")
    @Test
    void logoutSuccess_shouldSaveInvalidatedToken() throws Exception {
        // Arrange – create real JWT
        String jti = "jti-logout-001";

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("customer")
                .jwtID(jti)
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
                .build();

        SignedJWT jwt = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claims
        );

        jwt.sign(new MACSigner(SIGNER_KEY));
        String token = jwt.serialize();

        when(invalidatedTokenRepository.existsById(jti)).thenReturn(false);

        // Act
        authenticationService.logout(new LogoutRequest(token));

        // Assert
        verify(invalidatedTokenRepository).save(
                argThat(t -> t.getId().equals(jti))
        );
    }

    @DisplayName("TC_AUTH_008 | Logout thất bại | Token không hợp lệ | Throw ParseException")
    @Test
    void logout_invalidToken_shouldThrow() {
        // Arrange
        String invalidToken = "invalid.token.value";

        // Act + Assert
        assertThrows(ParseException.class, () ->
                authenticationService.logout(new LogoutRequest(invalidToken))
        );

        verify(invalidatedTokenRepository, never()).save(any());
    }
}
