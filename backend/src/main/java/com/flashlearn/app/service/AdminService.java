package com.flashlearn.app.service;

import com.flashlearn.app.exception.AppException;
import com.flashlearn.app.model.entity.User;
import com.flashlearn.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> listUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public void deleteUser(String id, String requesterId) {
        if (id.equals(requesterId)) {
            throw new AppException(400, "Administrators cannot delete their own account");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "User not found"));

        userRepository.delete(user);
    }
}
