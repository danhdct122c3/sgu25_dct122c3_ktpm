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
            // Initialize roles first
            if (roleRepository.count() == 0) {
                Role guestRole = Role.builder().roles(RoleConstants.Role.GUEST).build();
                Role memberRole = Role.builder().roles(RoleConstants.Role.MEMBER).build();
                Role managerRole = Role.builder().roles(RoleConstants.Role.MANAGER).build();
                Role adminRole = Role.builder().roles(RoleConstants.Role.ADMIN).build();

                roleRepository.save(guestRole);
                roleRepository.save(memberRole);
                roleRepository.save(managerRole);
                roleRepository.save(adminRole);

                log.info("Default roles have been created");
            }

            if (userRepository.findByUsername("admin").isEmpty()) {
                Role adminRole = roleRepository.findByRoles(RoleConstants.Role.ADMIN)
                        .orElseThrow(() -> new RuntimeException("Admin Role not found"));

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .createdAt(Instant.now())
                        .email("admin@gmail.com")
                        .address("123 Main St, Springfield")
                        .phone("1234567890")
                        .role(adminRole)
                        .isActive(true)
                        .build();

                userRepository.save(user);
                log.warn("Default admin user has been created with password admin123");
            }

            if (userRepository.findByUsername("manager").isEmpty()) {
                Role managerRole = roleRepository.findByRoles(RoleConstants.Role.MANAGER)
                        .orElseThrow(() -> new RuntimeException("Manager Role not found"));

                User manager = User.builder()
                        .username("manager")
                        .password(passwordEncoder.encode("manager123"))
                        .createdAt(Instant.now())
                        .email("manager123@yopmail.com")
                        .address("789 Main St, Springfield")
                        .phone("0987654321")
                        .role(managerRole)
                        .isActive(true)
                        .build();

                userRepository.save(manager);
                log.warn("Default manager user has been created with password manager123");
            }
        };
    }
}
