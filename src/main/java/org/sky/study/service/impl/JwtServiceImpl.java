package org.sky.study.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class JwtServiceImpl {

    @Value("${jwt.secret-key}")
    private String secretKey;

    /**
     * A simple in-memory blacklist for JWT tokens.
     * In production, consider using a more persistent store like Redis.
     */
    private final Set<String> blacklist = new HashSet<>();

    private static final Long VALIDITY = TimeUnit.MINUTES.toMillis(30);

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails the user details for which the token is generated
     * @return a JWT token as a String
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt( Date.from(Instant.now()) )
                .claim("roles", userDetails.getAuthorities().toString())
                .setExpiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.substring(7)).getBody();
    }

    public void addToBlacklist(String token) {
        blacklist.add(token); // For production, use a database or cache like Redis
    }

    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
