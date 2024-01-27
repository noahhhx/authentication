package com.noah.integration;

import com.noah.db.document.User;
import com.noah.service.UserManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserManagerTest extends MongoContainer {

    @Autowired
    private UserManager userManager;
    @Autowired
    MongoTemplate mongoTemplate;

    static final String USER = "USER";
    static final String PASSWORD = "PASSWORD";

    @Test
    @DisplayName("Create user works")
    @Order(1)
    void testCreateUser() {
        userManager.createUser(User.builder()
                .username(USER)
                .password(PASSWORD)
                .createdAt(LocalDateTime.now())
                .build());
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(USER));
        List<User> users = mongoTemplate.find(query, User.class);
        Assertions.assertEquals(1, users.size());
    }

    @Test
    @DisplayName("Duplicate username should throw error")
    @Order(2)
    void testDuplicateUsername() {
        User user = User.builder()
                .username(USER)
                .password(PASSWORD)
                .createdAt(LocalDateTime.now())
                .build();
        assertThrows(DuplicateKeyException.class, () -> userManager.createUser(user));
    }

    @Test
    @DisplayName("User exists")
    @Order(3)
    void testFindByUsername() {
        UserDetails userDetails = userManager.loadUserByUsername(USER);
        Assertions.assertNotNull(userDetails);
    }

    @Test
    @DisplayName("User doesn't exist")
    @Order(4)
    void testUsernameNotFound() {
        assertThrows(UsernameNotFoundException.class, () -> userManager.loadUserByUsername("fake_user"));
    }
}
