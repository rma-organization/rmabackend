package com.mit.rma_web_application.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * This method determines whether the filter should NOT be applied to the request.
     * It allows unauthenticated access to login and registration endpoints.
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Allow login & register endpoints to bypass JWT authentication
        return path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register");
    }

    /**
     * This method is called for every HTTP request that passes through the filter chain.
     * It checks for a JWT in the Authorization header and sets the security context if valid.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        // Extract Authorization header (expected format: "Bearer <token>")
        String authorizationHeader = request.getHeader("Authorization");

        // If header is missing or doesn't start with "Bearer ", skip JWT processing
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response); // Continue to next filter
            return;
        }

        // Remove "Bearer " prefix to get the actual token
        String token = authorizationHeader.substring(7);

        try {
            // Extract username from token
            String username = jwtUtil.extractUsername(token);

            // Check if username is valid and no authentication is currently set in context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate the token against the username
                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    // Create authentication token with user details and authorities (roles)
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // Add request details (IP, session, etc.) to the authentication object
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in the security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        } catch (JwtException ex) {
            // If token is invalid or expired, return 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + ex.getMessage());
            return;
        }

        // Continue to the next filter or controller
        chain.doFilter(request, response);
    }
}
