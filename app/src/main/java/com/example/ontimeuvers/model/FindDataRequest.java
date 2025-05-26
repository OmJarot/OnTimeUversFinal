package com.example.ontimeuvers.model;

import java.time.LocalDate;

public class FindDataRequest {

    private LocalDate tanggal;

    private String jurusan;

    private Integer angkatan;

    private String name;

    public FindDataRequest() {
    }

    public FindDataRequest(LocalDate tanggal, String jurusan, Integer angkatan, String name) {
        this.tanggal = tanggal;
        this.jurusan = jurusan;
        this.angkatan = angkatan;
        this.name = name;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public Integer getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(Integer angkatan) {
        this.angkatan = angkatan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
