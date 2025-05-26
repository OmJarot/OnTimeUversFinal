package com.example.ontimeuvers.model;

public class AdminResponse {

    private String name;

    private String id;

    private String level;

    public AdminResponse() {
    }

    public AdminResponse(String name, String id, String level) {
        this.name = name;
        this.id = id;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
