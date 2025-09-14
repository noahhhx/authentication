package com.noah.jwt.integration.config;

import com.noah.jwt.db.User;
import com.noah.jwt.db.repository.AuthorityRepository;
import com.noah.jwt.db.repository.UserRepository;
import com.noah.jwt.service.UserManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class PostgresTest {

  public static final String USERNAME = "USER";
  public static final String PASSWORD = "PASSWORD";
  @Container
  @ServiceConnection
  public static PostgreSQLContainer postgreSQLContainer =
          new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private AuthorityRepository authorityRepository;
  @Autowired
  private UserManager userManager;

  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.active.profiles", () -> "test");
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.username.url", postgreSQLContainer::getUsername);
    registry.add("spring.password.url", postgreSQLContainer::getPassword);
  }

  @BeforeAll
  public static void beforeAll() {
    postgreSQLContainer.start();
  }

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
    authorityRepository.deleteAll();
    userRepository.deleteAll();
  }
}
