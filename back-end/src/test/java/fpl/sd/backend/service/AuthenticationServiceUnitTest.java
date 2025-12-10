package fpl.sd.backend.service;

import com.nimbusds.jwt.SignedJWT;
import fpl.sd.backend.dto.request.AuthenticationRequest;
import fpl.sd.backend.dto.request.LogoutRequest;
import fpl.sd.backend.entity.InvalidatedToken;
import fpl.sd.backend.entity.User;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.repository.InvalidatedTokenRepository;
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

import java.util.Date;
import java.util.Optional;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    private final String TEST_USERNAME = "unit_test_user";
    private final String RAW_PASSWORD = "123456";
    private final String SIGNER_KEY = "0123456789012345678901234567890123456789012345678901234567890123"; // 64 chars

    @BeforeEach
    void setUp() {
        // Set signerKey and durations on the service instance
        ReflectionTestUtils.setField(authenticationService, "signerKey", SIGNER_KEY);
        ReflectionTestUtils.setField(authenticationService, "validDuration", 3600L);
        ReflectionTestUtils.setField(authenticationService, "refreshableDuration", 7200L);
    }

    @DisplayName("login_withValidCredentials_shouldReturnToken | Đăng nhập với thông tin hợp lệ | username='unit_test_user', password='123456' | Trả về token JWT hợp lệ")
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

    @DisplayName("login_withInvalidPassword_shouldThrowUnauthenticated | Đăng nhập với mật khẩu sai | username='unit_test_user', password='wrong' | Ném AppException với ErrorCode.UNAUTHENTICATED")
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

    @DisplayName("login_userNotFound_shouldThrow | Đăng nhập với user không tồn tại | username='unknown' | Ném AppException với ErrorCode.USER_NOT_FOUND")
    @Test
    void login_userNotFound_shouldThrow() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticate(new AuthenticationRequest("unknown", RAW_PASSWORD)))
                .isInstanceOf(AppException.class)
                .extracting(throwable -> ((AppException) throwable).getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @DisplayName("logout_withValidToken_shouldSaveInvalidatedToken | Đăng xuất với token hợp lệ | token=validJWT | Lưu InvalidatedToken vào database")
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

    @DisplayName("logout_withInvalidToken_shouldThrowParseException_and_notSave | Đăng xuất với token không hợp lệ | token='not.a.valid.token' | Ném ParseException và không lưu")
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
