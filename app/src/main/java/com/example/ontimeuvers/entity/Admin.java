package com.example.ontimeuvers.entity;

public class Admin {

    private String id;

    private String level;

    private String name;

    private String password;

    private String token;

    private Long tokenExpiredAt;

    public Admin() {
    }

    public Admin(String id, String level, String name, String password, String token, Long tokenExpiredAt) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.password = password;
        this.token = token;
        this.tokenExpiredAt = tokenExpiredAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getTokenExpiredAt() {
        return tokenExpiredAt;
    }

    public void setTokenExpiredAt(Long tokenExpiredAt) {
        this.tokenExpiredAt = tokenExpiredAt;
    }
}
