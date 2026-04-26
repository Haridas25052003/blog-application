package BlogApplication.BlogApplication.controller;

import BlogApplication.BlogApplication.dto.ApiResponse;
import BlogApplication.BlogApplication.dto.LoginRequest;
import BlogApplication.BlogApplication.dto.RegisterRequest;
import BlogApplication.BlogApplication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // ==================== REGISTER ====================
    // POST http://localhost:8080/api/auth/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        ApiResponse response = userService.register(request);

        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    // ==================== LOGIN ====================
    // POST http://localhost:8080/api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @Valid @RequestBody LoginRequest request) {

        ApiResponse response = userService.login(request);

        if (!response.isSuccess()) {
            return ResponseEntity.status(401).body(response);
        }

        return ResponseEntity.ok(response);
    }
}