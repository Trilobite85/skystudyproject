package org.sky.study.controller;

import org.sky.study.service.impl.JwtServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.sky.study.dto.LoginRequest;
import org.sky.study.dto.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, JwtServiceImpl jwtServiceImpl) {
        this.authenticationManager = authenticationManager;
        this.jwtServiceImpl = jwtServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Attempting login for user: {}", loginRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                logger.info("Authentication successful for user: {}", loginRequest.getUsername());
            } else {
                logger.warn("Authentication failed for user: {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            String token = jwtServiceImpl.generateToken((UserDetails) authentication.getPrincipal());
            logger.info("Login successful for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException e) {
            logger.error("Login failed for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
