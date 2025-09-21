package com.tasktracker.Security;

import com.tasktracker.model.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // ← ADD THIS LINE

public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow preflight OPTIONS requests for ALL endpoints
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // ← THIS IS CRITICAL

                        // Public endpoints
                        .requestMatchers("/api/auth/**", "/api/test/generate-token").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Expose roles endpoint publicly (consider securing this)
                        .requestMatchers(HttpMethod.GET, "/api/roles/**").permitAll()

                        // Project endpoints
                        .requestMatchers(HttpMethod.POST,   "/api/projects/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/projects/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/projects/**").hasAnyRole("ADMIN", "TASK_CREATOR", "READ_ONLY_USER")

                        // Task endpoints
                        .requestMatchers(HttpMethod.POST,   "/api/tasks/**").hasAnyRole("ADMIN", "TASK_CREATOR")
                        .requestMatchers(HttpMethod.PUT,    "/api/tasks/**").hasAnyRole("ADMIN", "TASK_CREATOR", "READ_ONLY_USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/**").hasAnyRole("ADMIN", "TASK_CREATOR", "READ_ONLY_USER")
                        .requestMatchers(HttpMethod.GET,    "/api/tasks/**").hasAnyRole("ADMIN", "TASK_CREATOR", "READ_ONLY_USER")

                        // User management - FIX THIS SECTION
                        .requestMatchers(HttpMethod.GET,   "/api/users/**").hasAnyRole("ADMIN", "READ_ONLY_USER","TASK_CREATOR")
                        .requestMatchers(HttpMethod.POST,   "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,   "/api/users/**").hasRole("ADMIN") // ← This should cover role updates
                        .requestMatchers(HttpMethod.DELETE,   "/api/users/**").hasRole("ADMIN")

                        // Role management - ADD EXPLICIT RULES
                        .requestMatchers(HttpMethod.PUT, "/api/roles/**").hasRole("ADMIN") // ← ADD THIS

                        // Allow static frontend assets
                        .requestMatchers(
                                "/favicon.ico",
                                "/index.html",
                                "/static/**",
                                "/manifest.json",
                                "/logo192.png",
                                "/logo512.png"
                        ).permitAll()

                        // Everything else is denied
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .cors();

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        return NimbusJwtDecoder.withSecretKey(key).build();
    }


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        // Add debug logging
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var authorities = authoritiesConverter.convert(jwt);
            System.out.println("DEBUG: Extracted authorities from JWT: " + authorities);
            System.out.println("DEBUG: JWT claims: " + jwt.getClaims());
            return authorities;
        });

        return jwtConverter;
    }


}
