package com.example.ontimeuvers.model;

import com.example.ontimeuvers.entity.User;

import java.time.LocalTime;

public class DataTerlambatResponse {

    private User user;

    private LocalTime jam;

    private String matkul;

    public DataTerlambatResponse() {
    }

    public DataTerlambatResponse(User user, LocalTime jam, String matkul) {
        this.user = user;
        this.jam = jam;
        this.matkul = matkul;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalTime getJam() {
        return jam;
    }

    public void setJam(LocalTime jam) {
        this.jam = jam;
    }

    public String getMatkul() {
        return matkul;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }
}
