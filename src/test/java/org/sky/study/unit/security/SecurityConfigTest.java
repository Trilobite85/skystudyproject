package org.sky.study.unit.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.sky.study.security.JwtAuthenticationFilter;
import org.sky.study.security.SecurityConfig;
import org.sky.study.service.impl.JwtServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void passwordEncoder_returnsBCryptPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder.matches("pw", encoder.encode("pw")));
    }

    @Test
    void jwtAuthenticationFilter_returnsFilter() {
        JwtAuthenticationFilter filter = securityConfig.jwtAuthenticationFilter();
        assertNotNull(filter);
    }

    @Test
    void authenticationManager_returnsManager() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);
        assertEquals(authenticationManager, result);
    }

}