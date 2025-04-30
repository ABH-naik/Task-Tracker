package com.tasktracker.service;

import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.RoleType;
import com.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User findOrCreateUser(String oauthProviderId, String email) {
        return userRepository.findByIdentifier(oauthProviderId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .oauthProviderId(oauthProviderId)
                            .role(RoleType.READ_ONLY_USER) // Default role
                            .build();
                    return userRepository.save(newUser);
                });
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
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
}