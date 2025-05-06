package com.tasktracker.Security;

import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.RoleType;
import com.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthProvider {

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    public User authenticate(String token) {
        try {
            // Decode the JWT token
            Jwt jwt = jwtDecoder.decode(token);

            if (!issuerUri.equals(jwt.getIssuer())) {
                throw new JwtException("Invalid issuer");
            }

            // 2. Validate custom claims
            String email = jwt.getClaimAsString("email");
            if (email == null || email.isBlank()) {
                throw new JwtException("Missing email");
            }

            // Extract email from the JWT claims

            // Check if the user exists in the database
            return userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        // If user does not exist, create a new user with default role
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setRole(RoleType.READ_ONLY_USER);  // Assign default role as READ_ONLY_USER
                        return userRepository.save(newUser);  // Save the new user
                    });

        } catch (JwtException e) {
            throw new JwtException("Invalid JWT token", e);
        }
    }
}
