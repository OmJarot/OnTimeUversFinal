package com.example.ontimeuvers.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Builder;

@Builder
public class DetailUserResponse {

    private String tanggal;

    private String jam;

    private String matkul;

    public DetailUserResponse() {
    }

    public DetailUserResponse(String tanggal, String jam, String matkul) {
        this.tanggal = tanggal;
        this.jam = jam;
        this.matkul = matkul;
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
