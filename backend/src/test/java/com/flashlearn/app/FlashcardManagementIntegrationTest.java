package com.flashlearn.app;

import com.flashlearn.app.model.dto.CreateFlashcardSetRequest;
import com.flashlearn.app.model.dto.RegisterRequest;
import com.flashlearn.app.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FlashcardManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private LearningResultRepository learningResultRepository;

    @Autowired
    private LearningSessionRepository learningSessionRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private FlashcardSetRepository flashcardSetRepository;

    @BeforeEach
    void cleanDatabase() {
        learningResultRepository.deleteAll();
        learningSessionRepository.deleteAll();
        statisticsRepository.deleteAll();
        flashcardRepository.deleteAll();
        flashcardSetRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void flashcardManagementPersistsToDatabase() throws Exception {
        String token = registerAndGetToken();

        CreateFlashcardSetRequest setRequest = new CreateFlashcardSetRequest();
        setRequest.setTitle("Biology");
        setRequest.setDescription("Cell biology basics");
        setRequest.setCategory("Science");

        String setResponse = mockMvc.perform(post("/api/sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(setRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Biology"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String setId = objectMapper.readTree(setResponse).get("id").asText();

        mockMvc.perform(post("/api/sets/" + setId + "/cards")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"question":"What is a cell?","answer":"Basic unit of life"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.question").value("What is a cell?"));

        assertEquals(1, flashcardRepository.count());
        assertEquals(1, flashcardSetRepository.count());
    }

    private String registerAndGetToken() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("manager@test.com");
        registerRequest.setUsername("manager");
        registerRequest.setPassword("password123");

        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }
}
