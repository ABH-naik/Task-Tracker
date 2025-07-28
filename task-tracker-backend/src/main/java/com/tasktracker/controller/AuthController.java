package com.tasktracker.controller;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.RoleType;
import com.tasktracker.repository.UserRepository;
import com.tasktracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@JsonPropertyOrder({"jwt","userId", "userId", "isAdmin", "isTaskCreator", "readonly", "expiration", "isError"})
@CrossOrigin(origins = "http://localhost:3000") // CORS support for frontend
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepos;

    private final String CLIENT_ID = "638488493284-l06kghp1u9q3qtov19s99pnmf64qs90m.apps.googleusercontent.com";

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String email) {
        User user = userRepos.findByEmail(email).orElse(null);

        if (user == null) {
            Map<String, Object> errorResponse = Map.of(
                    "isError", true,
                    "message", "User not found"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        return ResponseEntity.ok(buildLoginResponse(user));
    }

    @PostMapping("/google")
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestBody Map<String, String> request) {
        String idToken = request.get("token");

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(CLIENT_ID)).build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);

            if (googleIdToken != null) {
                String email = googleIdToken.getPayload().getEmail();

                User user = userRepos.findByEmail(email).orElse(null);

                if (user == null) {
                    Map<String, Object> errorResponse = Map.of(
                            "isError", true,
                            "message", "User not found for Google account"
                    );
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                }

                return ResponseEntity.ok(buildLoginResponse(user));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("isError", true, "message", "Invalid Google token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("isError", true, "message", "Google auth failed"));
        }
    }

    private Map<String, Object> buildLoginResponse(User user) {
        String token = userService.generateJwtToken(user);
        long expirationTime = System.currentTimeMillis() + 3600000;
        String expirationIST = Instant.ofEpochMilli(expirationTime)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return Map.of(
                "userId", user.getId(),
                "name", user.getName(),
                "isAdmin", user.getRole() == RoleType.ADMIN,
                "readonly", user.getRole() == RoleType.READ_ONLY_USER,
                "isTaskCreator", user.getRole() == RoleType.TASK_CREATOR,
                "isError", false,
                "jwt", token,
                "expiration", expirationIST
        );
    }
}
