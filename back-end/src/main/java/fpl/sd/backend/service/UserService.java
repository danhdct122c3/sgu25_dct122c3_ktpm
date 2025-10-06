package fpl.sd.backend.service;

import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.dto.PageResponse;
import fpl.sd.backend.dto.request.PasswordChangeRequest;
import fpl.sd.backend.dto.request.UserCreateRequest;
import fpl.sd.backend.dto.response.UserResponse;
import fpl.sd.backend.dto.request.UserUpdateRequest;
import fpl.sd.backend.entity.*;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.mapper.UserMapper;
import fpl.sd.backend.repository.RoleRepository;
import fpl.sd.backend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void validateUserCreateRequest(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    public UserResponse createUser(UserCreateRequest request) {
        validateUserCreateRequest(request);
        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(Instant.now());
        user.setAddress("This field need to be updated");

        // Assign MEMBER role for regular user registration
        Role memberRole = roleRepository.findByRoles(RoleConstants.Role.USER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.setRole(memberRole);

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

//    public List<UserResponse> getAllUsers(){
//        List<User> users = userRepository.findAll();
//        return users.stream()
//                .map(userMapper::toUserResponse)
//                .toList();
//    }



    public List<UserResponse> getAllUsers() {

        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> {
                    UserResponse response = userMapper.toUserResponse(user);
                    if (user.getRole() != null) {
                        response.setRoleName(user.getRole().getRoles().name());
                    }
                    return response;
                })
                .toList();
    }

    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    // Check if user owns this userId (compare username from token with user's username)
    public boolean isUserOwner(String userId, String usernameFromToken) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            return user != null && user.getUsername().equals(usernameFromToken);
        } catch (Exception e) {
            log.error("Error checking user ownership: {}", e.getMessage());
            return false;
        }
    }

    public UserResponse updateUser(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Username validation - skip if updating own record
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getIsActive() != null) {  // Use getIsActive here, which corresponds to the isActive field
            user.setActive(request.getIsActive());
        }

        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    // Update user bằng username (cho admin)
    public UserResponse updateUserByUsername(String username, UserUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        // Kiểm tra trùng username (nếu đổi username)
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // Kiểm tra trùng email
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // Cập nhật các field
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }

        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserByUserName(String username) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return userMapper.toUserResponse(existingUser.get());
        } else {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
    }

    public void changePassword(PasswordChangeRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

    }


    public boolean verifyOtp(String email, String otpCode) {
        Optional<User> existingUser = userRepository.findByEmailAndOtpCode(email, otpCode);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // Check if OTP has expired
            if (user.getOtpExpiryDate().isBefore(LocalDateTime.now())) {
                log.warn("OTP expired for email: {}", email);
                return false;
            }

            return true;
        }

        return false;
    }


    public boolean resetPassword(String email, String newPassword, String confirmPassword) {
        // Validate password match
        if (!newPassword.equals(confirmPassword)) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(Instant.now());
            user.setOtpCode(null);
            user.setOtpExpiryDate(null);

            userRepository.save(user);

            log.info("Password reset successful for email: {}", email);
            return true;
        }

        return false;
    }





    private Sort createSort(String sortOrder) {

        String date = "createdAt";
        if (sortOrder == null) {
            return Sort.by(Sort.Direction.ASC, date);
        }

        return switch (sortOrder.toLowerCase()) {
            case "updesc" -> Sort.by(Sort.Direction.DESC, "updatedAt");
            case "upasc" -> Sort.by(Sort.Direction.ASC, "updatedAt");
            case "date_asc" -> Sort.by(Sort.Direction.ASC, date);
            default -> Sort.by(Sort.Direction.DESC, date);
        };
    }



    public List<UserResponse> getUserByIsActive(boolean isActive) {
        List<User> users = userRepository.findByIsActive(isActive);
        return users.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public List<UserResponse> getUserByRole(String roleName) {
        RoleConstants.Role roleEnum = RoleConstants.getRoleFromString(roleName);
        if (roleEnum == null) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }

        Role role = roleRepository.findByRoles(roleEnum)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(userMapper::toUserResponse)
                .toList();
    }


    public PageResponse<UserResponse> getUserPaging(
            String username,
            String roleName,  // Thay roleId bằng roleName
            Boolean isActive,
            int page,
            int size,
            String sortOrder
    ) {
        Sort sort = createSort(sortOrder);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        // Gọi phương thức findUserByFilters với roleName
        Page<User> userData;
        if (isActive == null) {
            userData = userRepository.findUserByFilters(username, roleName, null, pageable);  // Truyền roleName
        } else {
            userData = userRepository.findUserByFilters(username, roleName, isActive, pageable); // Truyền roleName
        }

        var userList = userData.getContent()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();

        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .pageSize(userData.getSize())
                .totalPages(userData.getTotalPages())
                .totalElements(userData.getTotalElements())
                .data(userList)
                .build();
    }

    public UserResponse createAdminUser(String username, String email, String password, String fullName) {
        // Check if admin already exists
        if (userRepository.existsByUsername(username)) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // Create admin user
        User adminUser = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .address("Admin Address")
                .phone("0000000000")
                .isActive(true)
                .createdAt(Instant.now())
                .build();

        // Set admin role (ID = 1 for ADMIN)
        Role adminRole = roleRepository.findByRoles(RoleConstants.Role.ADMIN)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        adminUser.setRole(adminRole);

        userRepository.save(adminUser);
        return userMapper.toUserResponse(adminUser);
    }

    public boolean hasAdminUser() {
        Role adminRole = roleRepository.findByRoles(RoleConstants.Role.ADMIN)
                .orElse(null);
        if (adminRole == null) return false;

        return userRepository.findByRole(adminRole).size() > 0;
    }

}
