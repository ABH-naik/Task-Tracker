package com.tasktracker.service;

import com.tasktracker.controller.UserController;
import com.tasktracker.dto.response.ProjectResponse;
import com.tasktracker.dto.response.UserResponse;
import com.tasktracker.exception.UserNotFoundException;
import com.tasktracker.model.entity.Project;
import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.RoleType;
import com.tasktracker.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;


    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;



    @Transactional
    public User findOrCreateUser(String email, String name, String oauthProviderId,boolean emailVerified) {
        return userRepository.findByIdentifier(oauthProviderId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .name(name)
                            .email(email)
                            .oauthProviderId(oauthProviderId)
                            .role(RoleType.READ_ONLY_USER)// Default role
                            .emailVerified(emailVerified)
                            .build();
                    return userRepository.save(newUser);
                });
    }
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));



        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> projects = userRepository.findAll();
        return projects.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public User updateUserRole(Long userId, RoleType newRole) {
        User user = getUserById(userId);
        user.setRole(newRole);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByRole(RoleType role) {
        return userRepository.findByRole(role);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                "", // No password for OAuth
                getAuthorities(user.getRole())
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(RoleType role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public String generateJwtToken(User user) {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(user.getEmail())
//                .claim("roles", user.getRole().name())
                .claim("roles", Collections.singletonList(user.getRole().name())) // <- Now it's a LIST

                .setIssuer("TaskTracker")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}