package com.noah.integration.web;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ContentType;
import com.noah.db.User;
import com.noah.dto.LoginDTO;
import com.noah.dto.TokenDTO;
import com.noah.integration.config.PostgresTest;
import com.noah.service.UserManager;
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
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest extends PostgresTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserManager userManager;

    @Test
    @DisplayName("Test get user")
    void testGetUser() throws Exception {

        User mockUser = User.builder()
                .username(USERNAME + "1")
                .password(PASSWORD)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        userManager.createUser(mockUser);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .content(LoginDTO.builder().username(USERNAME + "1").password(PASSWORD).build().loginJson())
                        .contentType(ContentType.APPLICATION_JSON.toString()))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        TokenDTO tokenDTO = new ObjectMapper().readValue(content, TokenDTO.class);

        mockMvc.perform(get("/api/users/" + tokenDTO.getUserId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDTO.getAccessToken())
                        .with(user(mockUser))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\":\"" + tokenDTO.getUserId() + "\",\"username\":\"USER1\",\"createdAt\":\""+mockUser.getCreatedAt()+"\"}"));
    }
}