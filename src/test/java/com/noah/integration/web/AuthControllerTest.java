package com.noah.integration.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noah.dto.LoginDTO;
import com.noah.dto.SignupDTO;
import com.noah.dto.TokenDTO;
import com.noah.integration.MongoContainer;
import com.noah.service.UserManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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

@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTest extends MongoContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserManager userManager;

    @Test
    @DisplayName("Register user")
    void testRegister() throws Exception {
        String username = "test";
        String json = getLoginJson(username, PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/auth/register")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        UserDetails loadedUser = userManager.loadUserByUsername(username);
        Assertions.assertEquals(username, loadedUser.getUsername());
    }

    @Test
    @DisplayName("Register with existing username")
    void testRegisterExistingUsername() throws Exception {
        String json = new ObjectMapper().writeValueAsString(
                SignupDTO.builder()
                        .username(USERNAME)
                        .password(PASSWORD)
                        .build()
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string("User already exists"));
    }

    @Test
    @DisplayName("Test Login")
    void testLogin() throws Exception {
        String json = getLoginJson(USERNAME, PASSWORD);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        TokenDTO responseTokenDTO = new ObjectMapper().readValue(
                result.getResponse().getContentAsString(),
                TokenDTO.class
        );
        Assertions.assertNotNull(responseTokenDTO);
    }

    @Test
    @DisplayName("Test Invalid Login")
    void testInvalidLogin() throws Exception {
        String json = getLoginJson(USERNAME, PASSWORD + "wrong");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @DisplayName("Test Refresh Token")
    void testRefreshToken() throws Exception {
        String loginJson = getLoginJson(USERNAME, PASSWORD);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content(loginJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        TokenDTO responseTokenDTO = new ObjectMapper().readValue(
                result.getResponse().getContentAsString(),
                TokenDTO.class
        );
        Assertions.assertNotNull(responseTokenDTO);

        String tokenJson = new ObjectMapper().writeValueAsString(responseTokenDTO);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/token")
                        .content(tokenJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    private String getLoginJson(String username, String password) throws JsonProcessingException {
        LoginDTO loginDTO = new LoginDTO(
                username,
                password
        );
        return new ObjectMapper().writeValueAsString(loginDTO);
    }
}