package com.noah.integration.config;

import com.noah.db.document.User;
import com.noah.db.document.repository.UserRepository;
import com.noah.service.UserManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class BaseTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserManager userManager;

  public static final String USERNAME = "USER";
  public static final String PASSWORD = "PASSWORD";

  @BeforeEach
  public void beforeEach() {
    userManager.createUser(User.builder()
        .username(USERNAME)
        .password(PASSWORD)
        .createdAt(LocalDateTime.now())
        .build());
  }

  @AfterEach
  public void afterEach() {
    userRepository.deleteAll();
  }
  
}
