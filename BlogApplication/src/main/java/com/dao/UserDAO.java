package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.model.User;
import com.demo.DBConnection;

public class UserDAO {

    // Register new user
    public boolean register(User user) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.createConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, user.getUsername());
            pst.setString(2, user.getEmail());
            pst.setString(3, user.getPassword()); // Note: Use hashing in production
            pst.setString(4, "user"); // always user role for registrations

            int rows = pst.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Login user
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection con = DBConnection.createConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, email);
            pst.setString(2, password); // Note: Compare hashed password in production
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Login failed
    }

    // Optional: Fetch user by ID
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection con = DBConnection.createConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<User> fetchAllUsers() {
        List<User> userList = new ArrayList<>();
        // Note: Excludes password for safety, though it's already secured in the login method.
        String sql = "SELECT id, username, email, role, created_at, updated_at FROM users"; 

        try (Connection con = DBConnection.createConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                // Creates a new User object for each row
                userList.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        // We intentionally pass null for password since we didn't fetch it
                        null, 
                        rs.getString("role"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }
    
 // In com.dao.UserDAO.java

    public boolean deleteUser(int userId) {
        
        // --------------------------------------------------------------------------------
        // 🚨 FIX HERE: Replace 'userId' with the actual column name from your blogs table!
        String deleteBlogsQuery = "DELETE FROM blogs WHERE user_id = ?"; // <--- EXAMPLE FIX
        // --------------------------------------------------------------------------------
        
        String deleteUserQuery = "DELETE FROM users WHERE id = ?";
        
        Connection con = null;
        try {
            con = DBConnection.createConnection();
            con.setAutoCommit(false); 

            // Step 1: Delete related blogs
            try (PreparedStatement pstBlogs = con.prepareStatement(deleteBlogsQuery)) {
                pstBlogs.setInt(1, userId);
                pstBlogs.executeUpdate(); 
            }
            
            // Step 2: Delete the user
            try (PreparedStatement pstUser = con.prepareStatement(deleteUserQuery)) {
                pstUser.setInt(1, userId);
                int rowsAffected = pstUser.executeUpdate();
                
                if (rowsAffected > 0) {
                    con.commit(); 
                    return true;
                } else {
                    con.rollback(); 
                    return false;
                }
            } 
            
        } catch (Exception e) {
            e.printStackTrace(); 
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
