package BlogApplication.BlogApplication.service;

import BlogApplication.BlogApplication.dto.ApiResponse;
import BlogApplication.BlogApplication.dto.LoginRequest;
import BlogApplication.BlogApplication.dto.LoginResponse;
import BlogApplication.BlogApplication.dto.RegisterRequest;
import BlogApplication.BlogApplication.model.User;
import BlogApplication.BlogApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ==================== REGISTER ====================
    public ApiResponse register(RegisterRequest request) {

        // 1. Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.failure("Email is already registered");
        }

        // 2. Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.failure("Username is already taken");
        }

        // 3. Build User object
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 🔒 hashed
        user.setRole("USER"); // always USER on registration

        // 4. Save to DB
        userRepository.save(user);

        return ApiResponse.success("User registered successfully");
    }

    // ==================== LOGIN ====================
    public ApiResponse login(LoginRequest request) {

        // 1. Find user by email
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        // 2. Check if user exists
        if (optionalUser.isEmpty()) {
            return ApiResponse.failure("Invalid email or password");
        }

        User user = optionalUser.get();

        // 3. Compare hashed password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.failure("Invalid email or password");
        }

        // 4. Build safe response — NO password sent to frontend
        LoginResponse loginResponse = new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );

        return ApiResponse.success("Login successful", loginResponse);
    }

    // ==================== GET ALL USERS (ADMIN) ====================
    public ApiResponse getAllUsers() {
        List<User> users = userRepository.findAll();
        return ApiResponse.success("Users fetched successfully", users);
    }

    // ==================== GET USER BY ID ====================
    public ApiResponse getUserById(int id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return ApiResponse.failure("User not found");
        }

        return ApiResponse.success("User fetched successfully", user.get());
    }

    // ==================== DELETE USER (ADMIN) ====================
    @Transactional
    public ApiResponse deleteUser(int adminId, int userIdToDelete) {

        // 1. Verify admin
        Optional<User> adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty() || !"ADMIN".equalsIgnoreCase(adminOpt.get().getRole())) {
            return ApiResponse.failure("Access Denied: Must be a verified Administrator");
        }

        // 2. Prevent admin from deleting themselves
        if (adminId == userIdToDelete) {
            return ApiResponse.failure("Admin cannot delete their own account");
        }

        // 3. Check if user to delete exists
        if (!userRepository.existsById(userIdToDelete)) {
            return ApiResponse.failure("User not found");
        }

        // 4. Delete user (blogs deleted automatically via BlogRepository)
        userRepository.deleteById(userIdToDelete);

        return ApiResponse.success("User deleted successfully");
    }
}