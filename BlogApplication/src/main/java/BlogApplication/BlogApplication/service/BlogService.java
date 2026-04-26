package BlogApplication.BlogApplication.service;

import BlogApplication.BlogApplication.dto.ApiResponse;
import BlogApplication.BlogApplication.model.Blog;
import BlogApplication.BlogApplication.model.User;
import BlogApplication.BlogApplication.repository.BlogRepository;
import BlogApplication.BlogApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    // ==================== ADD BLOG ====================
    public ApiResponse addBlog(Blog blog) {

        // 1. Validate image
        if (blog.getImageUrl() == null || blog.getImageUrl().isEmpty()) {
            return ApiResponse.failure("Image URL is required");
        }

        // 2. Verify user exists
        if (!userRepository.existsById(blog.getUserId())) {
            return ApiResponse.failure("Invalid user");
        }

        // 3. Save blog
        Blog savedBlog = blogRepository.save(blog);

        return ApiResponse.success("Blog added successfully", savedBlog);
    }

    // ==================== FETCH ALL BLOGS ====================
    public ApiResponse fetchAllBlogs() {
        List<Blog> blogs = blogRepository.findAllByOrderByCreatedAtDesc();
        return ApiResponse.success("Blogs fetched successfully", blogs);
    }

    // ==================== FETCH BLOG BY ID ====================
    public ApiResponse fetchBlogById(int id) {
        Optional<Blog> blog = blogRepository.findById(id);

        if (blog.isEmpty()) {
            return ApiResponse.failure("Blog not found");
        }

        return ApiResponse.success("Blog fetched successfully", blog.get());
    }

    // ==================== FETCH BLOGS BY USER ====================
    public ApiResponse fetchBlogsByUser(int userId) {

        // 1. Verify user exists
        if (!userRepository.existsById(userId)) {
            return ApiResponse.failure("User not found");
        }

        // 2. Fetch blogs
        List<Blog> blogs = blogRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return ApiResponse.success("User blogs fetched successfully", blogs);
    }

    // ==================== UPDATE BLOG ====================
    public ApiResponse updateBlog(Blog updatedBlog, int requestingUserId) {

        // 1. Fetch existing blog
        Optional<Blog> existingBlogOpt = blogRepository.findById(updatedBlog.getId());
        if (existingBlogOpt.isEmpty()) {
            return ApiResponse.failure("Blog not found");
        }

        Blog existingBlog = existingBlogOpt.get();

        // 2. Fetch requesting user
        Optional<User> userOpt = userRepository.findById(requestingUserId);
        if (userOpt.isEmpty()) {
            return ApiResponse.failure("User session expired or invalid");
        }

        User currentUser = userOpt.get();

        // 3. Check if Admin OR Owner
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        boolean isOwner = existingBlog.getUserId() == requestingUserId;

        if (!isAdmin && !isOwner) {
            return ApiResponse.failure("Access Denied: Only Admin or Owner can update");
        }

        // 4. Update only the fields that should change
        existingBlog.setTitle(updatedBlog.getTitle());
        existingBlog.setContent(updatedBlog.getContent());
        existingBlog.setImageUrl(updatedBlog.getImageUrl());

        // 5. Save updated blog
        Blog savedBlog = blogRepository.save(existingBlog);

        return ApiResponse.success("Blog updated successfully", savedBlog);
    }

    // ==================== DELETE BLOG (USER/ADMIN) ====================
    public ApiResponse deleteBlog(int blogId, int requestingUserId) {

        // 1. Fetch blog
        Optional<Blog> blogOpt = blogRepository.findById(blogId);
        if (blogOpt.isEmpty()) {
            return ApiResponse.failure("Blog not found");
        }

        Blog blog = blogOpt.get();

        // 2. Fetch requesting user
        Optional<User> userOpt = userRepository.findById(requestingUserId);
        if (userOpt.isEmpty()) {
            return ApiResponse.failure("User session expired or invalid");
        }

        User currentUser = userOpt.get();

        // 3. Check if Admin OR Owner
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        boolean isOwner = blog.getUserId() == requestingUserId;

        if (!isAdmin && !isOwner) {
            return ApiResponse.failure("Access Denied: Only Admin or Owner can delete");
        }

        // 4. Delete blog
        blogRepository.deleteById(blogId);

        return ApiResponse.success("Blog deleted successfully");
    }

    // ==================== ADMIN DELETE BLOG ====================
    @Transactional
    public ApiResponse adminDeleteBlog(int adminId, int blogId) {

        // 1. Verify admin role
        Optional<User> adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty() || !"ADMIN".equalsIgnoreCase(adminOpt.get().getRole())) {
            return ApiResponse.failure("Access Denied: Must be a verified Administrator");
        }

        // 2. Check blog exists
        if (!blogRepository.existsById(blogId)) {
            return ApiResponse.failure("Blog not found");
        }

        // 3. Delete
        blogRepository.deleteById(blogId);

        return ApiResponse.success("Blog deleted successfully by Admin");
    }
}