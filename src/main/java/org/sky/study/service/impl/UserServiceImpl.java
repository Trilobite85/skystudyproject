package org.sky.study.service.impl;

import org.sky.study.model.jpa.User;
import org.sky.study.repository.jpa.UserRepository;
import org.sky.study.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting login for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with " + username + " not found"));
        // Convert User entity to Spring Security UserDetails
        return  org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles().split(","))
                .build();
    }

    @Override
    public void registerUser(User user) {
        logger.info("Registering user: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode password before saving
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with Id: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public User getUser(Long id) {
        logger.info("Retrieving user with Id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public boolean isUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}