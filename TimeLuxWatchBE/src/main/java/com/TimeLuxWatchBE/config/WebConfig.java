package com.TimeLuxWatchBE.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /photos/** URL pattern to the correct images directory in resources/static
        registry.addResourceHandler("/photos/**")
                .addResourceLocations("classpath:/static/photos/")
                .setCachePeriod(3600) // Cache for 1 hour (3600 seconds)
                .resourceChain(true); // Enable the resource chain for better performance
    }
} 