package org.sky.study.service.impl;

import org.sky.study.model.jpa.User;
import org.sky.study.repository.jpa.UserRepository;
import org.sky.study.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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

    /**
     * Loads user details by username.
     *
     * @param username the username of the user
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if the user is not found
     */
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

    /**
     * Registers a new user by saving the user entity to the repository.
     *
     * @param user the User entity to be registered
     */
    @Override
    public void registerUser(User user) {
        logger.info("Registering user: {}", user.getUsername());
        //For simplicity, only users with USER role can be created
        user.setRoles("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode password before saving
        userRepository.save(user);
    }

    /**
     * Deletes a user by their ID, ensuring that the current user is authorized to perform this action.
     *
     * @param id the ID of the user to be deleted
     * @throws UsernameNotFoundException if the user with the given ID is not found
     * @throws AccessDeniedException if the current user is not authorized to delete the specified user
     */
    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with Id: " + id + " is not found"));
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        if (currentUsername.equals(user.getUsername()) || isAdmin) {
            logger.info("Deleting user with name: {}", user.getUsername());
            userRepository.deleteById(id);
        } else {
            logger.warn("Unauthorized delete attempt by user: {}", currentUsername);
            throw new AccessDeniedException("Not authorized to delete this user");
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to be retrieved
     * @return the User entity if found
     * @throws UsernameNotFoundException if the user with the given ID is not found
     */
    @Override
    public User getUser(Long id) {
        logger.info("Retrieving user with Id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with Id: " + id + " is not found"));
    }

    /**
     * Checks if a user with the given username already exists.
     *
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
    @Override
    public boolean isUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}