package com.studentcompanion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.studentcompanion.model.User;
import com.studentcompanion.repository.UserRepository;

@CrossOrigin(
    origins = {
        "https://student-study-portal-frontend-e65xq7bo8.vercel.app",
        "https://student-study-portal-frontend-eyp1okzdf.vercel.app"
    },
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS},
    allowedHeaders = "*"
)
@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 🔓 Public or Admin: Get all users
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 🔐 Get profile of currently authenticated user
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

}
