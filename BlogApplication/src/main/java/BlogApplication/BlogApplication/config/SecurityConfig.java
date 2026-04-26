package BlogApplication.BlogApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            //  Disable CSRF — not needed for REST APIs
            .csrf(AbstractHttpConfigurer::disable)

            //  Apply CORS config defined below
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            //  Define which endpoints are public and which are protected
            .authorizeHttpRequests(auth -> auth

                // --- PUBLIC endpoints --- anyone can access
                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/api/blogs",
                    "/api/blogs/{id}"
                ).permitAll()

                // --- ADMIN only endpoints ---
                .requestMatchers(
                    "/api/admin/**"
                ).hasRole("ADMIN")

                // --- Everything else needs authentication ---
                .anyRequest().authenticated()
            )

            // Stateless — no sessions, each request is independent
            // This is correct for REST APIs
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            //  Disable default Spring login page
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // ==================== CORS CONFIG ====================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        //  Allow your frontend origins
        // Add your frontend URL here when you deploy
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",    // React dev server
            "http://localhost:5500",    // Live Server (VS Code)
            "http://127.0.0.1:5500",   // Live Server alternate
            "http://localhost:8080"     // Same server
        ));

        //  Allow these HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        //  Allow these headers from frontend
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-User-Id",        // Your existing header for user auth
            "X-Admin-Id"        // Your existing header for admin auth
        ));

        //  Allow credentials (cookies, auth headers)
        configuration.setAllowCredentials(true);

        //  Apply this CORS config to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}