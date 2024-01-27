package com.noah.integration.web;

import com.noah.db.document.User;
import com.noah.db.document.repository.UserRepository;
import com.noah.dto.UserDTO;
import com.noah.integration.MongoContainer;
import com.noah.service.UserManager;
import com.noah.web.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends MongoContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserManager userManager;

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void testGetUser() throws Exception {
        User mockUser = User.builder()
                .id("id")
                .username("test")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();
        userManager.createUser(mockUser);

        mockMvc.perform(get("/api/users/id").with(user(mockUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\":\"id\",\"username\":\"test\",\"createdAt\":\""+mockUser.getCreatedAt().truncatedTo(ChronoUnit.MILLIS)+"\"}"));
    }
}