package com.example.ontimeuvers.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
public class LoginResponse {

    private String name;

    private String nim;

    private String jurusan;

    private String token;

    private Long expiredAt;

    private String level;

    public LoginResponse() {
    }

    public LoginResponse(String name, String nim, String jurusan, String token, Long expiredAt, String level) {
        this.name = name;
        this.nim = nim;
        this.jurusan = jurusan;
        this.token = token;
        this.expiredAt = expiredAt;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Long expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
