package com.example.ontimeuvers.model;

import com.example.ontimeuvers.entity.User;

import java.io.Serializable;

import lombok.Builder;

@Builder
public class InputDataResponse implements Serializable {

    private User user;

    private String tanggal;

    private String jam;

    private String matkul;

    public InputDataResponse() {
    }

    public InputDataResponse(User user, String tanggal, String jam, String matkul) {
        this.user = user;
        this.tanggal = tanggal;
        this.jam = jam;
        this.matkul = matkul;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getMatkul() {
        return matkul;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }
}
