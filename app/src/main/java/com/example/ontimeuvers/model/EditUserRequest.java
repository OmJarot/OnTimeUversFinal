package com.example.ontimeuvers.model;

import com.example.ontimeuvers.entity.Jurusan;

public class EditUserRequest {

    private String nim;
    private String name;

    private String password;

    private Jurusan jurusan;

    public EditUserRequest() {
    }

    public EditUserRequest(String nim, String name, String password, Jurusan jurusan) {
        this.nim = nim;
        this.name = name;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Jurusan getJurusan() {
        return jurusan;
    }

    public void setJurusan(Jurusan jurusan) {
        this.jurusan = jurusan;
    }
}
