package com.example.ontimeuvers.model;

public class FindJurusanRequest {

    private String jurusan;

    private Integer angkatan;

    public FindJurusanRequest() {
    }

    public FindJurusanRequest(String jurusan, Integer angkatan) {
        this.jurusan = jurusan;
        this.angkatan = angkatan;
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
}
