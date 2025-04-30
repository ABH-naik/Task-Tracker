package com.tasktracker.repository;

import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;


import com.tasktracker.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find by email or OAuth provider ID (for login)
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.oauthProviderId = :identifier")
    Optional<User> findByIdentifier(@Param("identifier") String identifier);

    // Update OAuth provider ID for existing users
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.oauthProviderId = :providerId WHERE u.id = :userId")
    int updateOAuthProviderId(@Param("userId") Long userId, @Param("providerId") String providerId);

    // Find users by role (for admin dashboards)
    @Query("SELECT u FROM User u WHERE u.role = :role ORDER BY u.name")
    List<User> findByRole(@Param("role") RoleType role);
}
