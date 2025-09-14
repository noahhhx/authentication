package com.noah.jwt.integration.web;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ContentType;
import com.noah.jwt.db.User;
import com.noah.jwt.dto.LoginDto;
import com.noah.jwt.dto.TokenDto;
import com.noah.jwt.integration.config.PostgresTest;
import com.noah.jwt.service.UserManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles(value = "dev")
class AuthorityControllerTest extends PostgresTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserManager userManager;

  @Test
  @DisplayName("Add ROLE_ADMIN to user")
  void addRoleToUser() throws Exception {
    // Create user directly via service
    User mockUser = User.builder()
            .username(USERNAME + "2")
            .password(PASSWORD)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .build();
    userManager.createUser(mockUser);

    // Login to get token
    MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .content(LoginDto.builder().username(USERNAME + "2").password(PASSWORD).build().loginJson())
                    .contentType(ContentType.APPLICATION_JSON.toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    TokenDto tokenDTO = new ObjectMapper().readValue(loginResult.getResponse().getContentAsString(), TokenDto.class);

    // Call add authority endpoint
    String body = new ObjectMapper().writeValueAsString(Map.of("authority", "ROLE_ADMIN"));
    MvcResult addResult = mockMvc.perform(post("/api/authorities/" + tokenDTO.getUserId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDTO.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    List<String> authorities = new ObjectMapper().readValue(addResult.getResponse().getContentAsString(), new TypeReference<>(){});
    assertThat(authorities).contains("ROLE_ADMIN");
  }
}
