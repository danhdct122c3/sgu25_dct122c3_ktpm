package fpl.sd.backend.system;

import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.entity.Role;
import fpl.sd.backend.entity.User;
import fpl.sd.backend.repository.RoleRepository;
import fpl.sd.backend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DBInitialize {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing DB");
        return args -> {
            // Initialize roles first - Only 2 roles: USER and ADMIN
            if (roleRepository.count() == 0) {
                Role userRole = Role.builder().roles(RoleConstants.Role.USER).build();
                Role adminRole = Role.builder().roles(RoleConstants.Role.ADMIN).build();

                roleRepository.save(userRole);
                roleRepository.save(adminRole);

                log.info("Default roles (USER, ADMIN) have been created");
            }

            // Create default admin user
            if (userRepository.findByUsername("admin").isEmpty()) {
                Role adminRole = roleRepository.findByRoles(RoleConstants.Role.ADMIN)
                        .orElseThrow(() -> new RuntimeException("Admin Role not found"));

                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .createdAt(Instant.now())
                        .email("admin@gmail.com")
                        .address("123 Main St, Springfield")
                        .phone("1234567890")
                        .role(adminRole)
                        .isActive(true)
                        .build();

                userRepository.save(admin);
                log.warn("Default admin user has been created with username: admin, password: admin123");
            }

            // Create default regular user for testing
            if (userRepository.findByUsername("user").isEmpty()) {
                Role userRole = roleRepository.findByRoles(RoleConstants.Role.USER)
                        .orElseThrow(() -> new RuntimeException("User Role not found"));

                User user = User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("user123"))
                        .createdAt(Instant.now())
                        .email("user@gmail.com")
                        .address("456 Main St, Springfield")
                        .phone("0987654321")
                        .role(userRole)
                        .isActive(true)
                        .build();

                userRepository.save(user);
                log.warn("Default user has been created with username: user, password: user123");
            }
        };
    }
}
