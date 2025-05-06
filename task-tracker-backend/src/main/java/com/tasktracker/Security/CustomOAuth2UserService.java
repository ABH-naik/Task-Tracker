package com.tasktracker.Security;

import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.RoleType;
import com.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service

@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        try {
            OAuth2User oauthUser = super.loadUser(request);
            Map<String, Object> attributes = oauthUser.getAttributes();

            // 1. Validate critical fields
            String email = validateEmail(attributes);
            String oauthId = validateOAuthId(attributes);
            String name = getNameWithFallback(attributes, email);

            // 2. Find or create user
            User user = userRepository.findByOauthProviderId(oauthId)
                    .or(() -> userRepository.findByEmail(email)) // Prevent duplicate emails
                    .orElseGet(() -> createNewUser(oauthId, email, name));

            // 3. Check account status


            return new CustomOAuth2User(user);

        } catch (Exception ex) {
            throw ex;
        }
    }

    private String validateEmail(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        Boolean verified = (Boolean) attributes.get("email_verified");

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("Email not provided");
        }
        if (verified == null || !verified) {
            throw new OAuth2AuthenticationException("Email not verified");
        }
        return email;
    }

    private String validateOAuthId(Map<String, Object> attributes) {
        String oauthId = (String) attributes.get("sub");
        if (oauthId == null || oauthId.isBlank()) {
            throw new OAuth2AuthenticationException("OAuth ID not found");
        }
        return oauthId;
    }

    private String getNameWithFallback(Map<String, Object> attributes, String email) {
        String name = (String) attributes.get("name");
        return (name != null && !name.isBlank()) ? name : email.split("@")[0];
    }

    private User createNewUser(String oauthId, String email, String name) {
        return userRepository.save(
                User.builder()
                        .email(email)
                        .name(name)
                        .oauthProviderId(oauthId)
                        .role(RoleType.READ_ONLY_USER)
                        .build()
        );
    }
}