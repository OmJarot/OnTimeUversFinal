package com.example.ontimeuvers.model;

import com.example.ontimeuvers.entity.User;

import java.time.LocalDateTime;

public class FindDataResponse {

    private User user;

    private LocalDateTime waktu;

    private String matkul;

    public FindDataResponse() {
    }

    public FindDataResponse(User user, LocalDateTime waktu, String matkul) {
        this.user = user;
        this.waktu = waktu;
        this.matkul = matkul;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getWaktu() {
        return waktu;
    }

    public void setWaktu(LocalDateTime waktu) {
        this.waktu = waktu;
    }

    public String getMatkul() {
        return matkul;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }
}
