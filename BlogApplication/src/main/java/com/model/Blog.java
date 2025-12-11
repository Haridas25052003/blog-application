package com.model;

public class Blog {

    private int id;
    private String title;
    private String content;
    private String imageUrl; // NEW: Field to store the image URL
    private int userId;        // owner id
    private String username;  // for display purpose
    private String createdAt;
    private String updatedAt;

    public Blog() {}

    // Updated Constructor to include imageUrl
    public Blog(int id, String title, String content, String imageUrl, int userId, String username, String createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl; // NEW: Initialize imageUrl
        this.userId = userId;
        this.username = username;
        this.createdAt = createdAt;
    }
    
 // ✅ SIMPLE constructor (for ADDING data from frontend)
    public Blog(String title, String content, String imageUrl,
                int userId, String username) {

        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.username = username;
    }

    // getters & setters

    public int getId() {
        return id;
    }
                    
    public void setId(int id) {
        this.id = id;
    }
                    
    public String getTitle() {
        return title;
    }
                    
    public void setTitle(String title) {
        this.title = title;
    }
                    
    public String getContent() {
        return content;
    }
                    
    public void setContent(String content) {
        this.content = content;
    }
    
    // NEW: Getter for imageUrl
    public String getImageUrl() {
        return imageUrl;
    }
    
    // NEW: Setter for imageUrl
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
                    
    public int getUserId() {
        return userId;
    }
                    
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }
                    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreatedAt() {
        return createdAt;
    }
                   
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
                   
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}