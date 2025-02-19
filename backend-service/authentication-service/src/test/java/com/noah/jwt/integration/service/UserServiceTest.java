package com.noah.jwt.integration.service;

import com.noah.jwt.entities.User;
import com.noah.jwt.integration.config.PostgresTest;
import com.noah.jwt.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
class UserServiceTest extends PostgresTest {

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("Create user works")
  void testCreateUser() {
    userService.createUser(User.builder()
            .username(USERNAME + "1")
            .password(PASSWORD)
            .createdAt(LocalDateTime.now())
            .build());
    assertTrue(userService.userExists(USERNAME + "1"));
  }

  @Test
  @DisplayName("Duplicate username should throw error")
  void testDuplicateUsername() {
    User user = User.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .createdAt(LocalDateTime.now())
            .build();
    assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(user));
  }

  @Test
  @DisplayName("User exists")
  void testFindByUsername() {
    UserDetails userDetails = userService.loadUserByUsername(USERNAME);
    Assertions.assertNotNull(userDetails);
  }

  @Test
  @DisplayName("User doesn't exist")
  void testUsernameNotFound() {
    assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("fake_user"));
  }

  @Test
  @DisplayName("Test Change Password by Username")
  void testChangePasswordByUsername() {
    String newPassword = PASSWORD + "new";

    //Test UserManager
    userService.changePasswordByUsername(USERNAME, PASSWORD, newPassword);
    UserDetails user = userService.loadUserByUsername(USERNAME);

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
                    userService.changePasswordByUsername(USERNAME + "1", PASSWORD, newPassword)
    );
  }

  @Test
  void testChangePasswordByUsernameWithIncorrectOldPassword() {
    String newPassword = PASSWORD + "new";
    String incorrectOldPassword = PASSWORD + "wrong";

    // Expect exception
    Assertions.assertThrows(BadCredentialsException.class, () ->
            userService.changePasswordByUsername(USERNAME, incorrectOldPassword, newPassword));
  }
}
