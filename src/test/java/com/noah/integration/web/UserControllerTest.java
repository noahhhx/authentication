package com.noah.integration.web;

import com.noah.db.document.User;
import com.noah.integration.MongoContainer;
import com.noah.service.UserManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest extends MongoContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserManager userManager;

    @Test
    void testGetUser() throws Exception {
        User mockUser = User.builder()
                .id("id")
                .username(USERNAME + "1")
                .password(PASSWORD)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        userManager.createUser(mockUser);

        mockMvc.perform(get("/api/users/id").with(user(mockUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\":\"id\",\"username\":\"USER1\",\"createdAt\":\""+mockUser.getCreatedAt()+"\"}"));
    }
}