package BlogApplication.BlogApplication;

import io.javalin.Javalin;
import io.javalin.http.Context;

import com.dao.UserDAO;
import com.dao.BlogDAO;
import com.model.User;
import com.model.Blog;

import java.util.List;
// import com.google.gson.Gson; // Not needed if relying on bodyAsClass

public class App {

    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();
        BlogDAO blogDAO = new BlogDAO();
        // Gson gson = new Gson(); // Removed Gson dependency, using bodyAsClass

        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> cors.add(it -> it.anyHost()));
        }).start(8080);

        System.out.println("Server started at http://localhost:8080");

        // --- REGISTER --- (No change)
        app.post("/register", ctx -> {
            User user = ctx.bodyAsClass(User.class);
            user.setRole("USER"); 
            boolean success = userDAO.register(user);
            if (success) ctx.result("User registered successfully");
            else ctx.status(400).result("Registration failed. Email might already exist.");
        });

        // --- LOGIN --- (No change)
        app.post("/login", ctx -> {
            User loginUser = ctx.bodyAsClass(User.class);
            User user = userDAO.login(loginUser.getEmail(), loginUser.getPassword());
            if (user != null) {
                ctx.json(user);
            } else {
                ctx.status(401).result("Invalid email or password");
            }
        });

        // --- FETCH ALL BLOGS (PUBLIC) --- (No change)
        app.get("/blogs", ctx -> {
            List<Blog> blogs = blogDAO.fetchAllBlogs();
            ctx.json(blogs);
        });

        // --- FETCH LOGGED-IN USER BLOGS --- (No change)
        app.get("/myblogs/{userId}", ctx -> {
            int userId = Integer.parseInt(ctx.pathParam("userId"));
            List<Blog> blogs = blogDAO.fetchBlogsByUser(userId);
            ctx.json(blogs);
        });

     // --- ADD BLOG (No change) ---
        app.post("/blog/add", ctx -> {
            Blog blog = ctx.bodyAsClass(Blog.class); 
            
            if (blog.getImageUrl() == null || blog.getImageUrl().isEmpty()) {
                ctx.status(400).result("Image upload is required.");
                return;
            }

            boolean success = blogDAO.addBlog(blog);
            ctx.result(success ? "Blog added successfully" : "Failed to add blog");
        });
        
        // --- FETCH BLOG BY ID --- (No change)
        app.get("/blog/{id}", ctx -> {
            int blogId = Integer.parseInt(ctx.pathParam("id"));
            Blog blog = blogDAO.fetchBlogById(blogId);
            if (blog != null) ctx.json(blog);
            else ctx.status(404).result("Blog not found");
        });
        
        // ---------------- UPDATED: UPDATE BLOG (SECURE) ----------------
        // Corrected path to /blog/update (no path param)
        app.put("/blog/update", ctx -> { 
            // 1. Get the Blog object from the body (includes blog ID, fields, and userId from the frontend)
            Blog blog = ctx.bodyAsClass(Blog.class);
            
            int userIdFromRequest = blog.getUserId(); // ID of the user claiming to be the editor

            if (userIdFromRequest == 0 || blog.getId() == 0) {
                ctx.status(400).result("Invalid blog ID or user information.");
                return;
            }

            // 2. 🛡️ SECURITY CHECK: Fetch the User object from the database 
            // This verifies the ID and gets the TRUSTED role from the DB.
            User currentUser = userDAO.getUserById(userIdFromRequest); // REQUIRES fetchUserById in UserDAO

            if (currentUser == null) {
                ctx.status(401).result("User session expired or invalid.");
                return;
            }

            // 3. Perform the update with the secure BlogDAO method
            boolean success = blogDAO.updateBlog(blog, currentUser);
            
            ctx.result(success ? "Blog updated successfully" : "Access denied or update failed.");
            ctx.status(success ? 200 : 403);
        });


        // ---------------- UPDATED: DELETE BLOG (SECURE) ----------------
        app.delete("/blog/delete/{id}", ctx -> {
            int blogId = ctx.pathParamAsClass("id", Integer.class).get();
            
            // 1. Get User ID from the request header (as implemented in frontend)
            String userIdHeader = ctx.header("X-User-Id");
            if (userIdHeader == null) {
                ctx.status(401).result("Authentication missing. Please log in.");
                return;
            }
            int userIdFromRequest = Integer.parseInt(userIdHeader);

            // 2. 🛡️ SECURITY CHECK: Fetch the User object from the database 
            // This verifies the ID and gets the TRUSTED role from the DB.
            User currentUser = userDAO.getUserById(userIdFromRequest); // REQUIRES fetchUserById in UserDAO

            if (currentUser == null) {
                ctx.status(401).result("User session expired or invalid.");
                return;
            }

            // 3. Perform the delete with the secure BlogDAO method
            boolean success = blogDAO.deleteBlog(blogId, currentUser);
            
            ctx.result(success ? "Blog deleted successfully" : "Access denied or delete failed.");
            ctx.status(success ? 200 : 403);
        });
        
        // --- FETCH ALL USERS (ADMIN) ---
        app.get("/users", ctx -> {
            List<User> users = userDAO.fetchAllUsers();
            ctx.json(users);
        });
        
     // In BlogApplication.BlogApplication.App.java (inside the main method)

     // ---------------- NEW ENDPOINT: DELETE ANY BLOG (ADMIN ONLY - SECURE) ----------------
        app.delete("/admin/blog/delete/{id}", ctx -> {
            // 1. Get the Admin's ID from the header (sent by admin.html)
            String adminIdHeader = ctx.header("X-Admin-Id");
            if (adminIdHeader == null) {
                ctx.status(401).result("Authentication missing. Admin ID required.");
                return;
            }
            int adminId = Integer.parseInt(adminIdHeader);

            // 2. 🛡️ SECURITY CHECK: Fetch the User object from the database to verify role
            User adminUser = userDAO.getUserById(adminId); 

            if (adminUser == null || !"ADMIN".equalsIgnoreCase(adminUser.getRole())) {
                ctx.status(403).result("Access Denied: Must be a verified Administrator.");
                return;
            }
            
            // --- Security check passed ---
            
            int blogIdToDelete = ctx.pathParamAsClass("id", Integer.class).get();
            
            // Admin delete logic - calls the simplified DAO method
            // You MUST have the adminDeleteBlog method in your BlogDAO.java
            boolean success = blogDAO.adminDeleteBlog(blogIdToDelete); 
            
            ctx.result(success ? "Blog deleted successfully by Admin." : "Failed to delete blog.");
        });
        
     // In BlogApplication.BlogApplication.App.java

        app.delete("/admin/user/delete/{id}", ctx -> {
            // 1. Get the Admin's ID from the header
            String adminIdHeader = ctx.header("X-Admin-Id");
            if (adminIdHeader == null) {
                ctx.status(401).result("Authentication missing. Admin ID required.");
                return;
            }
            int adminId = Integer.parseInt(adminIdHeader);

            // 2. 🛡️ SECURITY CHECK: Verify the user's role from the database
            User adminUser = userDAO.getUserById(adminId); 

            if (adminUser == null || !"ADMIN".equalsIgnoreCase(adminUser.getRole())) {
                ctx.status(403).result("Access Denied: Must be a verified Administrator.");
                return;
            }
            
            // --- Security check passed ---
            
            int userIdToDelete = ctx.pathParamAsClass("id", Integer.class).get();
            
            // Safety Check: Prevent admin from deleting themselves
            if (adminId == userIdToDelete) {
                ctx.status(400).result("Admin cannot delete their own account via this panel.");
                return;
            }
            
            // 3. DAO Call
            // You MUST have the deleteUser method in your UserDAO.java
            boolean success = userDAO.deleteUser(userIdToDelete); 
            
            ctx.result(success ? "User deleted successfully" : "Failed to delete user.");
        });
        
    }
}