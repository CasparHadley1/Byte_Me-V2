package com.byteme.app;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;


import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Configuration
@EnableWebSecurity
public class SecurityConfig {


   private final JwtUtil jwtUtil;


   public SecurityConfig(JwtUtil jwtUtil) {
       this.jwtUtil = jwtUtil;
   }


   @Bean
   public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }


   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
           .cors(cors -> cors.configurationSource(corsConfigurationSource()))
           .csrf(csrf -> csrf.disable())
           .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
           .authorizeHttpRequests(auth -> auth
               .requestMatchers("/api/auth/**").permitAll()
               .requestMatchers("/api/bundles").permitAll()
               .requestMatchers("/api/bundles/{id}").permitAll()
               .requestMatchers("/api/categories").permitAll()
               .anyRequest().authenticated()
           )
           .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);


       return http.build();
   }


   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
       CorsConfiguration config = new CorsConfiguration();
       config.setAllowedOrigins(List.of("http://localhost:3000"));
       config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
       config.setAllowedHeaders(List.of("*"));
       config.setAllowCredentials(true);


       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**", config);
       return source;
   }


   @Bean
   public OncePerRequestFilter jwtAuthFilter() {
       return new OncePerRequestFilter() {
           @Override
           protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                           FilterChain filterChain) throws ServletException, IOException {
               String header = request.getHeader("Authorization");


               if (header != null && header.startsWith("Bearer ")) {
                   String token = header.substring(7);
                   try {
                       UUID userId = jwtUtil.getUserId(token);
                       var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                               userId, null, java.util.Collections.emptyList());
                       org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
                   } catch (Exception e) {
                       // Invalid token - continue without auth
                   }
               }


               filterChain.doFilter(request, response);
           }
       };
   }
}