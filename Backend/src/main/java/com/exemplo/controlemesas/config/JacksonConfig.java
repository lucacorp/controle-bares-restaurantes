package com.exemplo.controlemesas.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;

@Configuration
public class JacksonConfig {

    // Register Hibernate6 module only; let Spring Boot auto-configure the core ObjectMapper
    @Bean
    public Module hibernate6Module() {
        Hibernate6Module module = new Hibernate6Module();
        module.disable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        module.enable(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
        return module;
    }

    // Customize Jackson to write ISO-8601 dates (disable timestamp) without replacing ObjectMapper
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

}