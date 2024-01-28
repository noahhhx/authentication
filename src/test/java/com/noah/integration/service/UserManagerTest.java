package com.noah.integration.service;

import com.noah.db.document.User;
import com.noah.integration.MongoContainer;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
class UserManagerTest extends MongoContainer {

    @Autowired
    private UserManager userManager;
    @Autowired
    MongoTemplate mongoTemplate;

    @Test
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
}
