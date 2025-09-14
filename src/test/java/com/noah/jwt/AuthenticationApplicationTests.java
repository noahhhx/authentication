package com.noah.jwt;

import com.noah.jwt.integration.config.PostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(value = "dev")
class AuthenticationApplicationTests extends PostgresTest {

  @Test
  void contextLoads() { // NOSONAR - Test it starts up

  }
}
