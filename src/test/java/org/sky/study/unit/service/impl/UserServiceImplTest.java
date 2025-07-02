package org.sky.study.unit.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sky.study.model.jpa.User;
import org.sky.study.repository.jpa.UserRepository;
import org.sky.study.service.impl.UserServiceImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void loadUserByUsername_success() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("pass");
        user.setRoles("USER");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        var userDetails = userService.loadUserByUsername("john");

        assertEquals("john", userDetails.getUsername());
        assertEquals("pass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_notFound() {
        when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("notfound"));
    }

    @Test
    void registerUser_success() {
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("plain");
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.registerUser(user);

        assertEquals("USER", user.getRoles());
        assertEquals("encoded", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUserById_selfDelete() {
        User user = new User();
        user.setId(1L);
        user.setUsername("self");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        mockAuthentication("self", "ROLE_USER");

        userService.deleteUserById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserById_adminDeletesOther() {
        User user = new User();
        user.setId(2L);
        user.setUsername("other");
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        mockAuthentication("admin", "ROLE_ADMIN");

        userService.deleteUserById(2L);

        verify(userRepository).deleteById(2L);
    }

    @Test
    void deleteUserById_unauthorized() {
        User user = new User();
        user.setId(3L);
        user.setUsername("victim");
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        mockAuthentication("attacker", "ROLE_USER");

        assertThrows(AccessDeniedException.class, () -> userService.deleteUserById(3L));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deleteUserById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        mockAuthentication("any", "ROLE_USER");
        assertThrows(UsernameNotFoundException.class, () -> userService.deleteUserById(99L));
    }

    @Test
    void getUser_success() {
        User user = new User();
        user.setId(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        assertEquals(user, userService.getUser(5L));
    }

    @Test
    void getUser_notFound() {
        when(userRepository.findById(6L)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getUser(6L));
    }

    @Test
    void isUserExists_true() {
        when(userRepository.findByUsername("exists")).thenReturn(Optional.of(new User()));
        assertTrue(userService.isUserExists("exists"));
    }

    // Helper to mock authentication context
    private void mockAuthentication(String username, String role) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(role)
        );
        when(auth.getAuthorities()).thenReturn((Collection)authorities);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }
}