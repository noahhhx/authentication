package com.noah.integration.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noah.db.document.User;
import com.noah.dto.LoginDTO;
import com.noah.dto.SignupDTO;
import com.noah.dto.TokenDTO;
import com.noah.integration.MongoContainer;
import com.noah.service.UserManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest extends MongoContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserManager userManager;

    private static TokenDTO responseTokenDTO = null;
    private static final String USERNAME = "test";
    private static final String PASSWORD = "password";

    @Test
    @DisplayName("Register user")
    @Order(1)
    void testRegister() throws Exception {
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setUsername(USERNAME);
        signupDTO.setPassword(PASSWORD);
        String json = new ObjectMapper().writeValueAsString(signupDTO);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/auth/register")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        UserDetails loadedUser = userManager.loadUserByUsername(USERNAME);
        Assertions.assertEquals(USERNAME, loadedUser.getUsername());
    }

    @Test
    @DisplayName("Register with existing username")
    @Order(2)
    void testRegisterExistingUsername() throws Exception {
        SignupDTO signupDTO = new SignupDTO();
        signupDTO.setUsername(USERNAME);
        signupDTO.setPassword(PASSWORD);
        String json = new ObjectMapper().writeValueAsString(signupDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string("User already exists"));
    }

    @Test
    @DisplayName("Test Login")
    @Order(3)
    void testLogin() throws Exception {
        LoginDTO loginDTO = new LoginDTO(
                USERNAME,
                PASSWORD
        );
        String json = new ObjectMapper().writeValueAsString(loginDTO);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        responseTokenDTO = new ObjectMapper().readValue(
                result.getResponse().getContentAsString(),
                TokenDTO.class
        );
        Assertions.assertNotNull(responseTokenDTO);
    }

    @Test
    @DisplayName("Test Invalid Login")
    @Order(4)
    void testInvalidLogin() throws Exception {
        String json = new ObjectMapper().writeValueAsString(
                new LoginDTO(
                        USERNAME,
                        "password1"
                ));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Test Refresh Token")
    @Order(5)
    void testRefreshToken() throws Exception {
        String json = new ObjectMapper().writeValueAsString(responseTokenDTO);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/token")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
    }
}