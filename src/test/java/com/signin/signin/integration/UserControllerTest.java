package com.signin.signin.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.signin.signin.Controllers.DTOs.LoginUserDto;
import com.signin.signin.Models.UserModel;
import com.signin.signin.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.AbstractMockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        this.userRepository.deleteAll();
    }

    @Test
    public void shouldCreateNewUser() throws Exception {
        UserModel user = new UserModel();

        user.setName("test of silva");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        mockMvc.perform(post("/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    public void shouldMakeLogin() throws Exception {

        UserModel user = new UserModel();

        user.setName("test of silva");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        mockMvc.perform(post("/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        LoginUserDto userDto = new LoginUserDto(user.getEmail(), user.getPassword());

        MvcResult result = mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        String token = node.get("token").asText();

        assertNotNull(token);
        assertFalse(token.isBlank());

    }

    @Test
    public void shouldAccessProtectedEndpointWithToken() throws Exception {

        UserModel user = new UserModel();
        user.setName("test of silva");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

        LoginUserDto loginDto = new LoginUserDto(user.getEmail(), user.getPassword());

        MvcResult result = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(user.getName()));
    }

    @Test
    public void shouldThrowUnauthorized() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldDeletUser() throws Exception {
        UserModel user = new UserModel();

        user.setName("test of silva");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.email").value("test@gmail.com"));

        LoginUserDto userDto = new LoginUserDto("test@gmail.com", "12345678");

        MvcResult result = mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        mockMvc.perform(delete("/users")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        UserModel user = new UserModel();

        user.setName("test of silva");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").isNotEmpty())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.email").value("test@gmail.com"));

        LoginUserDto userDto = new LoginUserDto("test@gmail.com", "12345678");

        MvcResult result = mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        MvcResult resultGetUser = mockMvc.perform(get("/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").isNotEmpty())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        String responseBodyGet = resultGetUser.getResponse().getContentAsString();
        long id = objectMapper.readTree(responseBodyGet).get("id").asLong();

        UserModel userToUpdate = new UserModel();

        userToUpdate.setId(id);
        userToUpdate.setName("test of silva update");
        userToUpdate.setEmail("test@gmail.com");
        userToUpdate.setPassword("12345678");

        mockMvc.perform(put("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.name").value("test of silva update"))
                .andExpect(jsonPath("$.email").isNotEmpty())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.id").isNumber());

    }

}
