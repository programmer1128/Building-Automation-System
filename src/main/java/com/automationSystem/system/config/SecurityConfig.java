package com.automationSystem.system.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig 
{
     @Autowired
     private JwtAuthenticationFilter jwtAuthFilter;

     @Bean
     public PasswordEncoder passwordEncoder()
     {
         return new BCryptPasswordEncoder();
     }

     @Bean
     public ObjectMapper objectMapper() 
     {
         return new ObjectMapper();
     }

     @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception 
     {
         http
         .csrf(csrf -> csrf.disable())
         
         // Directly wire up the security-level CORS filter configuration source
         .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
         
         .authorizeHttpRequests(auth -> auth
             .requestMatchers("/api/admin/register/user", "/api/admin/signin/user").permitAll()
             .requestMatchers("/ws/**").permitAll() 
             .anyRequest().authenticated() 
         )
         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    
         return http.build();
     }
     
     // This intercepts and clears preflight requests before they hit JWT filter
     @Bean
     public CorsConfigurationSource corsConfigurationSource() 
     {
         CorsConfiguration configuration = new CorsConfiguration();
         configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://192.168.0.162:3000"));
         configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
         configuration.setAllowedHeaders(Arrays.asList("*")); // Allows all headers including Authorization
         configuration.setAllowCredentials(true);
         
         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
         source.registerCorsConfiguration("/**", configuration);
         return source;
     }
}
