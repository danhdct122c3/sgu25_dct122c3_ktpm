package fpl.sd.backend.controller;


import com.nimbusds.jose.JOSEException;
import fpl.sd.backend.dto.APIResponse;
import fpl.sd.backend.dto.request.*;
import fpl.sd.backend.dto.response.AuthenticationResponse;
import fpl.sd.backend.dto.response.IntrospectResponse;
import fpl.sd.backend.service.AuthenticationService;

import fpl.sd.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173"})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    UserService userService;



    @PostMapping("/token")
    APIResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        var rs = authenticationService.authenticate(authenticationRequest);
        return APIResponse.<AuthenticationResponse>builder()
                .result(rs)
                .flag(true)
                .message("Login successful")
                .build();
    }

    @PostMapping("/introspect")
    APIResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var rs = authenticationService.introspect(request);
        return APIResponse.<IntrospectResponse>builder()
                .result(rs)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public APIResponse<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(request);
        return APIResponse.<Void>builder()
                .flag(true)
                .message("Password changed successfully")
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public APIResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException {
        authenticationService.logout(request);
        return APIResponse.<Void>builder()
                .code(200)
                .message("Successfully logged out.")
                .result(null)
                .build();
    }

    

    @PostMapping("/verify-otp")
    public APIResponse<Void> verifyOtp(@RequestBody OTPVerificationRequest request) {
        boolean isVerified = userService.verifyOtp(request.getEmail(), request.getOtpCode());
        if (isVerified) {
            return APIResponse.<Void>builder()
                    .flag(true)
                    .message("Successfully verified OTP.")
                    .build();
        } else {
            return APIResponse.<Void>builder()
                    .flag(false)
                    .message("Invalid or expired OTP.")
                    .code(400)
                    .build();
        }
    }

    @PostMapping("/reset-password")
    public APIResponse<Void> resetPassword(@RequestBody OTPVerificationRequest request) {
        boolean isReset = userService.resetPassword(request.getEmail(), request.getNewPassword(), request.getConfirmPassword());
        if (isReset) {
            return APIResponse.<Void>builder()
                    .flag(true)
                    .message("Reset password successful.")
                    .build();
        } else {
            return APIResponse.<Void>builder()
                    .flag(false)
                    .message("Reset password failed.")
                    .code(400)
                    .build();
        }
    }
}
