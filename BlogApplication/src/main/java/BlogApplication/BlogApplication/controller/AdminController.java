package BlogApplication.BlogApplication.controller;

import BlogApplication.BlogApplication.dto.ApiResponse;
import BlogApplication.BlogApplication.service.BlogService;
import BlogApplication.BlogApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BlogService blogService;

    // ==================== FETCH ALL USERS (ADMIN) ====================
    // GET http://localhost:8080/api/admin/users
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestHeader("X-Admin-Id") int adminId) {
        // Verify admin first
        ApiResponse verify = verifyAdmin(adminId);
        if (verify != null) return ResponseEntity.status(403).body(verify);
        ApiResponse response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    // ==================== DELETE USER (ADMIN) ====================
    // DELETE http://localhost:8080/api/admin/user/delete/3
    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<ApiResponse> deleteUser(
            @PathVariable int id,
            @RequestHeader("X-Admin-Id") int adminId) {

        ApiResponse response = userService.deleteUser(adminId, id);
        if (!response.isSuccess()) {
            return ResponseEntity.status(403).body(response);
        }
        return ResponseEntity.ok(response);
    }

    // ==================== DELETE BLOG (ADMIN) ====================
    // DELETE http://localhost:8080/api/admin/blog/delete/5
    @DeleteMapping("/blog/delete/{id}")
    public ResponseEntity<ApiResponse> deleteBlog(
            @PathVariable int id,
            @RequestHeader("X-Admin-Id") int adminId) {

        ApiResponse response = blogService.adminDeleteBlog(adminId, id);
        if (!response.isSuccess()) {
            return ResponseEntity.status(403).body(response);
        }
        return ResponseEntity.ok(response);
    }

    // ==================== HELPER — VERIFY ADMIN ====================
    private ApiResponse verifyAdmin(int adminId) {
        ApiResponse userResponse = userService.getUserById(adminId);

        if (!userResponse.isSuccess()) {
            return ApiResponse.failure("Admin not found");
        }
        // Check role from data object
        BlogApplication.BlogApplication.model.User admin =
                (BlogApplication.BlogApplication.model.User) userResponse.getData();

        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return ApiResponse.failure("Access Denied: Must be a verified Administrator");
        }
        return null; // null means verified
    }
}