package com.mit.rma_web_application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with config below
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Disable CSRF for APIs
                .csrf(AbstractHttpConfigurer::disable)
                // Authorize requests
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints for auth and open APIs
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/users",
                                "/api/auth/pending-users",
                                "/api/auth/approve"
                        ).permitAll()
                        .requestMatchers("/api/vendors/**").permitAll()
                        .requestMatchers("/api/customers/**").permitAll()
                        .requestMatchers("/api/inventory/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()

                        // Role-based restrictions
                        .requestMatchers("/api/requests/**").hasAnyRole("ADMIN", "SUPPLYCHAIN", "ENGINEER", "RMA")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/engineer/**").hasRole("ENGINEER")
                        .requestMatchers("/api/supplychain/**").hasRole("SUPPLYCHAIN")
                        .requestMatchers("/api/rma/**").hasRole("RMA")

                        // Notifications requires authenticated user
                        .requestMatchers("/api/notifications/**").authenticated()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                // Stateless session: no session maintained
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Add JWT filter before Spring Security’s username/password filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000", "http://localhost:5174"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Expose AuthenticationManager bean to use in your services if needed
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
