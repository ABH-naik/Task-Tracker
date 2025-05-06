package com.tasktracker.model.entity;

import com.tasktracker.model.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Table(name = "users")
@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @Email(message = "Enter a valid email")
    private String email;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();
    @Column(name = "oauth_provider_id", updatable = false)
    private String oauthProviderId; // Store Google/GitHub's unique user ID
    @Column(name = "email_verified", updatable = false)
    private Boolean emailVerified = false;

}