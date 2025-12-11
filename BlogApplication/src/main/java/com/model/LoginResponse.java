package com.model;

public class LoginResponse {
    private int id;
    private String username;
    private String email;
    private String role;

    public LoginResponse(int id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
