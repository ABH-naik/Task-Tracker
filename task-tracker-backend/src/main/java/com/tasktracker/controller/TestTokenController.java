//package com.tasktracker.controller;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.Date;
//import java.util.List;
//
//@RestController
//public class TestTokenController {
//    @Value("${jwt.secret}")
//    private String jwtSecret;
//
//    @Value("${jwt.expiration}")
//    private long jwtExpirationMs;
//
//    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
//    private String issuerUri;
//
//    @GetMapping("/api/test/generate-token")
//    public String generateTestToken(
//            @RequestParam String email,
//            @RequestParam List<String> roles) {
//
//        // Generate token (no restrictions on generation)
//        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
//        Key key = Keys.hmacShaKeyFor(keyBytes);
//
//        return Jwts.builder()
//                .setSubject(email)
//                .claim("roles", roles)
//                .setIssuer(issuerUri)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
//                .signWith(key, SignatureAlgorithm.HS256)
//
//                .compact();
//    }
//
//}
