////package com.mit.rma_web_application.config;
////
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.security.authentication.AuthenticationManager;
////import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
////import org.springframework.security.config.http.SessionCreationPolicy;
////import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
////import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
////import org.springframework.security.crypto.password.PasswordEncoder;
////import org.springframework.security.web.SecurityFilterChain;
////import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
////import org.springframework.web.cors.CorsConfiguration;
////import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
////import org.springframework.web.cors.CorsConfigurationSource;
////import org.springframework.web.filter.CorsFilter;
////
////import java.util.List;
////
////@Configuration
////@EnableWebSecurity
////public class SecurityConfig {
////
////    private final JwtAuthenticationFilter jwtAuthenticationFilter;
////
////    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
////        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
////    }
////
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http
////                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
////                .csrf(AbstractHttpConfigurer::disable)
////                .authorizeHttpRequests(auth -> auth
////                        // Public Endpoints
////                        .requestMatchers(
////                                "/api/auth/login",
////                                "/api/auth/register",
////                                "/api/auth/users",
////                                "/api/auth/pending-users",
////                                "/api/auth/approve"
////                        ).permitAll()
////                        .requestMatchers("/api/vendors/**").permitAll()
////                        .requestMatchers("/api/requests/**").permitAll()
////                        .requestMatchers("/api/customers/**").permitAll()
////                        .requestMatchers("/api/inventory/**").permitAll()
////                        .requestMatchers("/ws/**").permitAll()
////
////                        // Role-secured Endpoints
////                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
////                        .requestMatchers("/api/engineer/**").hasRole("ENGINEER")
////                        .requestMatchers("/api/supplychain/**").hasRole("SUPPLYCHAIN")
////                        .requestMatchers("/api/rma/**").hasRole("RMA")
////
////                        // Catch-all: All others require authentication
////                        .anyRequest().authenticated()
////                )
////                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
////
////        return http.build();
////    }
////
////    @Bean
////    public CorsConfigurationSource corsConfigurationSource() {
////        CorsConfiguration config = new CorsConfiguration();
////        config.setAllowedOrigins(List.of(
////                "http://localhost:5173",
////                "http://localhost:3000",
////                "http://localhost:5174"
////        ));
////        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
////        config.setAllowedHeaders(List.of("*"));
////        config.setAllowCredentials(true);
////
////        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////        source.registerCorsConfiguration("/**", config);
////
////        return source;
////    }
////
////    @Bean
////    public CorsFilter corsFilter() {
////        return new CorsFilter(corsConfigurationSource());
////    }
////
////    @Bean
////    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
////        return authConfig.getAuthenticationManager();
////    }
////
////    @Bean
////    public PasswordEncoder passwordEncoder() {
////        return new BCryptPasswordEncoder();
////    }
////}
//
//package com.mit.rma_web_application.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    // Inject your JwtAuthenticationFilter through constructor
//    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        // Public endpoints accessible without authentication
//                        .requestMatchers(
//                                "/api/auth/login",
//                                "/api/auth/register",
//                                "/api/auth/users",
//                                "/api/auth/pending-users",
//                                "/api/auth/approve"
//                        ).permitAll()
//                        .requestMatchers("/api/vendors/**").permitAll()
//                        .requestMatchers("/api/requests/**").permitAll()
//                        .requestMatchers("/api/customers/**").permitAll()
//                        .requestMatchers("/api/inventory/**").permitAll()
//                        .requestMatchers("/ws/**").permitAll()
//
//                        // Role-protected endpoints
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/api/engineer/**").hasRole("ENGINEER")
//                        .requestMatchers("/api/supplychain/**").hasRole("SUPPLYCHAIN")
//                        .requestMatchers("/api/rma/**").hasRole("RMA")
//
//                        // Notifications require authentication
//                        .requestMatchers("/api/notifications/**").authenticated()
//
//                        // All other endpoints require authentication
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    // CORS configuration for frontend apps running on different ports
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of(
//                "http://localhost:5173",
//                "http://localhost:3000",
//                "http://localhost:5174"
//        ));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return source;
//    }
//
//    // Register CorsFilter bean
//    @Bean
//    public CorsFilter corsFilter() {
//        return new CorsFilter(corsConfigurationSource());
//    }
//
//    // Expose AuthenticationManager bean
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    // Password encoder bean (BCrypt)
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}

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
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Constructor Injection of JWT filter
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with custom configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Disable CSRF since JWT is used
                .csrf(AbstractHttpConfigurer::disable)
                // Configure route security
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (allow all)
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/users",
                                "/api/auth/pending-users",
                                "/api/auth/approve"
                        ).permitAll()
                        .requestMatchers("/api/vendors/**").permitAll()
                        .requestMatchers("/api/requests/**").permitAll()
                        .requestMatchers("/api/customers/**").permitAll()
                        .requestMatchers("/api/inventory/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()

                        // Role-based authorization
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/engineer/**").hasRole("ENGINEER")
                        .requestMatchers("/api/supplychain/**").hasRole("SUPPLYCHAIN")
                        .requestMatchers("/api/rma/**").hasRole("RMA")

                        // Authenticated access for notifications
                        .requestMatchers("/api/notifications/**").authenticated()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                // Stateless session management (no HTTP sessions)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Add JWT authentication filter before username/password filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS config to allow frontend origins
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:5174"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // CorsFilter bean (optional, but fine to keep)
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    // AuthenticationManager bean exposure (needed for login/auth service)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // PasswordEncoder bean for user password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
