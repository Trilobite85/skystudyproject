package org.sky.study.unit.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sky.study.service.impl.JwtServiceImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        String secretKey = "testSecretKey1234567890";
        Field field = JwtServiceImpl.class.getDeclaredField("secretKey");
        field.setAccessible(true);
        field.set(jwtService, secretKey);
    }

    @Test
    void generateToken_and_getClaims_success() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        when(userDetails.getAuthorities()).thenReturn(
                (Collection) Set.of(adminAuthority)
        );
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        Claims claims = jwtService.getClaims("Bearer " + token);
        Object roles = claims.get("roles");

        assertEquals("user1", claims.getSubject());
        assertTrue(roles.toString().contains("ROLE_ADMIN"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void getClaims_invalidToken_throwsException() {
        String invalidToken = "Bearer invalid.token.value";
        assertThrows(Exception.class, () -> jwtService.getClaims(invalidToken));
    }

    @Test
    void getClaims_expiredToken_throwsException() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MjE0MTM0Mywicm9sZXMiOiJbUk9MRV9BRE1JTl0iLCJleHAiOjE3NTIxNDMxNDN9.4XCnO4CmT1BpX9JhjXi9gBbShnjL8-ijf8CNP3GTZtM";
        assertThrows(MalformedJwtException.class, () -> jwtService.getClaims(invalidToken));
    }

    @Test
    void getClaims_nullToken_throwsException() {
        assertThrows(NullPointerException.class, () -> jwtService.getClaims(null));
    }

    @Test
    void getClaims_shortToken_throwsException() {
        // Token shorter than 7 chars (no "Bearer ")
        assertThrows(StringIndexOutOfBoundsException.class, () -> jwtService.getClaims("abc"));
    }

}