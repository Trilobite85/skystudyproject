package org.sky.study.unit.service.impl;

import io.jsonwebtoken.Claims;
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
    void getClaims_nullToken_throwsException() {
        assertThrows(NullPointerException.class, () -> jwtService.getClaims(null));
    }

    @Test
    void getClaims_shortToken_throwsException() {
        // Token shorter than 7 chars (no "Bearer ")
        assertThrows(StringIndexOutOfBoundsException.class, () -> jwtService.getClaims("abc"));
    }

/*    @Test
    void addToBlacklist_and_isBlacklisted() {
        String token = "sometoken";
        assertFalse(jwtService.isBlacklisted(token));
        jwtService.addToBlacklist(token);
        assertTrue(jwtService.isBlacklisted(token));
    }*/

}