package BlogApplication.BlogApplication.repository;

import BlogApplication.BlogApplication.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {

    // ---- Fetch all blogs by a specific user (for /myblogs) ----
    List<Blog> findByUserIdOrderByCreatedAtDesc(int userId);

    // ---- Fetch all blogs newest first (for /blogs) ----
    List<Blog> findAllByOrderByCreatedAtDesc();

    // ---- Delete all blogs by user (when admin deletes a user) ----
    void deleteByUserId(int userId);

    // ---- Check if blog exists by id and userId (ownership check) ----
    boolean existsByIdAndUserId(int id, int userId);
}