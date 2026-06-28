package com.flashlearn.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flashlearn.app.model.dto.CreateFlashcardRequest;
import com.flashlearn.app.model.dto.CreateFlashcardSetRequest;
import com.flashlearn.app.model.dto.LearningResultRequest;
import com.flashlearn.app.model.dto.RegisterRequest;
import com.flashlearn.app.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LearningIntegrationTest {

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

    @BeforeEach
    void cleanDatabase() {
        learningResultRepository.deleteAll();
        learningSessionRepository.deleteAll();
        flashcardRepository.deleteAll();
        flashcardSetRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void learningSessionFlowWorks() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("learner@test.com");
        registerRequest.setUsername("learner");
        registerRequest.setPassword("password123");

        String authResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(authResponse).get("token").asText();
        String userId = objectMapper.readTree(authResponse).get("user").get("id").asText();

        CreateFlashcardSetRequest setRequest = new CreateFlashcardSetRequest();
        setRequest.setTitle("Test Set");
        setRequest.setDescription("Desc");
        setRequest.setCategory("Test");

        String setResponse = mockMvc.perform(post("/api/sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(setRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String setId = objectMapper.readTree(setResponse).get("id").asText();

        CreateFlashcardRequest card1 = new CreateFlashcardRequest();
        card1.setQuestion("Q1");
        card1.setAnswer("A1");
        CreateFlashcardRequest card2 = new CreateFlashcardRequest();
        card2.setQuestion("Q2");
        card2.setAnswer("A2");

        mockMvc.perform(post("/api/sets/" + setId + "/cards")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(card1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/sets/" + setId + "/cards")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(card2)))
                .andExpect(status().isCreated());

        String sessionResponse = mockMvc.perform(post("/api/learning/start/" + setId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.flashcards.length()").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode sessionJson = objectMapper.readTree(sessionResponse);
        String sessionId = sessionJson.get("sessionId").asText();
        String card1Id = sessionJson.get("flashcards").get(0).get("id").asText();
        String card2Id = sessionJson.get("flashcards").get(1).get("id").asText();

        LearningResultRequest result1 = new LearningResultRequest();
        result1.setSessionId(sessionId);
        result1.setFlashcardId(card1Id);
        result1.setKnown(true);

        mockMvc.perform(post("/api/learning/result")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(result1)))
                .andExpect(status().isCreated());

        LearningResultRequest result2 = new LearningResultRequest();
        result2.setSessionId(sessionId);
        result2.setFlashcardId(card2Id);
        result2.setKnown(false);

        mockMvc.perform(post("/api/learning/result")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(result2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/statistics/user/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCardsStudied").value(2))
                .andExpect(jsonPath("$.correctAnswers").value(1))
                .andExpect(jsonPath("$.incorrectAnswers").value(1))
                .andExpect(jsonPath("$.successPercentage").value(50));
    }

    @Test
    void publicSetsAreAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/sets"))
                .andExpect(status().isOk());
    }
}
