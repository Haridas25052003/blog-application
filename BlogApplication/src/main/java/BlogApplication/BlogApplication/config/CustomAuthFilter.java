package BlogApplication.BlogApplication.config;

import BlogApplication.BlogApplication.model.User;
import BlogApplication.BlogApplication.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Try to read X-User-Id or X-Admin-Id header
        String userIdHeader  = request.getHeader("X-User-Id");
        String adminIdHeader = request.getHeader("X-Admin-Id");

        String header = adminIdHeader != null ? adminIdHeader : userIdHeader;

        if (header != null) {
            try {
                int userId = Integer.parseInt(header);

                // 2. Fetch user from DB
                Optional<User> userOpt = userRepository.findById(userId);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    // 3. Build role — Spring Security needs "ROLE_" prefix
                    String role = "ROLE_" + user.getRole().toUpperCase(); // ROLE_USER or ROLE_ADMIN

                    // 4. Create authentication token
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user.getEmail(),        // principal
                                    null,                   // credentials (not needed)
                                    List.of(new SimpleGrantedAuthority(role)) // roles
                            );

                    // 5. Set in SecurityContext — Spring Security now knows who this is
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (NumberFormatException e) {
                // Invalid header value — just skip, SecurityContext stays empty
            }
        }

        // 6. Continue the filter chain regardless
        filterChain.doFilter(request, response);
    }
}