package com.example.ontimeuvers.model;

import com.example.ontimeuvers.entity.Jurusan;

public class AddNewUserResponse {

    private String nim;

    private String name;

    private Jurusan jurusan;

    public AddNewUserResponse() {
    }

    public AddNewUserResponse(String nim, String name, Jurusan jurusan) {
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

    public Jurusan getJurusan() {
        return jurusan;
    }

    public void setJurusan(Jurusan jurusan) {
        this.jurusan = jurusan;
    }
}
