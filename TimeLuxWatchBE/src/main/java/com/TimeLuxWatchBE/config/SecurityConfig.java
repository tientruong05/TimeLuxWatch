package com.TimeLuxWatchBE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation(fixation -> fixation.migrateSession())
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    new AntPathRequestMatcher("/api/auth/**"),
                    new AntPathRequestMatcher("/api/forgotpass/**"),
                    new AntPathRequestMatcher("/api/home/**"),
                    new AntPathRequestMatcher("/api/products/detail/**"),
                    new AntPathRequestMatcher("/api/flash-sale/**"),
                    new AntPathRequestMatcher("/api/category/list-product"),
                    new AntPathRequestMatcher("/api/brands"),
                    new AntPathRequestMatcher("/photos/**"),
                    new AntPathRequestMatcher("/error")
                ).permitAll()
                
                .requestMatchers(new AntPathRequestMatcher("/api/account/**")).authenticated()
                
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            HttpHeaders.AUTHORIZATION, 
            HttpHeaders.CACHE_CONTROL, 
            HttpHeaders.CONTENT_TYPE, 
            HttpHeaders.ORIGIN, 
            HttpHeaders.ACCEPT, 
            "X-Requested-With", 
            "Access-Control-Request-Method", 
            "Access-Control-Request-Headers", 
            "Cookie",
            "Pragma"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            HttpHeaders.AUTHORIZATION, 
            HttpHeaders.CONTENT_DISPOSITION, 
            HttpHeaders.SET_COOKIE
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
         return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance(); 
    }
    
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
} 