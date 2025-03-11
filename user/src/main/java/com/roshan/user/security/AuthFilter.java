package com.roshan.user.security;

import com.roshan.user.dto.UserDTO;
import com.roshan.user.security.JwtUtil;
import com.roshan.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
public class AuthFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        logger.debug("Received request with Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No valid Authorization header found, proceeding without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        logger.debug("Extracted token: {}", token);

        String username = jwtUtil.validateToken(token);
        logger.debug("Token validation result - username: {}", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Token is valid, setting up authentication for user: {}", username);
            UserDetails userDetails = userService.loadUserByUsername(username);
            
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.debug("Authentication set in SecurityContext");
        } else {
            logger.debug("Token validation failed or authentication already exists");
        }

        filterChain.doFilter(request, response);
    }
}
