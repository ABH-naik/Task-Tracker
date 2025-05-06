//package com.tasktracker.repository;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import static org.assertj.core.api.Assertions.assertThat;
//
////@DataJpaTest
////@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
////class UserRepositoryTest {
////
////    @Autowired
////    private UserRepository userRepository;
////
////    @Test
////    void shouldFindUserByIdentifier() {
////        // Given test data from SQL
////
////        // When
////        var userByEmail = userRepository.findByIdentifier("admin@company.com");
////        var userByOAuthId = userRepository.findByIdentifier("google-oauth2|123");
////
////        // Then
////        assertThat(userByEmail).isPresent();
////        assertThat(userByOAuthId).isPresent();
////        assertThat(userByEmail.get().getId()).isEqualTo(userByOAuthId.get().getId());
////    }
////}
//
//
//import com.tasktracker.model.entity.User;
//import com.tasktracker.model.enums.RoleType;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    void shouldFindUserByIdentifier() {
//        // Given
//        User user = new User();
//        user.setEmail("admin1@company.com");
//        user.setOauthProviderId("google-oauth2|123");
//        user.setName("System Admin");
//        user.setRole(RoleType.ADMIN);
//
//        userRepository.save(user);
//
//        // When
//        Optional<User> userByEmail = userRepository.findByIdentifier("admin1@company.com");
//        Optional<User> userByOAuthId = userRepository.findByIdentifier("google-oauth2|123");
//
//        // Then
//        assertThat(userByEmail).isPresent();
//        assertThat(userByOAuthId).isPresent();
//        assertThat(userByEmail.get().getId()).isEqualTo(userByOAuthId.get().getId());
//    }
//}
