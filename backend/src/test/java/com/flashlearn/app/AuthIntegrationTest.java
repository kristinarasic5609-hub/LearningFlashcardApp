package com.flashlearn.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashlearn.app.model.dto.LoginRequest;
import com.flashlearn.app.model.dto.RegisterRequest;
import com.flashlearn.app.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LearningResultRepository learningResultRepository;

    @Autowired
    private LearningSessionRepository learningSessionRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private FlashcardSetRepository flashcardSetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        learningResultRepository.deleteAll();
        learningSessionRepository.deleteAll();
        flashcardRepository.deleteAll();
        flashcardSetRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void registersNewUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@user.com");
        request.setUsername("newuser");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("new@user.com"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void rejectsDuplicateRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("dup@user.com");
        request.setUsername("user1");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void logsInWithValidCredentials() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("login@user.com");
        registerRequest.setUsername("loginuser");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("login@user.com");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void rejectsInvalidLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("missing@user.com");
        loginRequest.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deniesAdminAccessForRegularUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@test.com");
        request.setUsername("regular");
        request.setPassword("password123");

        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
