package com.TimeLuxWatchBE.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Register the Hibernate module
        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
        // Optional: Configure features like forcing lazy loading or handling transient properties
        // hibernateModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, true);
        objectMapper.registerModule(hibernateModule);

        // Register the Java Time module for LocalDateTime etc.
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper.registerModule(javaTimeModule);

        // Optional: Configure Java Time module to write dates as ISO-8601 strings
        // instead of numeric timestamps. This is generally preferred for APIs.
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }

    // If you already have other Jackson customizations (like for dates),
    // you might need to integrate this module registration into your existing ObjectMapper bean.
    // Alternatively, you can provide a Jackson2ObjectMapperBuilderCustomizer bean:
    /*
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.modules(new Hibernate5JakartaModule(), new JavaTimeModule())
                                 .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    */
} 