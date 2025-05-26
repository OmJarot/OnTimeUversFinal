package com.example.ontimeuvers.entity;

import java.io.Serializable;

import lombok.Builder;

@Builder
public class User implements Serializable {

    private String nim;

    private String name;

    private String password;

    private String token;

    private Long tokenExpiredAt;

    private String jurusanKode;

    private Jurusan jurusan;

    private Matkul matkul;

    public User() {
    }

    public User(String nim, String name, String password, String token, Long tokenExpiredAt, String jurusanKode, Jurusan jurusan, Matkul matkul) {
        this.nim = nim;
        this.name = name;
        this.password = password;
        this.token = token;
        this.tokenExpiredAt = tokenExpiredAt;
        this.jurusanKode = jurusanKode;
        this.jurusan = jurusan;
        this.matkul = matkul;
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

    public String getJurusanKode() {
        return jurusanKode;
    }

    public void setJurusanKode(String jurusanKode) {
        this.jurusanKode = jurusanKode;
    }

    public Jurusan getJurusan() {
        return jurusan;
    }

    public void setJurusan(Jurusan jurusan) {
        this.jurusan = jurusan;
    }

    public Matkul getMatkul() {
        return matkul;
    }

    public void setMatkul(Matkul matkul) {
        this.matkul = matkul;
    }
}
