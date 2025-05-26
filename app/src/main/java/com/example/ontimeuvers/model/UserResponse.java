package com.example.ontimeuvers.model;

import lombok.Builder;

@Builder
public class UserResponse {

    private String nim;

    private String name;

    private String jurusan;

    public UserResponse(String nim, String name, String jurusan) {
        this.nim = nim;
        this.name = name;
        this.jurusan = jurusan;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }
}
