package BlogApplication.BlogApplication.controller;

import BlogApplication.BlogApplication.dto.ApiResponse;
import BlogApplication.BlogApplication.model.Blog;
import BlogApplication.BlogApplication.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    // ==================== FETCH ALL BLOGS (PUBLIC) ====================
    // GET http://localhost:8080/api/blogs
    @GetMapping
    public ResponseEntity<ApiResponse> getAllBlogs() {
        ApiResponse response = blogService.fetchAllBlogs();
        return ResponseEntity.ok(response);
    }

    // ==================== FETCH BLOG BY ID (PUBLIC) ====================
    // GET http://localhost:8080/api/blogs/5
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getBlogById(
            @PathVariable int id) {

        ApiResponse response = blogService.fetchBlogById(id);

        if (!response.isSuccess()) {
            return ResponseEntity.status(404).body(response);
        }

        return ResponseEntity.ok(response);
    }

    // ==================== FETCH BLOGS BY USER ====================
    // GET http://localhost:8080/api/blogs/user/3
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getBlogsByUser(
            @PathVariable int userId) {

        ApiResponse response = blogService.fetchBlogsByUser(userId);

        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    // ==================== ADD BLOG ====================
    // POST http://localhost:8080/api/blogs/add
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addBlog(
            @RequestBody Blog blog) {

        ApiResponse response = blogService.addBlog(blog);

        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    // ==================== UPDATE BLOG ====================
    // PUT http://localhost:8080/api/blogs/update
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateBlog(
            @RequestBody Blog blog,
            @RequestHeader("X-User-Id") int userId) {

        ApiResponse response = blogService.updateBlog(blog, userId);

        if (!response.isSuccess()) {
            return ResponseEntity.status(403).body(response);
        }

        return ResponseEntity.ok(response);
    }

    // ==================== DELETE BLOG ====================
    // DELETE http://localhost:8080/api/blogs/delete/5
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteBlog(
            @PathVariable int id,
            @RequestHeader("X-User-Id") int userId) {

        ApiResponse response = blogService.deleteBlog(id, userId);

        if (!response.isSuccess()) {
            return ResponseEntity.status(403).body(response);
        }

        return ResponseEntity.ok(response);
    }
}