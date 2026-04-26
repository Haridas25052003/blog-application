package BlogApplication.BlogApplication.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthFilter customAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                //  Disable CSRF
                .csrf(AbstractHttpConfigurer::disable)

                //  CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                //  Endpoint protection rules
                .authorizeHttpRequests(auth -> auth

                        // --- PUBLIC --- no auth needed
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/blogs",
                                "/api/blogs/{id}"
                        ).permitAll()

                        // --- ADMIN only ---
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // --- Authenticated users only ---
                        .anyRequest().authenticated()
                )

                //  Stateless — no sessions
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                //  Disable default login
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                //  Register our custom filter BEFORE Spring's default auth filter
                .addFilterBefore(customAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ==================== CORS ====================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5500",
                "http://127.0.0.1:5500",
                "http://localhost:8080"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(Arrays.asList(
                "Content-Type",
                "X-User-Id",
                "X-Admin-Id"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}