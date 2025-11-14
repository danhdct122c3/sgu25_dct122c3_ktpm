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
            // Initialize roles first - Create CUSTOMER, STAFF, MANAGER, ADMIN
            if (roleRepository.count() == 0) {
                Role customerRole = Role.builder().roles(RoleConstants.Role.CUSTOMER).build();
                Role staffRole = Role.builder().roles(RoleConstants.Role.STAFF).build();
                Role managerRole = Role.builder().roles(RoleConstants.Role.MANAGER).build();
                Role adminRole = Role.builder().roles(RoleConstants.Role.ADMIN).build();

                roleRepository.save(customerRole);
                roleRepository.save(staffRole);
                roleRepository.save(managerRole);
                roleRepository.save(adminRole);

                log.info("Default roles (CUSTOMER, STAFF, MANAGER, ADMIN) have been created");
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

            // Create default manager user for testing
            if (userRepository.findByUsername("manager").isEmpty()) {
                Role managerRole = roleRepository.findByRoles(RoleConstants.Role.MANAGER)
                        .orElseThrow(() -> new RuntimeException("Manager Role not found"));

                User manager = User.builder()
                        .username("manager")
                        .password(passwordEncoder.encode("manager123"))
                        .createdAt(Instant.now())
                        .email("manager@gmail.com")
                        .address("789 Industry Rd, Springfield")
                        .phone("0930123456")
                        .role(managerRole)
                        .isActive(true)
                        .build();

                userRepository.save(manager);
                log.warn("Default manager has been created with username: manager, password: manager123");
            }

            // Create default staff user for testing
            if (userRepository.findByUsername("staff").isEmpty()) {
                Role staffRole = roleRepository.findByRoles(RoleConstants.Role.STAFF)
                        .orElseThrow(() -> new RuntimeException("Staff Role not found"));

                User staff = User.builder()
                        .username("staff")
                        .password(passwordEncoder.encode("staff123"))
                        .createdAt(Instant.now())
                        .email("staff@gmail.com")
                        .address("101 Warehouse Ave, Springfield")
                        .phone("0912345678")
                        .role(staffRole)
                        .isActive(true)
                        .build();

                userRepository.save(staff);
                log.warn("Default staff has been created with username: staff, password: staff123");
            }

            // Create default customer user for testing
            if (userRepository.findByUsername("user").isEmpty()) {
                Role customerRole = roleRepository.findByRoles(RoleConstants.Role.CUSTOMER)
                        .orElseThrow(() -> new RuntimeException("Customer Role not found"));

                User user = User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("user123"))
                        .createdAt(Instant.now())
                        .email("user@gmail.com")
                        .address("456 Main St, Springfield")
                        .phone("0987654321")
                        .role(customerRole)
                        .isActive(true)
                        .build();

                userRepository.save(user);
                log.warn("Default customer has been created with username: user, password: user123");
            }
        };
    }
}
