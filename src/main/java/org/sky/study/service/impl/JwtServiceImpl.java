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
    private final Set<String> blacklist = new HashSet<>();

    private static final Long VALIDITY = TimeUnit.MINUTES.toMillis(1);

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt( Date.from(Instant.now()) )
                .claim("roles", userDetails.getAuthorities().toString())
                .setExpiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.substring(7)).getBody();
    }

    public Claims getClaims(String token) {
        return parseToken(token);
    }

    public void addToBlacklist(String token) {
        blacklist.add(token); // For production, use a database or cache like Redis
    }

    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
