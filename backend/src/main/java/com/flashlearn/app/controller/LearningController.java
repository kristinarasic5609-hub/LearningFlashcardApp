package com.flashlearn.app.controller;

import com.flashlearn.app.model.dto.AuthUserDto;
import com.flashlearn.app.model.dto.LearningResultRequest;
import com.flashlearn.app.model.dto.LearningSessionStartResponse;
import com.flashlearn.app.model.dto.UserStatisticsResponse;
import com.flashlearn.app.model.entity.LearningResult;
import com.flashlearn.app.security.SecurityUtils;
import com.flashlearn.app.service.LearningService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LearningController {

    private final LearningService learningService;

    public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    @PostMapping("/learning/start/{setId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LearningSessionStartResponse startSession(@PathVariable String setId) {
        AuthUserDto user = SecurityUtils.requireCurrentUser();
        return learningService.startSession(user.id(), setId);
    }

    @PostMapping("/learning/result")
    @ResponseStatus(HttpStatus.CREATED)
    public LearningResult recordResult(@Valid @RequestBody LearningResultRequest request) {
        AuthUserDto user = SecurityUtils.requireCurrentUser();
        return learningService.recordResult(user, request);
    }

    @GetMapping("/statistics/user/{id}")
    public UserStatisticsResponse getUserStatistics(@PathVariable String id) {
        AuthUserDto user = SecurityUtils.requireCurrentUser();
        return learningService.getUserStatistics(id, user);
    }
}
