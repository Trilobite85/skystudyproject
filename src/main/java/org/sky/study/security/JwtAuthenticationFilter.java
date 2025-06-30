package org.sky.study.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.sky.study.service.impl.JwtServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter checks for the presence of a JWT in the Authorization header,
 * validates it, and sets the authentication in the SecurityContext if valid.
 */

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    JwtServiceImpl jwtService;

    public JwtAuthenticationFilter(JwtServiceImpl jwtService) {
        this.jwtService = jwtService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.getClaims(authHeader);
                String username = claims.getSubject();
                String roles = claims.get("roles", String.class);
                // Validate roles and check if the token is expired
                if ((roles.contains("USER") || roles.contains("ADMIN")) && !jwtService.isTokenExpired(authHeader)) {
                    Authentication auth = new JwtAuthenticationToken(username, roles);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (ExpiredJwtException e) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token has expired");
                return;
            } catch (SignatureException | MalformedJwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}