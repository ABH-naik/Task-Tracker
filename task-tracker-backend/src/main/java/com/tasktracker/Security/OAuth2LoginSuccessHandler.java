//package com.tasktracker.Security;
//
//import com.tasktracker.model.entity.User;
//import com.tasktracker.service.UserService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
//
//    private final JwtTokenUtil jwtTokenUtil;
//    private final UserService userService;
//
//    public OAuth2LoginSuccessHandler(JwtTokenUtil jwtTokenUtil, UserService userService) {
//        this.jwtTokenUtil = jwtTokenUtil;
//        this.userService = userService;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        Authentication authentication) throws IOException {
//
//        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
//
//        // 1. Find or create user
//        User user = userService.findOrCreateUser(
//                oauthUser.getOAuthProviderId(),
//                oauthUser.getEmail(),
//                oauthUser.getName()
//        );
//
//        // 2. Generate JWT
//        String token = jwtTokenUtil.generateToken(
//                new CustomUserDetails(user)
//        );
//
//        // 3. Return token to frontend
//        response.setContentType("application/json");
//        response.getWriter().write(String.format(
//                "{\"token\":\"%s\", \"email\":\"%s\", \"role\":\"%s\"}",
//                token,
//                user.getEmail(),
//                user.getRole()
//        ));
//    }
//}