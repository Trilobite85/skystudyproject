package org.sky.study.controller;

import org.sky.study.model.jpa.User;
import org.sky.study.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userService.isUserExists(user.getUsername())) {
            return ResponseEntity.badRequest().body("User already exists!");
        }
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully!");
    }
}