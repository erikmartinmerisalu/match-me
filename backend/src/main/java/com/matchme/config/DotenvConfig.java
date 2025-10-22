package com.matchme.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotenvConfig {

    @Bean
    public Dotenv dotenv() {
        System.out.println("🔧 Loading .env file...");
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();
        
        // Print loaded values for debugging
        dotenv.entries().forEach(entry -> {});
        
        return dotenv;
    }

    @Bean
    public Boolean initializeDotenvProperties(Dotenv dotenv, ConfigurableEnvironment environment) {
        Map<String, Object> dotenvProperties = new HashMap<>();
        
        dotenv.entries().forEach(entry -> {
            dotenvProperties.put(entry.getKey(), entry.getValue());
        });
        
        // Add dotenv properties as the highest priority property source
        environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", dotenvProperties));
        
        System.out.println("✅ .env properties added to Spring Environment");
        return true;
    }
}