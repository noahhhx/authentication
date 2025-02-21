package com.noah.jwt.integration.web;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ContentType;
import com.noah.jwt.dto.JwtTokenDto;
import com.noah.jwt.dto.LoginDto;
import com.noah.jwt.entities.User;
import com.noah.jwt.integration.config.PostgresTest;
import com.noah.jwt.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.noah.jwt.integration.config.JsonObjectMapper.asJsonString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest extends PostgresTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Test
  @DisplayName("Test get user")
  void testGetUser() throws Exception {

    User mockUser = User.builder()
            .username(USERNAME + "1")
            .password(PASSWORD)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .build();
    userService.createUser(mockUser);

    MvcResult result = mockMvc.perform(post("/api/auth/login")
                    .content(asJsonString(
                            LoginDto.builder()
                                .username(USERNAME + "1")
                                .password(PASSWORD)
                                .build()))
                    .contentType(ContentType.APPLICATION_JSON.toString()))
            .andReturn();
    String content = result.getResponse().getContentAsString();
    JwtTokenDto tokenDTO = new ObjectMapper().readValue(content, JwtTokenDto.class);

    mockMvc.perform(get("/api/users/" + tokenDTO.getUserId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDTO.getAccessToken())
                    .with(user(mockUser))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json("{\"id\":\"" + tokenDTO.getUserId() + "\"," +
                    "\"username\":\"USER1\",\"createdAt\":\"" + mockUser.getCreatedAt() + "\"}"));
  }
}