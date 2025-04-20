package com.TimeLuxWatchBE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Cho phép credentials (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Chỉ cho phép origin cụ thể (không thể dùng * với allowCredentials=true)
        config.addAllowedOrigin("http://localhost:5173");
        
        // Cho phép tất cả các headers
        config.addAllowedHeader("*");
        
        // Cho phép tất cả các HTTP methods
        config.addAllowedMethod("*");
        
        // Thiết lập max age cho preflight requests (giảm số lượng OPTIONS requests)
        config.setMaxAge(3600L);
        
        // Expose các headers cần thiết (để frontend có thể đọc)
        config.setExposedHeaders(Arrays.asList(
            HttpHeaders.AUTHORIZATION, 
            HttpHeaders.CONTENT_TYPE,
            HttpHeaders.SET_COOKIE
        ));
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With", 
            "Accept", 
            "Origin", 
            "Access-Control-Request-Method", 
            "Access-Control-Request-Headers", 
            "Cookie"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Disposition", 
            "Set-Cookie"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 