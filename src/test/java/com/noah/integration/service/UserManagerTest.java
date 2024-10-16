package com.noah.integration.service;

import com.noah.db.document.User;
import com.noah.integration.config.BaseTest;
import com.noah.integration.config.MultiDbTest;
import com.noah.service.UserManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
class UserManagerTest extends BaseTest {

    @Autowired
    private UserManager userManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MultiDbTest
    @DisplayName("Create user works")
    void testCreateUser() {
        userManager.createUser(User.builder()
                .username(USERNAME + "1")
                .password(PASSWORD)
                .createdAt(LocalDateTime.now())
                .build());
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(USERNAME));
        List<User> users = mongoTemplate.find(query, User.class);
        Assertions.assertEquals(1, users.size());
    }

    @Test
    @DisplayName("Duplicate username should throw error")
    void testDuplicateUsername() {
        User user = User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .createdAt(LocalDateTime.now())
                .build();
        assertThrows(DuplicateKeyException.class, () -> userManager.createUser(user));
    }

    @Test
    @DisplayName("User exists")
    void testFindByUsername() {
        UserDetails userDetails = userManager.loadUserByUsername(USERNAME);
        Assertions.assertNotNull(userDetails);
    }

    @Test
    @DisplayName("User doesn't exist")
    void testUsernameNotFound() {
        assertThrows(UsernameNotFoundException.class, () -> userManager.loadUserByUsername("fake_user"));
    }

    @Test
    @DisplayName("Test Change Password by Username")
    void testChangePasswordByUsername() {
        String newPassword = PASSWORD + "new";

        //Test UserManager
        userManager.changePasswordByUsername(USERNAME, PASSWORD, newPassword);
        UserDetails user = userManager.loadUserByUsername(USERNAME);

        // Verify password changed successfully
        Assertions.assertTrue(passwordEncoder.matches(newPassword, user.getPassword()));

    }

    @Test
    @DisplayName("Test Change Password by Bad Username")
    void testChangePasswordwithBadUsername() {
        String newPassword = PASSWORD + "new";

        //Test UserManager
        Assertions.assertThrows(
                UsernameNotFoundException.class, () ->
                userManager.changePasswordByUsername(USERNAME + "1", PASSWORD, newPassword)
        );
    }

    @Test
    void testChangePasswordByUsernameWithIncorrectOldPassword() {
        String newPassword = PASSWORD + "new";
        String incorrectOldPassword = PASSWORD + "wrong";

        // Expect exception
        Assertions.assertThrows(BadCredentialsException.class, () ->
                userManager.changePasswordByUsername(USERNAME, incorrectOldPassword, newPassword));
    }
}
