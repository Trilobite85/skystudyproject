package org.sky.study.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtServiceImpl {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";

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

    /**
     * Add JWT token to Redis blacklist.
     * @param token - JWT token to blacklist
     */
    public void addToBlacklist(String token) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "true", VALIDITY);
    }

    /**
     * Check if a JWT token is blacklisted.
     * @param token - JWT token to check
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return redisTemplate.hasKey(key);
    }
}
