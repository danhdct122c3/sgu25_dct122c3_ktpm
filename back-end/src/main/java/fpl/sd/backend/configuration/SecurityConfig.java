package fpl.sd.backend.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(customJwtDecoder)
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/users/register").permitAll()
                .requestMatchers("/users/create-admin").permitAll()
                .requestMatchers("/users/create-admin-json").permitAll()
                .requestMatchers("/users/check-admin").permitAll()
                .requestMatchers("/brands/**").permitAll()
                .requestMatchers("/shoes/**").permitAll()
                .requestMatchers("/orders/apply-discount").permitAll()
                .requestMatchers("/chat/**").permitAll()
                .requestMatchers("/brands/init").permitAll()
                    .requestMatchers(
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/swagger-resources/**",
                            "/webjars/**"
                    ).permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        // Create a custom authorities converter
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Try different claim names commonly used for roles
            Collection<String> authorities = new ArrayList<>();

            // Check for "role" claim first
            Object roleClaim = jwt.getClaim("role");
            if (roleClaim != null) {
                if (roleClaim instanceof String) {
                    authorities.add((String) roleClaim);
                } else if (roleClaim instanceof Collection) {
                    authorities.addAll((Collection<String>) roleClaim);
                }
            }

            // If no role found, check for "scope" claim
            if (authorities.isEmpty()) {
                Object scopeClaim = jwt.getClaim("scope");
                if (scopeClaim != null) {
                    if (scopeClaim instanceof String) {
                        authorities.addAll(Arrays.asList(((String) scopeClaim).split(" ")));
                    } else if (scopeClaim instanceof Collection) {
                        authorities.addAll((Collection<String>) scopeClaim);
                    }
                }
            }

            // If still no authorities found, check for "authorities" claim
            if (authorities.isEmpty()) {
                Object authoritiesClaim = jwt.getClaim("authorities");
                if (authoritiesClaim != null && authoritiesClaim instanceof Collection) {
                    authorities.addAll((Collection<String>) authoritiesClaim);
                }
            }

            return authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });

        return jwtAuthenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:3000",
            "http://localhost:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
