package com.flashlearn.app.controller;

import com.flashlearn.app.model.dto.AuthUserDto;
import com.flashlearn.app.model.entity.FlashcardSet;
import com.flashlearn.app.model.entity.User;
import com.flashlearn.app.security.SecurityUtils;
import com.flashlearn.app.service.AdminService;
import com.flashlearn.app.service.FlashcardSetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final FlashcardSetService flashcardSetService;

    public AdminController(AdminService adminService, FlashcardSetService flashcardSetService) {
        this.adminService = adminService;
        this.flashcardSetService = flashcardSetService;
    }

    @GetMapping("/users")
    public List<User> listUsers() {
        return adminService.listUsers();
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id) {
        AuthUserDto user = SecurityUtils.requireCurrentUser();
        adminService.deleteUser(id, user.id());
    }

    @GetMapping("/sets")
    public List<FlashcardSet> listAllSets() {
        return flashcardSetService.listAll();
    }

    @DeleteMapping("/sets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDeleteSet(@PathVariable String id) {
        flashcardSetService.adminDelete(id);
    }
}
