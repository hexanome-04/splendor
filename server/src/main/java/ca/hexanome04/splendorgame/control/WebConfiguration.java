package ca.hexanome04.splendorgame.control;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Set up configuration for the server-client interactions.
 */
@Configuration
public class WebConfiguration {

    /**
     * Set CORS registry configuration.
     */
    @Bean
    public WebMvcConfigurer corsConfig() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:8000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }

}
