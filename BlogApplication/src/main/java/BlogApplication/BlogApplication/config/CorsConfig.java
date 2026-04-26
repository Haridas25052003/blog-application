package BlogApplication.BlogApplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // Applies to all /api/ endpoints
                .allowedOrigins("*")     // Allows all origins (for development)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")     // Allows all headers including X-User-Id, X-Admin-Id
                .exposedHeaders("*")     // Exposes all headers to frontend
                .allowCredentials(false) // Set to false since we're using "*" origin
                .maxAge(3600);           // Cache preflight response for 1 hour
    }
}