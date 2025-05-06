package com.tasktracker.Security;

import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.RoleType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user) {
        this.user = user;
        this.attributes = Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "roles", user.getRole().name()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getName() {
        return user.getName();
    }
    public String getEmail() {
        return user.getEmail();
    }

    public String getOAuthProviderId() {
        return user.getOauthProviderId();
    }

}