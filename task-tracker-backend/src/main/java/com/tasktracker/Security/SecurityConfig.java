package com.tasktracker.Security;

import com.tasktracker.model.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Allow preflight OPTIONS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints
                        .requestMatchers("/api/auth/**", "/api/test/generate-token").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Expose roles endpoint publicly
                        .requestMatchers(HttpMethod.GET, "/api/roles/**").permitAll()

                        // Project endpoints
                        .requestMatchers(HttpMethod.POST,   "/api/projects/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/projects/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/projects/**").hasAnyRole("ADMIN", "TASK_CREATOR", "READ_ONLY_USER")

                        // Task endpoints
                        .requestMatchers(HttpMethod.POST,   "/api/tasks/**").hasAnyRole("ADMIN", "TASK_CREATOR")
                        .requestMatchers(HttpMethod.PUT,    "/api/tasks/**").hasAnyRole("ADMIN", "TASK_CREATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/**").hasAnyRole("ADMIN", "TASK_CREATOR")
                        .requestMatchers(HttpMethod.GET,    "/api/tasks/**").hasAnyRole("ADMIN", "TASK_CREATOR", "READ_ONLY_USER")

                        // User management
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/users/**").hasAnyRole("ADMIN", "READ_ONLY_USER")
                        .requestMatchers(HttpMethod.PUT,   "/api/users/**").hasAnyRole("ADMIN", "TASK_CREATOR")
                        // grant ADMIN and TASK_CREATOR the right to update tasks
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/**")
                        .hasAnyRole("ADMIN", "TASK_CREATOR")
                        .requestMatchers("/favicon.ico").permitAll()
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
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix("ROLE_");
        converter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var authorities = converter.convert(jwt);
            System.out.println(">> JWT Roles from Token: " + authorities);
            return authorities;
        });

        return jwtConverter;
    }
}
