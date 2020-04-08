package com.bolsaparatodos.bolsaparatodos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfiguration implements WebMvcConfigurer {

    @Autowired
    private Environment env;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String uiCorsServer = env.getProperty("security-cors-origins");
        if (uiCorsServer != null && !uiCorsServer.equals("")) {
            registry.addMapping("/**").allowedOrigins(uiCorsServer).allowCredentials(true);
        }
    }
}