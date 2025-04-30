package com.tasktracker.model.entity;

import com.tasktracker.model.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

   @Entity
    @Table(name = "users")
    @Data
   @Builder
   @AllArgsConstructor
   @NoArgsConstructor
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(nullable = false, unique = true)
        private String email;
        @Column(nullable = false)
        private String name;
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private RoleType role;
        @CreationTimestamp
        private LocalDateTime createdAt;
        @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Project> projects = new ArrayList<>();
    @Column(name = "oauth_provider_id", unique = true)
    private String oauthProviderId; // Store Google/GitHub's unique user ID

    }
