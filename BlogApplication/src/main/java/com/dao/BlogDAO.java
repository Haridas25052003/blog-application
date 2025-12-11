package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.demo.DBConnection;
import com.model.Blog;
import com.model.User;

public class BlogDAO {

	// ---------------- ADD BLOG ----------------
	public boolean addBlog(Blog blog) {
		String sql = "INSERT INTO blogs (title, content, image_url, user_id, username) VALUES (?, ?, ?, ?, ?)";

		try (Connection con = DBConnection.createConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setString(1, blog.getTitle());
			pst.setString(2, blog.getContent());
			pst.setString(3, blog.getImageUrl());
			pst.setInt(4, blog.getUserId());
			pst.setString(5, blog.getUsername());

			return pst.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// ---------------- HELPER(MAP) ----------------
	private Blog map(ResultSet rs) throws SQLException {
		return new Blog(rs.getInt("id"), rs.getString("title"), rs.getString("content"), rs.getString("image_url"),
				rs.getInt("user_id"), rs.getString("username"), rs.getString("created_at"));
	}

	// ---------------- FETCH ALL ----------------
	public List<Blog> fetchAllBlogs() {
		List<Blog> list = new ArrayList<>();
		String sql = "SELECT * FROM blogs ORDER BY created_at DESC";

		try (Connection con = DBConnection.createConnection();
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				list.add(map(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	// ---------------- FETCH BY USER ----------------
	public List<Blog> fetchBlogsByUser(int userId) {
		List<Blog> list = new ArrayList<>();
		String sql = "SELECT * FROM blogs WHERE user_id = ? ORDER BY created_at DESC";

		try (Connection con = DBConnection.createConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setInt(1, userId);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				list.add(map(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	// ---------------- FETCH BY ID ----------------
	public Blog fetchBlogById(int id) {
		String sql = "SELECT * FROM blogs WHERE id = ?";
		Blog blog = null;

		try (Connection con = DBConnection.createConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				blog = map(rs);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return blog;
	}

	// ---------------- UPDATE BLOG ----------------
	public boolean updateBlog(Blog blog, User currentUser) {

		if (!isAdminOrOwner(blog, currentUser)) {
			System.out.println("ACCESS DENIED: Only Admin or Owner can update.");
			return false;
		}

		String sql = "UPDATE blogs SET title=?, content=?, image_url=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";

		try (Connection con = DBConnection.createConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setString(1, blog.getTitle());
			pst.setString(2, blog.getContent());
			pst.setString(3, blog.getImageUrl());
			pst.setInt(4, blog.getId());

			return pst.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// ---------------- DELETE BLOG ----------------
	public boolean deleteBlog(int blogId, User currentUser) {

		Blog blog = fetchBlogById(blogId);
		if (blog == null)
			return false;

		if (!isAdminOrOwner(blog, currentUser)) {
			System.out.println("ACCESS DENIED: Only Admin or Owner can delete.");
			return false;
		}

		String sql = "DELETE FROM blogs WHERE id=?";

		try (Connection con = DBConnection.createConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setInt(1, blogId);
			return pst.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// ---------------- ROLE CHECK ----------------
	private boolean isAdminOrOwner(Blog blog, User user) {

		if (user == null || user.getRole() == null)
			return false;

		String role = user.getRole().trim();

		return "ADMIN".equalsIgnoreCase(role) || blog.getUserId() == user.getId();
	}

	// In com.dao.BlogDAO.java

	// --- NEW METHOD: ADMIN DELETE BLOG (No ownership check needed here) ---
	public boolean adminDeleteBlog(int blogId) {
		String query = "DELETE FROM blogs WHERE id = ?";
		try (Connection con = DBConnection.createConnection(); PreparedStatement pst = con.prepareStatement(query)) {

			pst.setInt(1, blogId);
			return pst.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
