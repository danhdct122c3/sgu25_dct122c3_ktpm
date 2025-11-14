package fpl.sd.backend.controller;


import fpl.sd.backend.dto.APIResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;  // ✅ annotation
import io.swagger.v3.oas.annotations.responses.ApiResponses; // ✅ annotation container
import fpl.sd.backend.dto.PageResponse;
import fpl.sd.backend.dto.request.AdminCreateRequest;
import fpl.sd.backend.dto.request.UserCreateRequest;
import fpl.sd.backend.dto.response.UserResponse;
import fpl.sd.backend.dto.request.UserUpdateRequest;
import fpl.sd.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import fpl.sd.backend.service.AuthenticationService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {
    UserService userService;
    AuthenticationService authenticationService;
//    @Operation(
//            summary = "Lấy danh sách sản phẩm",
//            description = "API này trả về toàn bộ danh sách sản phẩm trong hệ thống"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Thành công"),
//            @ApiResponse(responseCode = "404", description = "Không tìm thấy user")
//    })
    @PostMapping("/register")
    public APIResponse<UserResponse> addUser(@RequestBody @Valid UserCreateRequest user) {
        return APIResponse.<UserResponse>builder()
                .flag(true)
                .code(200)
                .message("Successfully added user")
                .result(userService.createUser(user))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public APIResponse<List<UserResponse>> getAllUsers() {
        return APIResponse.<List<UserResponse>>builder()
                .flag(true)
                .code(200)
                .message("Successfully loaded")
                .result(userService.getAllUsers())
                .build();
    }
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.claims['sub']")
    @GetMapping("/{userId}")
    public APIResponse<UserResponse> getUserById(@PathVariable String userId) {
        return APIResponse.<UserResponse>builder()
                .flag(true)
                .code(200)
                .message("Successfully")
                .result(userService.getUserById(userId))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or @userService.isUserOwner(#userId, authentication.principal.claims['sub'])")
    @PutMapping("/{userId}")
    public APIResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest user) {
        UserResponse updateUser = userService.updateUser(userId,user);
        return APIResponse.<UserResponse>builder()
                .flag(true)
                .code(200)
                .message("User updated successfully")
                .result(updateUser)
                .build();
    }

    // Endpoint mới: Update user bằng username (cho admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{username}")
    public APIResponse<UserResponse> updateUserByUsername(@PathVariable String username, @RequestBody @Valid UserUpdateRequest user) {
        UserResponse updateUser = userService.updateUserByUsername(username, user);
        return APIResponse.<UserResponse>builder()
                .flag(true)
                .code(200)
                .message("User updated successfully")
                .result(updateUser)
                .build();
    }

    @GetMapping("/profile")
    public APIResponse<UserResponse> getUserByUsername(
            @RequestParam(value = "username", required = false) String username,
            Authentication authentication) {
        if (username == null || username.isBlank()) {
            username = authentication != null ? authentication.getName() : null;
        }

        return APIResponse.<UserResponse>builder()
                .flag(true)
                .code(200)
                .message("Successfully")
                .result(userService.getUserByUserName(username))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role")
    public APIResponse<List<UserResponse>> getUserByRole(@RequestParam(value = "role", required = false) String roleName) {
        return APIResponse.<List<UserResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(userService.getUserByRole(roleName))  // Cập nhật tham số thành roleName
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/isActive")
    public APIResponse<List<UserResponse>> getUserByIsActive(@RequestParam(value = "isActive", required = false) boolean isActive) {
        return APIResponse.<List<UserResponse>>builder()
                .flag(true)
                .code(200)
                .message("OK")
                .result(userService.getUserByIsActive(isActive))
                .build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list-user")
    public APIResponse<PageResponse<UserResponse>> getUserPaging(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String roleName,  // Thay roleId bằng roleName
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "date") String sortOrder,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        return APIResponse.<PageResponse<UserResponse>>builder()
                .flag(true)
                .message("OK")
                .result(userService.getUserPaging(username, roleName, isActive, page, size, sortOrder))
                .build();
    }

    /**
     * Endpoint để tạo admin user đầu tiên trong hệ thống
     * CHỈ cho phép tạo khi chưa có admin nào
     * Sau khi có admin, endpoint này sẽ trả về lỗi
     */
    @PostMapping("/create-admin")
    public APIResponse<UserResponse> createAdmin(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName) {

        // Kiểm tra xem đã có admin chưa
        if (userService.hasAdminUser()) {
            return APIResponse.<UserResponse>builder()
                    .flag(false)
                    .code(403)
                    .message("Admin user already exists. Cannot create another admin through this endpoint.")
                    .build();
        }

        UserResponse adminUser = userService.createAdminUser(username, email, password, fullName);
        return APIResponse.<UserResponse>builder()
                .flag(true)
                .code(200)
                .message("Admin user created successfully")
                .result(adminUser)
                .build();
    }

    /**
     * Endpoint để tạo admin user đầu tiên trong hệ thống (JSON format)
     * CHỈ cho phép tạo khi chưa có admin nào
     * Sau khi có admin, endpoint này sẽ trả về lỗi
     */
    @PostMapping("/create-admin-json")
    public APIResponse<UserResponse> createAdminJson(@RequestBody @Valid AdminCreateRequest request) {
        // Kiểm tra xem đã có admin chưa
        if (userService.hasAdminUser()) {
            return APIResponse.<UserResponse>builder()
                    .flag(false)
                    .code(403)
                    .message("Admin user already exists. Cannot create another admin through this endpoint.")
                    .build();
        }

        UserResponse adminUser = userService.createAdminUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFullName()
        );
        return APIResponse.<UserResponse>builder()
                .flag(true)
                .code(200)
                .message("Admin user created successfully")
                .result(adminUser)
                .build();
    }

    @GetMapping("/check-admin")
    public APIResponse<Boolean> checkAdminExists() {
        boolean hasAdmin = userService.hasAdminUser();
        return APIResponse.<Boolean>builder()
                .flag(true)
                .code(200)
                .message(hasAdmin ? "Admin user exists" : "No admin user found")
                .result(hasAdmin)
                .build();
    }

}